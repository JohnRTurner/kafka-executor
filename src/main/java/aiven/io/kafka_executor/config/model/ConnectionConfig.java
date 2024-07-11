package aiven.io.kafka_executor.config.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.record.CompressionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@NoArgsConstructor
@Data
public class ConnectionConfig {
    /* Basic Connection */
    @Value("${kafka_executor.host}")
    private String host;
    @Value("${kafka_executor.port}")
    private String port;
    @Value("${cert_pass}")
    private String cert_password;
    @Value("${kafka_executor.truststore_location}")
    private String truststore_location;
    @Value("${kafka_executor.keystore_location}")
    private String keystore_location;

    /* Schema Registry */
    @Value("${kafka_executor.schema_registry_host}")
    private String schemaRegistryHost;
    @Value("${kafka_executor.schema_registry_port}")
    private String schemaRegistryPort;
    @Value("${kafka_executor.schema_registry_user}")
    private String schemaRegistryUser;
    @Value("${kafka_executor.schema_registry_password}")
    private String schemaRegistryPassword;

    /* Producer */
    private int producerLingerMs = 1000;
    private int producerBatchSize = 16384;
    private CompressionType compressionType = CompressionType.NONE;
    private long bufferMemory = 33554432L;
    private boolean idempotenceEnabled = false;
    private ACKS acks = ACKS.NONE;

    /* Consumer */
    private int maxPollRecords = 1000;
    private int fetchMinByes = 1024;
    private int fetchMaxWaitMS = 1000;
    private int sessionTimeoutMs = 30000;
    private int heartbeatTimeoutMs = 30000;
    private int autoCommitIntervalMs = 5000;


    public enum KAFKA_TYPE {
        PRODUCER,
        CONSUMER,
        ADMIN
    }

    @Getter
    public enum ACKS {
        ALL("all"),
        LEADER_ONLY("1"),
        NONE("0");

        private final String value;

        ACKS(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public Properties connectionProperties(KAFKA_TYPE kafkaType) {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", host + ":" + port);
        properties.setProperty("security.protocol", "SSL");
        properties.setProperty("ssl.truststore.location", truststore_location);
        properties.setProperty("ssl.truststore.password", cert_password);
        properties.setProperty("ssl.keystore.type", "PKCS12");
        properties.setProperty("ssl.keystore.location", keystore_location);
        properties.setProperty("ssl.keystore.password", cert_password);
        properties.setProperty("ssl.key.password", cert_password);
        if(kafkaType == KAFKA_TYPE.PRODUCER) {
            properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, Integer.toString(producerLingerMs));
            properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(producerBatchSize));
            properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType.name().toLowerCase());
            properties.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, Long.toString(bufferMemory));
            properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,(idempotenceEnabled ? "true" : "false"));
            properties.setProperty(ProducerConfig.ACKS_CONFIG, acks.value);
        }
        if(kafkaType == KAFKA_TYPE.CONSUMER) {
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

            properties.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, Integer.toString(autoCommitIntervalMs));
            properties.setProperty(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, Integer.toString(fetchMinByes)); // Minimum amount of data (in bytes) the server should return for a fetch request
            properties.setProperty(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, Integer.toString(fetchMaxWaitMS)); // Maximum wait time (in ms) the server should block before sending data
            properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, Integer.toString(maxPollRecords)); // Maximum number of records returned in a single call to poll()
            properties.setProperty(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, Integer.toString(heartbeatTimeoutMs));
            properties.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, Integer.toString(sessionTimeoutMs));
        }
        return properties;
    }

    public Properties connectionWithSchemaRegistryProperties(KAFKA_TYPE kafkaType) {
        Properties properties = connectionProperties(kafkaType);
        properties.setProperty("schema.registry.url", "https://" + schemaRegistryHost + ":" + schemaRegistryPort);
        properties.setProperty("basic.auth.credentials.source", "USER_INFO");
        properties.setProperty("basic.auth.user.info", schemaRegistryUser + ":" + schemaRegistryPassword);
        return properties;
    }

    public void loadConfig(ConnectionConfigDTO configDTO){
        this.host  = configDTO.getHost();
        this.port = configDTO.getPort();
        this.cert_password = configDTO.getCert_password();
        this.truststore_location = configDTO.getTruststore_location();
        this.keystore_location = configDTO.getKeystore_location();

        /* Schema Registry */
        this.schemaRegistryHost = configDTO.getSchemaRegistryHost();
        this.schemaRegistryPort  = configDTO.getSchemaRegistryPort();
        this.schemaRegistryUser = configDTO.getSchemaRegistryUser();
        this.schemaRegistryPassword = configDTO.getSchemaRegistryPassword();

        /* Producer */
        this.producerLingerMs = configDTO.getProducerLingerMs();
        this.producerBatchSize = configDTO.getProducerBatchSize();
        this.compressionType = configDTO.getCompressionType();
        this.bufferMemory = configDTO.getBufferMemory();
        this.idempotenceEnabled = configDTO.isIdempotenceEnabled();
        this.acks = configDTO.getAcks();

        /* Consumer */
        this.maxPollRecords = configDTO.getMaxPollRecords();
        this.fetchMinByes = configDTO.getFetchMinByes();
        this.fetchMaxWaitMS = configDTO.getFetchMaxWaitMS();
        this.sessionTimeoutMs = configDTO.getSessionTimeoutMs();
        this.heartbeatTimeoutMs = configDTO.getHeartbeatTimeoutMs();
        this.autoCommitIntervalMs = configDTO.getAutoCommitIntervalMs();

    }

    public ConnectionConfigDTO retConfig() {
        ConnectionConfigDTO configDTO = new ConnectionConfigDTO();

        // Copy values from current object to configDTO
        configDTO.setHost(this.host);
        configDTO.setPort(this.port);
        configDTO.setCert_password(this.cert_password);
        configDTO.setTruststore_location(this.truststore_location);
        configDTO.setKeystore_location(this.keystore_location);

        // Schema Registry
        configDTO.setSchemaRegistryHost(this.schemaRegistryHost);
        configDTO.setSchemaRegistryPort(this.schemaRegistryPort);
        configDTO.setSchemaRegistryUser(this.schemaRegistryUser);
        configDTO.setSchemaRegistryPassword(this.schemaRegistryPassword);

        // Producer
        configDTO.setProducerLingerMs(this.producerLingerMs);
        configDTO.setProducerBatchSize(this.producerBatchSize);
        configDTO.setCompressionType(this.compressionType);
        configDTO.setBufferMemory(this.bufferMemory);
        configDTO.setIdempotenceEnabled(this.idempotenceEnabled);
        configDTO.setAcks(this.acks);

        // Consumer
        configDTO.setMaxPollRecords(this.maxPollRecords);
        configDTO.setFetchMinByes(this.fetchMinByes);
        configDTO.setFetchMaxWaitMS(this.fetchMaxWaitMS);
        configDTO.setSessionTimeoutMs(this.sessionTimeoutMs);
        configDTO.setHeartbeatTimeoutMs(this.heartbeatTimeoutMs);
        configDTO.setAutoCommitIntervalMs(this.autoCommitIntervalMs);

        return configDTO;
    }


}
