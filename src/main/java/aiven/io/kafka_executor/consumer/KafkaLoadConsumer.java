package aiven.io.kafka_executor.consumer;

import aiven.io.kafka_executor.config.KafkaConnectionConfig;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.data.DataInterface;
import aiven.io.kafka_executor.data.utils.AvroUtils;
import aiven.io.kafka_executor.data.utils.ProtobufUtils;
import com.google.protobuf.DynamicMessage;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializerConfig;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
public class KafkaLoadConsumer {
    private static final HashMap<String, KafkaConsumer<String, DataInterface>> jsonConsumers = new HashMap<>();
    private static final HashMap<String, KafkaConsumer<String, GenericRecord>> avroConsumers = new HashMap<>();
    private static final HashMap<String, KafkaConsumer<String, DynamicMessage>> protobufConsumers = new HashMap<>();

    private static KafkaConsumer<String, DataInterface> getConsumerJSON(String topic, int server, DataClass dataClass, KafkaConnectionConfig kafkaConnectionConfig) {
        //JSON has schema JSON_NO_SCHEMA does not
        boolean registry = (dataClass.getKafkaFormat() == DataClass.KafkaFormat.JSON);
        String key = topic.concat(Integer.toString(server).concat(Boolean.toString(registry)));
        KafkaConsumer<String, DataInterface> consumer = jsonConsumers.get(key);
        if (consumer == null) {
            try (AdminClient adminClient = AdminClient.create(kafkaConnectionConfig.connectionProperties(KafkaConnectionConfig.KAFKA_TYPE.ADMIN))) {
                for (String name : adminClient.listTopics().names().get()) {
                    if (name.equals(topic)) {
                        Properties properties = (registry) ?
                                kafkaConnectionConfig.connectionWithSchemaRegistryProperties(KafkaConnectionConfig.KAFKA_TYPE.CONSUMER) :
                                kafkaConnectionConfig.connectionProperties(KafkaConnectionConfig.KAFKA_TYPE.CONSUMER);
                        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, (registry) ?
                                KafkaJsonSchemaDeserializer.class.getName() :
                                JsonDeserializer.class.getName());
                        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, topic);
                        properties.setProperty(JsonDeserializer.TRUSTED_PACKAGES, "*");
                        // this is for no schema
                        properties.setProperty(JsonDeserializer.VALUE_DEFAULT_TYPE, dataClass.getDataInterfaceClass().getName());
                        // this is for schema
                        properties.setProperty(KafkaJsonSchemaDeserializerConfig.JSON_VALUE_TYPE, dataClass.getDataInterfaceClass().getName());
                        properties.setProperty(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, "false");

                        try {
                            consumer = new KafkaConsumer<>(properties);
                            jsonConsumers.put(key, consumer);
                        } catch (Exception e) {
                            log.error("Error getting consumer for topic {}", topic, e);
                        }
                        break;
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                log.error("Error getting topic {}", topic, e);
            }
        }
        return consumer;
    }

    private static KafkaConsumer<String, GenericRecord> getConsumerAvro(String topic, int server, KafkaConnectionConfig kafkaConnectionConfig) {
        String key = topic.concat(Integer.toString(server));
        KafkaConsumer<String, GenericRecord> consumer = avroConsumers.get(key);
        if (consumer == null) {
            try (AdminClient adminClient = AdminClient.create(kafkaConnectionConfig.connectionProperties(KafkaConnectionConfig.KAFKA_TYPE.ADMIN))) {
                for (String name : adminClient.listTopics().names().get()) {
                    if (name.equals(topic)) {
                        Properties properties = kafkaConnectionConfig.connectionWithSchemaRegistryProperties(KafkaConnectionConfig.KAFKA_TYPE.CONSUMER);
                        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
                        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, topic);

                        //properties.setProperty(KafkaAvroDeserializerConfig.SC)
                        try {
                            consumer = new KafkaConsumer<>(properties);
                            avroConsumers.put(key, consumer);
                        } catch (Exception e) {
                            log.error("Error getting consumer for topic {}", topic, e);
                        }
                        break;
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                log.error("Error getting topic {}", topic, e);
            }
        }
        return consumer;
    }

    private static KafkaConsumer<String, DynamicMessage> getConsumerProtobuf(String topic, int server, KafkaConnectionConfig kafkaConnectionConfig) {
        String key = topic.concat(Integer.toString(server));
        KafkaConsumer<String, DynamicMessage> consumer = protobufConsumers.get(key);
        if (consumer == null) {
            try (AdminClient adminClient = AdminClient.create(kafkaConnectionConfig.connectionProperties(KafkaConnectionConfig.KAFKA_TYPE.ADMIN))) {
                for (String name : adminClient.listTopics().names().get()) {
                    if (name.equals(topic)) {
                        Properties properties = kafkaConnectionConfig.connectionWithSchemaRegistryProperties(KafkaConnectionConfig.KAFKA_TYPE.CONSUMER);
                        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class.getName());
                        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, topic);
                        //properties.setProperty(KafkaAvroDeserializerConfig.SC)
                        try {
                            consumer = new KafkaConsumer<>(properties);
                            protobufConsumers.put(key, consumer);
                        } catch (Exception e) {
                            log.error("Error getting consumer for topic {}", topic, e);
                        }
                        break;
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                log.error("Error getting topic {}", topic, e);
            }
        }
        return consumer;
    }


    public static void clean() {
        Iterator<Map.Entry<String, KafkaConsumer<String, DataInterface>>> jsonIterator = jsonConsumers.entrySet().iterator();
        while (jsonIterator.hasNext()) {
            KafkaConsumer<String, DataInterface> consumer = jsonIterator.next().getValue();
            try {
                consumer.close();
            } catch (Exception e) {
                log.error("Error flushing consumer for topic {}", jsonIterator.next().getKey(), e);
            }
            jsonIterator.remove();
        }

        Iterator<Map.Entry<String, KafkaConsumer<String, GenericRecord>>> avroIterator = avroConsumers.entrySet().iterator();
        while (avroIterator.hasNext()) {
            KafkaConsumer<String, GenericRecord> consumer = avroIterator.next().getValue();
            try {
                consumer.close();
            } catch (Exception e) {
                log.error("Error flushing consumer for topic {}", jsonIterator.next().getKey(), e);
            }
            avroIterator.remove();
        }

        Iterator<Map.Entry<String, KafkaConsumer<String, DynamicMessage>>> protoIterator = protobufConsumers.entrySet().iterator();
        while (protoIterator.hasNext()) {
            KafkaConsumer<String, DynamicMessage> consumer = protoIterator.next().getValue();
            try {
                consumer.close();
            } catch (Exception e) {
                log.error("Error flushing consumer for topic {}", jsonIterator.next().getKey(), e);
            }
            protoIterator.remove();
        }
    }

    public static ConsumerStatus generateLoad(String topic, int server, int batchSize, int maxTries, DataClass dataClass, KafkaConnectionConfig kafkaConnectionConfig) {
        ConsumerStatus status = new ConsumerStatus();
        KafkaConsumer<String, ? extends DataInterface> consumerJSON = null;
        KafkaConsumer<String, ? extends GenericRecord> consumerAvro = null;
        KafkaConsumer<String, ? extends DynamicMessage> consumerProtobuf = null;

        switch (dataClass.getKafkaFormat()) {
            case JSON, JSON_NO_SCHEMA:
                consumerJSON = getConsumerJSON(topic, server, dataClass, kafkaConnectionConfig);
                break;
            case AVRO:
                consumerAvro = getConsumerAvro(topic, server, kafkaConnectionConfig);
                break;
            case PROTOBUF:
                consumerProtobuf = getConsumerProtobuf(topic, server, kafkaConnectionConfig);
        }

        if (consumerJSON == null && consumerAvro == null && consumerProtobuf == null) {
            log.error("Error getting consumer for topic {}", topic);
            status.setError(true);
            status.setErrorMessage("Error getting consumer for topic ".concat(topic));
            status.setStatus("Fail");
        } else {
            int count = 0;
            int loop = 0;
            switch (dataClass.getKafkaFormat()) {
                case JSON, JSON_NO_SCHEMA:
                    consumerJSON.subscribe(List.of(topic));
                    while (count < batchSize && loop < maxTries) {
                        loop++;
                        ConsumerRecords<String, ? extends DataInterface> messages = consumerJSON.poll(Duration.ofMillis(100));
                        count += messages.count();
                        boolean isFirst = true;
                        for (ConsumerRecord<String, ? extends DataInterface> record : messages) {
                            if (isFirst) {
                                log.debug("Class: {} Id: {} Message: {}", record.value().getClass().getName(),
                                        (record.value()).getId(), record.value().toString());
                                isFirst = false;
                            }
                            log.debug("Class: {} Message: {}", record.value().getClass().getName(), (record.value()).toString());
                        }
                    }
                    break;
                case AVRO:
                    consumerAvro.subscribe(List.of(topic));
                    while (count < batchSize && loop < maxTries) {
                        loop++;
                        ConsumerRecords<String, ? extends GenericRecord> messages = consumerAvro.poll(Duration.ofMillis(100));
                        count += messages.count();
                        boolean isFirst = true;
                        for (ConsumerRecord<String, ? extends GenericRecord> record : messages) {
                            if (isFirst) {
                                isFirst = false;
                                DataInterface dataInterface = AvroUtils.generateData(record.value(), dataClass);
                                if (dataInterface != null) {
                                    log.debug("Class: {} Id: {} Message: {}", dataInterface.getClass().getName(),
                                            dataInterface.getId(), dataInterface);
                                }
                            }
                            log.debug("Class: {} Message: {}", record.value().getClass().getName(), record.value().toString());
                        }
                    }
                    break;
                case PROTOBUF:
                    consumerProtobuf.subscribe(List.of(topic));
                    while (count < batchSize && loop < maxTries) {
                        loop++;
                        ConsumerRecords<String, ? extends DynamicMessage> messages = consumerProtobuf.poll(Duration.ofMillis(100));
                        count += messages.count();
                        boolean isFirst = true;
                        for (ConsumerRecord<String, ? extends DynamicMessage> record : messages) {
                            if (isFirst) {
                                isFirst = false;
                                DataInterface dataInterface = ProtobufUtils.generateData(record.value(), dataClass);
                                if (dataInterface != null) {
                                    log.debug("Class: {} Id: {} Message: {}", dataInterface.getClass().getName(),
                                            dataInterface.getId(), dataInterface);
                                }
                            }
                            log.debug("Class: {} Message: {}", record.value().getClass().getName(), (record.value()).toString());
                        }
                    }
                    break;
            }
            log.debug("Got {} messages", count);
            status.setError(false);
            status.setErrorMessage("");
            status.setStatus("Success");
            status.setCount(count);
        }
        return status;
    }
}

