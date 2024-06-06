package aiven.io.kafka_executor.data;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public enum DataClass {
    CUSTOMER_JSON_NO_SCHEMA(aiven.io.kafka_executor.data.Customer.class, KafkaFormat.JSON_NO_SCHEMA),
    CUSTOMER_JSON(aiven.io.kafka_executor.data.Customer.class, KafkaFormat.JSON),
    CUSTOMER_AVRO(aiven.io.kafka_executor.data.Customer.class, KafkaFormat.AVRO),
    CUSTOMER_PROTOBUF(aiven.io.kafka_executor.data.Customer.class, KafkaFormat.PROTOBUF),

    WEATHER_IOT_JSON_NO_SCHEMA(aiven.io.kafka_executor.data.WeatherIOT.class, KafkaFormat.JSON_NO_SCHEMA),
    WEATHER_IOT_JSON(aiven.io.kafka_executor.data.WeatherIOT.class, KafkaFormat.JSON),
    WEATHER_IOT_AVRO(aiven.io.kafka_executor.data.WeatherIOT.class, KafkaFormat.AVRO),
    WEATHER_IOT_PROTOBUF(aiven.io.kafka_executor.data.WeatherIOT.class, KafkaFormat.PROTOBUF);

    private static final Logger log = LoggerFactory.getLogger(DataClass.class);

    private final Class<? extends DataInterface> dataInterfaceClass;
    private final KafkaFormat kafkaFormat;
    private final DataInterface dataInterface;

    DataClass(Class<? extends DataInterface> dataInterfaceClass, KafkaFormat kafkaFormat) {
        this.dataInterfaceClass = dataInterfaceClass;
        this.kafkaFormat = kafkaFormat;
        DataInterface tmp = null;
        try {
            tmp = dataInterfaceClass.getConstructor().newInstance();
        } catch (Exception e) {
            LoggerFactory.getLogger(DataClass.class).error("Failed to create instance of {}", dataInterfaceClass.getName(), e);
        }
        this.dataInterface = tmp;
    }

    /* note: All use the schema EXCEPT for JSON_NO_SCHEMA. */
    public enum KafkaFormat {
        AVRO, JSON_NO_SCHEMA, JSON, PROTOBUF
    }
}