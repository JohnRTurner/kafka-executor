package aiven.io.kafka_executor.config.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.record.CompressionType;

@Data
@NoArgsConstructor
public class KafkaConnectionConfigDTO {
    private String host;
    private String port;
    private String cert_password;
    private String truststore_location;
    private String keystore_location;
    private String schemaRegistryHost;
    private String schemaRegistryPort;
    private String schemaRegistryUser;
    private String schemaRegistryPassword;
    private int producerLingerMs;
    private int producerBatchSize;
    private CompressionType compressionType;
    private long bufferMemory;
    private boolean idempotenceEnabled;
    private KafkaConnectionConfig.ACKS acks;
    private int maxPollRecords;
    private int fetchMinByes;
    private int fetchMaxWaitMS;
    private int sessionTimeoutMs;
    private int heartbeatTimeoutMs;
    private int autoCommitIntervalMs;
}
