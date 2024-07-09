package aiven.io.kafka_executor.config.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.record.CompressionType;

@Data
@NoArgsConstructor
public class ConnectionConfigDTO {
    private String host;
    private String port;
    private String cert_password;
    private String truststore_location;
    private String keystore_location;
    private String schemaRegistryHost;
    private String schemaRegistryPort;
    private String schemaRegistryUser;
    private String schemaRegistryPassword;
    private int lingerMs;
    private int batchSize;
    private CompressionType compressionType;
}
