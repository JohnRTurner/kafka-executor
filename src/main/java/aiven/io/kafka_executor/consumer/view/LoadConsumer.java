package aiven.io.kafka_executor.consumer.view;

import aiven.io.kafka_executor.config.model.ConnectionConfig;
import aiven.io.kafka_executor.consumer.model.ConsumerStatus;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.data.DataInterface;
import aiven.io.kafka_executor.data.avro.AvroUtils;
import aiven.io.kafka_executor.data.protobuf.ProtobufUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;


@Slf4j
public class LoadConsumer {
    private static final HashMap<String, KafkaConsumer<String, DataInterface> > jsonConsumers = new HashMap<>();
    private static final HashMap<String, KafkaConsumer<String, GenericRecord>> avroConsumers = new HashMap<>();
    private static final HashMap<String, KafkaConsumer<String, DynamicMessage> > protobufConsumers = new HashMap<>();

    private static KafkaConsumer<String, DataInterface> getConsumerJSON(String topic, int server, DataClass dataClass, ConnectionConfig connectionConfig){
        //JSON has schema JSON_NO_SCHEMA does not
        boolean registry = (dataClass.getKafkaFormat() == DataClass.KafkaFormat.JSON);
        String key = topic.concat(Integer.toString(server).concat(Boolean.toString(registry)));
        KafkaConsumer<String, DataInterface> consumer = jsonConsumers.get(key);
        if(consumer == null){
            try (AdminClient adminClient = AdminClient.create(connectionConfig.connectionProperties())) {
                for( String name: adminClient.listTopics().names().get()){
                    if (name.equals(topic)) {
                        Properties properties = (registry)?
                                connectionConfig.connectionWithSchemaRegistryProperties():
                                connectionConfig.connectionProperties();
                        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, (registry)?
                                KafkaJsonSchemaDeserializer.class.getName():
                                JsonDeserializer.class.getName() );
                        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, topic);
                        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
                        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
                        properties.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
                        properties.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
                        properties.setProperty(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "1"); // Minimum amount of data (in bytes) the server should return for a fetch request
                        properties.setProperty(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "500"); // Maximum wait time (in ms) the server should block before sending data
                        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100"); // Maximum number of records returned in a single call to poll()
                        properties.setProperty(JsonDeserializer.TRUSTED_PACKAGES, "*");
                        // this is for no schema
                        properties.setProperty(JsonDeserializer.VALUE_DEFAULT_TYPE, dataClass.getDataInterfaceClass().getName());
                        // this is for schema
                        properties.setProperty(KafkaJsonSchemaDeserializerConfig.JSON_VALUE_TYPE, dataClass.getDataInterfaceClass().getName());
                        properties.setProperty(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, "false");

                        try {
                            consumer = new KafkaConsumer<>(properties);
                            jsonConsumers.put(key, consumer);
                        } catch (Exception e){
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

    private static KafkaConsumer<String, GenericRecord> getConsumerAvro(String topic, int server, ConnectionConfig connectionConfig){
        String key = topic.concat(Integer.toString(server));
        KafkaConsumer<String, GenericRecord> consumer = avroConsumers.get(key);
        if(consumer == null){
            try (AdminClient adminClient = AdminClient.create(connectionConfig.connectionProperties())) {
                for( String name: adminClient.listTopics().names().get()){
                    if (name.equals(topic)) {
                        Properties properties =  connectionConfig.connectionWithSchemaRegistryProperties();
                        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
                        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, topic);
                        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
                        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
                        properties.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
                        properties.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
                        properties.setProperty(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "1"); // Minimum amount of data (in bytes) the server should return for a fetch request
                        properties.setProperty(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "500"); // Maximum wait time (in ms) the server should block before sending data
                        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100"); // Maximum number of records returned in a single call to poll()

                        //properties.setProperty(KafkaAvroDeserializerConfig.SC)
                        try {
                            consumer = new KafkaConsumer<>(properties);
                            avroConsumers.put(key, consumer);
                        } catch (Exception e){
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

    private static KafkaConsumer<String, DynamicMessage> getConsumerProtobuf(String topic, int server, ConnectionConfig connectionConfig){
        String key = topic.concat(Integer.toString(server));
        KafkaConsumer<String, DynamicMessage> consumer = protobufConsumers.get(key);
        if(consumer == null){
            try (AdminClient adminClient = AdminClient.create(connectionConfig.connectionProperties())) {
                for( String name: adminClient.listTopics().names().get()){
                    if (name.equals(topic)) {
                        Properties properties =  connectionConfig.connectionWithSchemaRegistryProperties();
                        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class.getName());
                        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, topic);
                        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
                        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
                        properties.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
                        properties.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
                        properties.setProperty(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "1"); // Minimum amount of data (in bytes) the server should return for a fetch request
                        properties.setProperty(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "500"); // Maximum wait time (in ms) the server should block before sending data
                        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100"); // Maximum number of records returned in a single call to poll()
                        //properties.setProperty(KafkaAvroDeserializerConfig.SC)
                        try {
                            consumer = new KafkaConsumer<>(properties);
                            protobufConsumers.put(key, consumer);
                        } catch (Exception e){
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


    public static void clean(){
        for(String key : jsonConsumers.keySet()){
            KafkaConsumer<String, DataInterface> remove = jsonConsumers.remove(key);
            if(remove != null){
                remove.close();
            }
        }
        for(String key : avroConsumers.keySet()){
            KafkaConsumer<String, GenericRecord> remove = avroConsumers.remove(key);
            if(remove != null){
                remove.close();
            }
        }
        for(String key : protobufConsumers.keySet()){
            KafkaConsumer<String, DynamicMessage> remove = protobufConsumers.remove(key);
            if(remove != null){
                remove.close();
            }
        }
    }

    public static ConsumerStatus generateLoad(String topic, int server, int batchSize, int maxTries, DataClass dataClass, ConnectionConfig connectionConfig) {
        ConsumerStatus status = new ConsumerStatus();
        KafkaConsumer<String, ? extends DataInterface> consumerJSON = null;
        KafkaConsumer<String, ? extends GenericRecord> consumerAvro = null;
        KafkaConsumer<String, ? extends DynamicMessage> consumerProtobuf = null;

        switch (dataClass.getKafkaFormat()) {
            case JSON, JSON_NO_SCHEMA:
                consumerJSON = getConsumerJSON(topic,server,dataClass,connectionConfig);
                break;
            case AVRO:
                consumerAvro = getConsumerAvro(topic,server,connectionConfig);
                break;
            case PROTOBUF:
                consumerProtobuf = getConsumerProtobuf(topic,server,connectionConfig);
        }

        if(consumerJSON == null && consumerAvro == null && consumerProtobuf == null){
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
            log.info("Got {} messages", count);
            status.setError(false);
            status.setErrorMessage("");
            status.setStatus("Success");
            status.setCount(count);
        }
        return status;
    }
}

