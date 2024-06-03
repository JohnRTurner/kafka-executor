package aiven.io.kafka_executor.config.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@NoArgsConstructor
@Data
public class ConnectionConfig {
    private String host = "kafka-test-bus4-jturner-demo.c.aivencloud.com";
    private int port = 15378;

    //private String topic_name = "testTopic";
    @Value("${kafka_executor.truststore_password}")
    private String truststore_password;
    @Value("${kafka_executor.keystore_password}")
    private String keystore_password;
    @Value("${kafka_executor.key_password}")
    private String key_password;
    @Value("${kafka_executor.truststore_location}")
    private String truststore_location;
    @Value("${kafka_executor.keystore_location}")
    private String keystore_location;
    @Value("${kafka_executor.schema_registry_host}")
    private String schemaRegistryHost;
    @Value("${kafka_executor.schema_registry_port}")
    private String schemaRegistryPort;
    @Value("${kafka_executor.schema_registry_user}")
    private String schemaRegistryUser;
    @Value("${kafka_executor.schema_registry_password}")
    private String schemaRegistryPassword;


    public Properties connectionProperties(){
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", host + ":" + port);
        properties.setProperty("security.protocol", "SSL");
        properties.setProperty("ssl.truststore.location", truststore_location);
        properties.setProperty("ssl.truststore.password", truststore_password);
        properties.setProperty("ssl.keystore.type", "PKCS12");
        properties.setProperty("ssl.keystore.location", keystore_location);
        properties.setProperty("ssl.keystore.password", key_password);
        properties.setProperty("ssl.key.password", key_password);
        return properties;
    }

    public Properties connectionWithSchemaRegistryProperties(){
        Properties properties = connectionProperties();
        properties.setProperty("schema.registry.url", "https://" + schemaRegistryHost + ":" + schemaRegistryPort);
        properties.setProperty("basic.auth.credentials.source", "USER_INFO");
        properties.setProperty("basic.auth.user.info", schemaRegistryUser + ":" + schemaRegistryPassword);

        return properties;
    }


}
