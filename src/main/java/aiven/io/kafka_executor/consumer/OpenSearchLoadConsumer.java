package aiven.io.kafka_executor.consumer;

import aiven.io.kafka_executor.config.OpenSearchConnectionConfig;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.data.DataInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.get.*;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;

import java.io.IOException;

@Slf4j
public class OpenSearchLoadConsumer {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static void clean(OpenSearchConnectionConfig connectionConfig) {
        connectionConfig.closeClient();
    }

    public static ConsumerStatus generateLoadPrim(String indexName, DataClass dataClass, int batchSize,
                                                  long startId, long endId,
                                                  OpenSearchConnectionConfig connectionConfig) {
        ConsumerStatus status = new ConsumerStatus();
        int count = 0;
        int notFound = 0;

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

        if (batchSize < 2) {
            for (long i = startId; i <= endId; i++) {
                try {
                    GetResponse getResponse = client.get(new GetRequest(indexName.toLowerCase(), Long.toString(i)), RequestOptions.DEFAULT);
                    if (getResponse.isExists()) {
                        dataInterface = objectMapper.readValue(getResponse.getSourceAsString(), dataClass.getDataInterfaceClass());
                        count++;
                        if (i == startId) {
                            log.warn("My datainterface: {}", dataInterface.toString());
                        }
                    } else {
                        notFound++;
                    }
                } catch (Exception e) {
                    log.error("Failed to get data from {}", indexName.toLowerCase(), e);
                    notFound++;
                    status.setCount(count);
                    status.setError(true);
                    status.setErrorMessage("Did not find: " + notFound);
                    status.setStatus("Failed");
                    return status;

                }
            }
        } else {
            MultiGetRequest request = new MultiGetRequest();
            int x = 0;
            for (long i = startId; i <= endId; i++) {
                request.add(new MultiGetRequest.Item(indexName.toLowerCase(), Long.toString(i)));
                x++;
                if (x >= batchSize) {
                    try {
                        MultiGetResponse response = client.mget(request, RequestOptions.DEFAULT);
                        for (MultiGetItemResponse itemResponse : response.getResponses()) {
                            if (itemResponse.getResponse().isExists()) {
                                String sourceAsString = itemResponse.getResponse().getSourceAsString();
                                dataInterface = objectMapper.readValue(sourceAsString, dataClass.getDataInterfaceClass());
                                count++;
                                if (count == 1) {
                                    log.warn("My dataInterface: {}", dataInterface.toString());
                                }
                            } else {
                                notFound++;
                            }
                        }
                        request = new MultiGetRequest();
                        x = 0;
                    } catch (Exception e) {
                        log.error("Failed to get data from {}", indexName.toLowerCase(), e);
                        notFound++;
                        status.setCount(count);
                        status.setError(true);
                        status.setErrorMessage("Did not find: " + notFound);
                        status.setStatus("Failed");
                        return status;
                    }
                }
            }
            if (x > 0) {
                try {
                    MultiGetResponse response = client.mget(request, RequestOptions.DEFAULT);
                    for (MultiGetItemResponse itemResponse : response.getResponses()) {
                        if (itemResponse.getResponse().isExists()) {
                            String sourceAsString = itemResponse.getResponse().getSourceAsString();
                            dataInterface = objectMapper.readValue(sourceAsString, dataClass.getDataInterfaceClass());
                            count++;
                            if (count == 1) {
                                log.warn("My dataInterface: {}", dataInterface.toString());
                            }
                        } else {
                            notFound++;
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to get data from {}", indexName.toLowerCase(), e);
                    notFound++;
                    status.setCount(count);
                    status.setError(true);
                    status.setErrorMessage("Did not find: " + notFound);
                    status.setStatus("Failed");
                    return status;
                }
            }
        }

        log.debug("Found {} messages, Didn't find {} messages", count, notFound);
        status.setCount(count);
        status.setError(false);
        status.setErrorMessage("Did not find: " + notFound);
        status.setStatus("Success");
        return status;
    }
}
