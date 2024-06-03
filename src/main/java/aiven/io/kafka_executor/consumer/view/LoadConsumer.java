package aiven.io.kafka_executor.consumer.view;

import aiven.io.kafka_executor.config.model.ConnectionConfig;
import aiven.io.kafka_executor.consumer.model.ConsumerStatus;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.data.DataInterface;
import com.google.protobuf.DynamicMessage;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializerConfig;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class LoadConsumer {
    private static final HashMap<String, KafkaConsumer<String, DataInterface> > jsonConsumers = new HashMap<>();
    private static final HashMap<String, KafkaConsumer<String, GenericRecord>> avroConsumers = new HashMap<>();
    private static final HashMap<String, KafkaConsumer<String, DynamicMessage> > protobufConsumers = new HashMap<>();

    private final ConnectionConfig connectionConfig;

    public LoadConsumer(ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
    }

    private KafkaConsumer<String, DataInterface> getConsumerJSON(String topic, int server, DataClass dataClass){
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

    private KafkaConsumer<String, GenericRecord> getConsumerAvro(String topic, int server, DataClass dataClass){
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

    private KafkaConsumer<String, DynamicMessage> getConsumerProtobuf(String topic, int server, DataClass dataClass){
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


    public void clean(){
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



    public ConsumerStatus generateLoad(String topic, int server, int batchSize, int maxTries, DataClass dataClass) {
        ConsumerStatus status = new ConsumerStatus();
        KafkaConsumer<String, ? extends DataInterface> consumerJSON = null;
        KafkaConsumer<String, ? extends GenericRecord> consumerAvro = null;
        KafkaConsumer<String, ? extends DynamicMessage> consumerProtobuf = null;
        DataInterface df = null;
        if(dataClass.getKafkaFormat()== DataClass.KafkaFormat.PROTOBUF ||
        dataClass.getKafkaFormat()== DataClass.KafkaFormat.AVRO) {
            try {
                df = dataClass.getDataInterfaceClass().getConstructor().newInstance();
            } catch (Exception e) {
                log.error("Error instantiating data interface", e);
                status.setError(true);
                status.setErrorMessage("Error getting consumer for topic ".concat(topic));
                status.setStatus("Fail");
                return status;
            }
        }

        switch (dataClass.getKafkaFormat()) {
            case JSON, JSON_NO_SCHEMA:
                consumerJSON = getConsumerJSON(topic,server,dataClass);
                break;
            case AVRO:
                consumerAvro = getConsumerAvro(topic,server,dataClass);
                break;
            case PROTOBUF:
                consumerProtobuf = getConsumerProtobuf(topic,server,dataClass);
        }

        if(consumerJSON == null && consumerAvro == null && consumerProtobuf == null){
            log.error("Error getting consumer for topic {}", topic);
            status.setError(true);
            status.setErrorMessage("Error getting consumer for topic ".concat(topic));
            status.setStatus("Fail");
        } else {
            AtomicInteger count = new AtomicInteger(0);
            int loop = 0;
            switch (dataClass.getKafkaFormat()) {
                case JSON, JSON_NO_SCHEMA:
                    consumerJSON.subscribe(List.of(topic));
                    while (count.get() < batchSize && loop < maxTries) {
                        loop++;
                        ConsumerRecords<String, ? extends DataInterface> messages = consumerJSON.poll(Duration.ofMillis(100));
                        messages.forEach(message -> {
                            if(count.get() < 1){
                                log.info("Class: {} Id: {} Message: {}", message.value().getClass().getName(),
                                        (message.value()).getId(), message.value().toString());
                            }
                            log.debug("Class: {} Message: {}",message.value().getClass().getName(), (message.value()).toString());
                            count.addAndGet(1);
                        });
                    }
                    break;
                case AVRO:
                    consumerAvro.subscribe(List.of(topic));
                    while (count.get() < batchSize && loop < maxTries) {
                        loop++;
                        ConsumerRecords<String, ? extends GenericRecord> messages = consumerAvro.poll(Duration.ofMillis(100));
                        DataInterface finalDf = df;
                        messages.forEach(message -> {
                            if(count.get() < 1){
                                DataInterface dataInterface = finalDf.generateData((GenericData.Record) message.value());
                                log.info("Class: {} Id: {} Message: {}", dataInterface.getClass().getName(),
                                        dataInterface.getId(), dataInterface);
                            }
                            log.debug("Class: {} Message: {}",message.value().getClass().getName(), (message.value()).toString());
                            count.addAndGet(1);
                        });
                    }
                    break;
                case PROTOBUF:
                    consumerProtobuf.subscribe(List.of(topic));
                    while (count.get() < batchSize && loop < maxTries) {
                        loop++;
                        ConsumerRecords<String, ? extends DynamicMessage> messages = consumerProtobuf.poll(Duration.ofMillis(100));
                        DataInterface finalDf = df;
                        messages.forEach(message -> {
                            if(count.get() < 1){
                                DataInterface dataInterface = finalDf.generateData((DynamicMessage) message.value());
                                log.info("Class: {} Id: {} Message: {}", dataInterface.getClass().getName(),
                                        dataInterface.getId(), dataInterface);
                            }
                            log.debug("Class: {} Message: {}",message.value().getClass().getName(), (message.value()).toString());
                            count.addAndGet(1);
                        });
                    }
                    break;
            }
            log.info("Got {} messages", count);
            status.setError(false);
            status.setErrorMessage("");
            status.setStatus("Success");
            status.setCount(count.get());
        }
        return status;
    }
}

