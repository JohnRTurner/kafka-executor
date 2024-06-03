package aiven.io.kafka_executor.producer.view;

import aiven.io.kafka_executor.config.model.ConnectionConfig;

import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.data.DataInterface;

import aiven.io.kafka_executor.data.avro.AvroUtils;
import aiven.io.kafka_executor.data.protobuf.ProtobufUtils;
import aiven.io.kafka_executor.producer.model.ProducerStatus;
import com.google.protobuf.DynamicMessage;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaJsonSerializer;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
import lombok.extern.slf4j.Slf4j;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;


import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;




@Component
@Slf4j
public class LoadProducer {
    private static final HashMap<String, KafkaProducer<String, DataInterface> > jsonProducers = new HashMap<>();
    private static final HashMap<String, KafkaProducer<String, GenericRecord> > avroProducers = new HashMap<>();
    private static final HashMap<String, KafkaProducer<String, DynamicMessage> > protobufProducers = new HashMap<>();



    //private static final Faker faker = new Faker();

    private final ConnectionConfig connectionConfig;
    public LoadProducer(ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
    }

    public ListTopicsResult getTopics(){
        try (AdminClient adminClient = AdminClient.create(connectionConfig.connectionProperties())) {
            return adminClient.listTopics();
        }catch (Exception e) {
            log.error("Error getting topic list", e);
            return null;
        }
    }

    public CreateTopicsResult createTopics(String[] topics, int partitions, short replication){
        try (AdminClient adminClient = AdminClient.create(connectionConfig.connectionProperties())) {
            Collection<NewTopic> newTopics = new ArrayList<>();
            for(String topic : topics){
                newTopics.add(new NewTopic(topic, partitions, replication));
            }
            return adminClient.createTopics(newTopics);
        }catch (Exception e) {
            log.error("Error creating topics", e);
            return null;
        }
    }

    private KafkaProducer<String, DataInterface> getProducerJSON(String topic, int server, boolean registry){
        String key = topic.concat(Integer.toString(server).concat(Boolean.toString(registry)));
        KafkaProducer<String, DataInterface> producer = jsonProducers.get(key);
        if(producer == null){
            try (AdminClient adminClient = AdminClient.create(connectionConfig.connectionProperties())) {
                for( String name: adminClient.listTopics().names().get()){
                    if (name.equals(topic)) {
                        Properties properties = (registry)?
                                connectionConfig.connectionWithSchemaRegistryProperties():
                                connectionConfig.connectionProperties();
                        properties.setProperty("key.serializer", StringSerializer.class.getName());
                        properties.setProperty("value.serializer", (registry)?
                                KafkaJsonSchemaSerializer.class.getName():
                                KafkaJsonSerializer.class.getName());
                        try {
                            producer = new KafkaProducer<>(properties);
                            jsonProducers.put(key, producer);
                        } catch (Exception e){
                            log.error("Error getting producer for topic {}", topic, e);
                        }
                        break;
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                log.error("Error getting topic {}", topic, e);
            }
        }
        return producer;
    }

    private KafkaProducer<String, DynamicMessage> getProducerProtobuf(String topic, int server){
        String key = topic.concat(Integer.toString(server).concat("True"));
        KafkaProducer<String, DynamicMessage> producer = protobufProducers.get(key);
        if(producer == null){
            try (AdminClient adminClient = AdminClient.create(connectionConfig.connectionProperties())) {
                for( String name: adminClient.listTopics().names().get()){
                    if (name.equals(topic)) {
                        Properties properties = connectionConfig.connectionWithSchemaRegistryProperties();
                        properties.setProperty("key.serializer", StringSerializer.class.getName());
                        properties.setProperty("value.serializer", KafkaProtobufSerializer.class.getName());
                        try {
                            producer = new KafkaProducer<>(properties);
                            protobufProducers.put(key, producer);
                        } catch (Exception e){
                            log.error("Error getting producer for topic {}", topic, e);
                        }
                        break;
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                log.error("Error getting topic {}", topic, e);
            }
        }
        return producer;
    }


    private KafkaProducer<String, GenericRecord> getProducerAvro(String topic, int server){
        String key = topic.concat(Integer.toString(server).concat("False"));
        KafkaProducer<String, GenericRecord> producer = avroProducers.get(key);
        if(producer == null){
            try (AdminClient adminClient = AdminClient.create(connectionConfig.connectionProperties())) {
                for( String name: adminClient.listTopics().names().get()){
                    if (name.equals(topic)) {
                        Properties properties = connectionConfig.connectionWithSchemaRegistryProperties();
                        properties.setProperty("key.serializer", StringSerializer.class.getName());
                        properties.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
                        try {
                            producer = new KafkaProducer<>(properties);
                            avroProducers.put(key, producer);
                        } catch (Exception e){
                            log.error("Error getting producer for topic {}", topic, e);
                        }
                        break;
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                log.error("Error getting topic {}", topic, e);
            }
        }
        return producer;
    }

    public void clean(){
        for(String key : jsonProducers.keySet()){
            KafkaProducer<String, DataInterface> remove = jsonProducers.remove(key);
            if(remove != null){
                remove.flush();
                remove.close();
            }
        }
        for(String key : avroProducers.keySet()){
            KafkaProducer<String, GenericRecord> remove = avroProducers.remove(key);
            if(remove != null){
                remove.flush();
                remove.close();
            }
        }
        for(String key : protobufProducers.keySet()){
            KafkaProducer<String, DynamicMessage> remove = protobufProducers.remove(key);
            if(remove != null){
                remove.flush();
                remove.close();
            }
        }
    }

    public ProducerStatus generateLoad(String topic, int server, DataClass dataClass, int batchSize, long startId, int relativeItem) {
        ProducerStatus status = new ProducerStatus();
        KafkaProducer<String, GenericRecord> producerAvro = null;
        KafkaProducer<String, DataInterface> producerJSON = null;
        KafkaProducer<String, DynamicMessage> producerProtobuf = null;
        DataInterface dataInterface;

        if (dataClass.getKafkaFormat() == DataClass.KafkaFormat.AVRO) {
            producerAvro = this.getProducerAvro(topic, server);
        } else if (dataClass.getKafkaFormat() == DataClass.KafkaFormat.JSON_NO_SCHEMA) {
            producerJSON = this.getProducerJSON(topic, server,false);
        } else if (dataClass.getKafkaFormat() == DataClass.KafkaFormat.JSON) {
            producerJSON = this.getProducerJSON(topic, server, true);
        } else if (dataClass.getKafkaFormat() == DataClass.KafkaFormat.PROTOBUF) {
            producerProtobuf = this.getProducerProtobuf(topic, server);
        }

        try {
            dataInterface = dataClass.getDataInterfaceClass().getConstructor().newInstance();
        } catch (Exception e) {
            log.error("Error creating interface for {}", dataClass.getDataInterfaceClass().getName());
            status.setError(true);
            status.setErrorMessage("Error getting interface class ".concat(dataClass.getDataInterfaceClass().getName()));
            status.setStatus("Fail");
            status.setCount(0);
            return status;
        }

        if (producerAvro == null && producerJSON == null && producerProtobuf == null) {
            log.error("Error getting producer for topic {}", topic);
            status.setError(true);
            status.setErrorMessage("Error getting producer for topic ".concat(topic));
            status.setStatus("Fail");
            status.setCount(0);
            return status;
        } else if (dataClass.getKafkaFormat() == DataClass.KafkaFormat.AVRO) {
            for (int i = 0; i < batchSize; i++) {
                DataInterface dataInterface1 = dataInterface.generateData((startId < 0) ? -1 : startId + i,
                        (relativeItem < 0) ? -1 : relativeItem + i);
                producerAvro.send(new ProducerRecord<>(topic, AvroUtils.serializeToAvro(dataInterface1,dataInterface1.retAvroSchema())),
                        (recordMetadata, e) -> {
                            if (e != null) {
                                log.error(e.getMessage());
                            }
                            log.trace(recordMetadata.toString());
                        });
            }
        } else if (dataClass.getKafkaFormat() == DataClass.KafkaFormat.JSON ||
                dataClass.getKafkaFormat() == DataClass.KafkaFormat.JSON_NO_SCHEMA) {
            //ObjectMapper mapper = new ObjectMapper();
            for (int i = 0; i < batchSize; i++) {
                DataInterface dataInterface1 = dataInterface.generateData((startId < 0) ? -1 : startId + i,
                        (relativeItem < 0) ? -1 : relativeItem + i);
                producerJSON.send(new ProducerRecord<>(topic, dataInterface1
                                //mapper.convertValue(dataInterface1, dataClass.getDataInterfaceClass())
                                ),
                        (recordMetadata, e) -> {
                            if (e != null) {
                                log.error(e.getMessage());
                            }
                            log.trace(recordMetadata.toString());
                        });
            }
        } else /*if (dataClass.getKafkaFormat() == DataClass.KafkaFormat.PROTOBUF)*/ {
            for (int i = 0; i < batchSize; i++) {
                DataInterface dataInterface1 = dataInterface.generateData((startId < 0) ? -1 : startId + i,
                        (relativeItem < 0) ? -1 : relativeItem + i);
                DynamicMessage dynamicMessage = ProtobufUtils.serializeToProtobuf(dataInterface1,dataInterface1.retProtoSchema() );
                int finalI = i;
                producerProtobuf.send(new ProducerRecord<>(topic, dynamicMessage),
                        (recordMetadata, e) -> {
                            if (e != null) {
                                log.error(e.getMessage());
                            }
                            if(finalI == 0) {
                                log.trace("Metadata {}", recordMetadata.toString());
                            }
                        });
            }
        }
        log.info("Completed sending {} message for topic {} of type {}!", batchSize, topic, dataInterface.getClass().getSimpleName());
        status.setError(false);
        status.setErrorMessage("");
        status.setStatus("Success");
        status.setCount(batchSize);
        return status;
    }
}
