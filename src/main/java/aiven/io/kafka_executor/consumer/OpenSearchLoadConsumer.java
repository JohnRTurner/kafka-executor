package aiven.io.kafka_executor.consumer;

import aiven.io.kafka_executor.config.OpenSearchConnectionConfig;
import aiven.io.kafka_executor.data.DataClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenSearchLoadConsumer {
    public static void clean(OpenSearchConnectionConfig connectionConfig) {
        connectionConfig.closeClient();
    }

    public static ConsumerStatus generateLoadPrim(String indexName, DataClass dataClass, int batchSize,
                                                  long startId, long endId,
                                                  OpenSearchConnectionConfig connectionConfig) {
        ConsumerStatus status = new ConsumerStatus();
        int count = 0;


        log.debug("Got {} messages", count);
        status.setError(false);
        status.setErrorMessage("");
        status.setStatus("Success");
        return status;
    }

}
