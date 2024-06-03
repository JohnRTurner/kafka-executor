package aiven.io.kafka_executor.data;

import lombok.Getter;

@Getter
public enum DataClass{
    CUSTOMER_JSON_NO_SCHEMA(aiven.io.kafka_executor.data.Customer.class, KafkaFormat.JSON_NO_SCHEMA),
    CUSTOMER_JSON(aiven.io.kafka_executor.data.Customer.class, KafkaFormat.JSON),
    CUSTOMER_AVRO(aiven.io.kafka_executor.data.Customer.class, KafkaFormat.AVRO),
    CUSTOMER_PROTOBUF(aiven.io.kafka_executor.data.Customer.class, KafkaFormat.PROTOBUF),

    WEATHER_IOT_JSON_NO_SCHEMA(aiven.io.kafka_executor.data.WeatherIOT.class, KafkaFormat.JSON_NO_SCHEMA),
    WEATHER_IOT_JSON(aiven.io.kafka_executor.data.WeatherIOT.class, KafkaFormat.JSON),
    WEATHER_IOT_AVRO(aiven.io.kafka_executor.data.WeatherIOT.class, KafkaFormat.AVRO),
    WEATHER_IOT_PROTOBUF(aiven.io.kafka_executor.data.WeatherIOT.class, KafkaFormat.PROTOBUF);

    private final Class<? extends DataInterface> dataInterfaceClass;
    private final KafkaFormat kafkaFormat;

    DataClass(Class<? extends DataInterface> dataInterfaceClass, KafkaFormat kafkaFormat) {
            this.dataInterfaceClass = dataInterfaceClass;
            this.kafkaFormat = kafkaFormat;
    }


    public enum KafkaFormat{
        /* note: all use the schema EXCEPT for JSON... */
        AVRO, JSON_NO_SCHEMA, JSON, PROTOBUF
    }

}
