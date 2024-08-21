package aiven.io.kafka_executor.producer.view;

import aiven.io.kafka_executor.config.model.OpensearchConnectionConfig;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.data.DataInterface;
import aiven.io.kafka_executor.data.json.JsonUtils;
import aiven.io.kafka_executor.producer.model.ProducerStatus;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.support.master.AcknowledgedResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.CreateIndexResponse;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.core.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Collection;

@Slf4j
public class OpensearchLoadProducer {

    public static void clean(OpensearchConnectionConfig connectionConfig) {
        connectionConfig.closeClient();
    }

    public static String[] getIndexes(OpensearchConnectionConfig connectionConfig) {
        RestHighLevelClient client = connectionConfig.getClient();
        GetIndexRequest request = new GetIndexRequest("*");

        // Get the index response
        try {
            return client.indices().get(request, RequestOptions.DEFAULT).getIndices();
        } catch (IOException e) {
            log.error("Error getting Index List", e);
            return new String[]{"Error Getting Index List"};
        }
    }

    public static boolean createIndexes(Collection<String> indexes, int shards, int replicas, int refreshSeconds,
                                        OpensearchConnectionConfig connectionConfig) {
        RestHighLevelClient client = connectionConfig.getClient();
        for (String index : indexes) {

            XContentBuilder xContentBuilder = null;
            try {
                DataInterface dataInterface = DataClass.valueOf(index).getDataInterface();
                xContentBuilder = dataInterface.retOpensearchSchema();
                log.info("Creating index {} {}", dataInterface.getClass().getSimpleName(), xContentBuilder);
            } catch (Exception e) {
                log.error("Error getting Index Schema", e);
            }

            CreateIndexRequest request = new CreateIndexRequest(index.toLowerCase());
            if (xContentBuilder != null) request.mapping(xContentBuilder);

            request.settings(Settings.builder()
                    .put("index.number_of_shards", shards)
                    .put("index.number_of_replicas", replicas)
                    .put("index.refresh_interval", Integer.toString(refreshSeconds).concat("s"))
            );

            try {
                CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
                if (!createIndexResponse.isAcknowledged()) {
                    log.error("Error creating index {} returned unacknowledged.", index.toLowerCase());
                    return false;
                }
            } catch (Exception e) {
                log.error("Error creating index {}", index.toLowerCase(), e);
                return false;
            }

        }
        return true;
    }

    public static boolean deleteIndexes(Collection<String> indexes,
                                        OpensearchConnectionConfig connectionConfig) {
        RestHighLevelClient client = connectionConfig.getClient();
        for (String index : indexes) {
            try {
                DeleteIndexRequest request = new DeleteIndexRequest(index.toLowerCase());
                AcknowledgedResponse deleteResponse = client.indices().delete(request, RequestOptions.DEFAULT);

                if (!deleteResponse.isAcknowledged()) {
                    log.error("Error deleting index {} returned unacknowledged.", index.toLowerCase());
                    return false;
                }
            } catch (Exception e) {
                log.error("Error deleting index {}", index.toLowerCase(), e);
                return false;
            }

        }
        return true;
    }


    public static ProducerStatus generateLoad(String indexName, DataClass dataClass, int batchSize,
                                              long startId, int correlatedStartIdInc, int correlatedEndIdInc,
                                              OpensearchConnectionConfig connectionConfig) {
        ProducerStatus status = new ProducerStatus();

        DataInterface dataInterface = dataClass.getDataInterface();

        if (dataClass.getKafkaFormat() != DataClass.KafkaFormat.JSON) {
            status.setError(true);
            status.setErrorMessage("Only JSON data classes are supported");
            status.setStatus("Fail");
            status.setCount(0);
            return status;
        }
        RestHighLevelClient client = connectionConfig.getClient();
        try {
            if (client == null || !client.ping(RequestOptions.DEFAULT)) {
                status.setError(true);
                status.setErrorMessage("Failed to connect to OpenSearch");
                status.setStatus("Fail");
                status.setCount(0);
                return status;
            }
        } catch (IOException e) {
            log.error("Failed to connect to OpenSearch", e);
            status.setError(true);
            status.setErrorMessage("Failed to connect to OpenSearch");
            status.setStatus("Fail");
            status.setCount(0);
            return status;

        }

        int correlatedRange = correlatedEndIdInc - correlatedStartIdInc + 1;
        if (correlatedRange < 1) {
            correlatedStartIdInc = -1;
        }

        BulkRequest bulkRequest = new BulkRequest();
        for (int i = 0; i < batchSize; i++) {
            DataInterface dataInterface1 = dataInterface.generateData((startId < 0) ? -1 : startId + i,
                    (correlatedStartIdInc < 0) ? -1 : correlatedStartIdInc + (i % correlatedRange));
            IndexRequest indexRequest = new IndexRequest(indexName.toLowerCase()).
                    id(Long.toString(dataInterface1.getId())).
                    source(JsonUtils.convertToJson(dataInterface1), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        try {
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (bulkResponse.hasFailures()) {
                log.error("Bulk response: {}", bulkResponse.buildFailureMessage());
                status.setError(true);
                status.setErrorMessage("Bulk Operation to OpenSearch failed with: " +
                        bulkResponse.buildFailureMessage());
                status.setStatus("Fail");
                status.setCount(0);
                return status;
            }
        } catch (IOException e) {
            log.error("OpenSearch Bulk produced IO error", e);
            status.setError(true);
            status.setErrorMessage("Bulk Operation to OpenSearch Failed with: " +
                    e.getMessage());
            status.setStatus("Fail");
            status.setCount(0);
            return status;
        }

        log.debug("Completed sending {} message for indexName {} of type {}!", batchSize, indexName.toLowerCase(), dataInterface.getClass().getSimpleName());
        status.setError(false);
        status.setErrorMessage("");
        status.setStatus("Success");
        status.setCount(batchSize);
        return status;
    }
}
