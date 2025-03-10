package aiven.io.kafka_executor.producer;

import aiven.io.kafka_executor.config.KafkaConnectionConfig;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.data.DataInterface;
import aiven.io.kafka_executor.data.utils.AvroUtils;
import aiven.io.kafka_executor.data.utils.ProtobufUtils;
import com.google.protobuf.DynamicMessage;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaJsonSerializer;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.*;
import java.util.concurrent.ExecutionException;


@Slf4j
public class KafkaLoadProducer {
    private static final HashMap<String, KafkaProducer<String, DataInterface>> jsonProducers = new HashMap<>();
    private static final HashMap<String, KafkaProducer<String, GenericRecord>> avroProducers = new HashMap<>();
    private static final HashMap<String, KafkaProducer<String, DynamicMessage>> protobufProducers = new HashMap<>();


    //private static final Faker faker = new Faker();


    public static ListTopicsResult getTopics(KafkaConnectionConfig kafkaConnectionConfig) {
        Properties properties = kafkaConnectionConfig.connectionProperties(KafkaConnectionConfig.KAFKA_TYPE.ADMIN);
        properties.put("client.dns.lookup", "use_all_dns_ips");
        try (AdminClient adminClient = AdminClient.create(properties)) {
            DescribeClusterResult cluster = adminClient.describeCluster();
            log.warn("Cluster Id: {}", cluster.clusterId().get());
            log.warn("Brokers: {}", cluster.nodes().get());
            return adminClient.listTopics();
        } catch (Exception e) {
            log.error("Error getting topic list", e);
            return null;
        }
    }

    public static boolean createTopics(Collection<String> topics, int partitions, short replication,
                                                  KafkaConnectionConfig kafkaConnectionConfig) {
        try (AdminClient adminClient = AdminClient.create(kafkaConnectionConfig.connectionProperties(KafkaConnectionConfig.KAFKA_TYPE.ADMIN))) {
            Collection<NewTopic> newTopics = new ArrayList<>();
            for (String topic : topics) {
                newTopics.add(new NewTopic(topic, partitions, replication));
            }
            adminClient.createTopics(newTopics);
            return true;
        } catch (Exception e) {
            log.error("Error creating topics", e);
            return false;
        }
    }

    public static boolean deleteTopics(Collection<String> topics, KafkaConnectionConfig kafkaConnectionConfig) {
        try (AdminClient adminClient = AdminClient.create(kafkaConnectionConfig.connectionProperties(KafkaConnectionConfig.KAFKA_TYPE.ADMIN))) {
            adminClient.deleteTopics(topics);
            return true;
        } catch (Exception e) {
            log.error("Error creating topics", e);
            return false;
        }
    }


    private static KafkaProducer<String, DataInterface> getProducerJSON(String topic, int server, boolean registry,
                                                                        KafkaConnectionConfig kafkaConnectionConfig) {
        String key = topic.concat(Integer.toString(server).concat(Boolean.toString(registry)));
        KafkaProducer<String, DataInterface> producer = jsonProducers.get(key);
        if (producer == null) {
            try (AdminClient adminClient = AdminClient.create(kafkaConnectionConfig.connectionProperties(KafkaConnectionConfig.KAFKA_TYPE.ADMIN))) {
                for (String name : adminClient.listTopics().names().get()) {
                    if (name.equals(topic)) {
                        Properties properties = (registry) ?
                                kafkaConnectionConfig.connectionWithSchemaRegistryProperties(KafkaConnectionConfig.KAFKA_TYPE.PRODUCER) :
                                kafkaConnectionConfig.connectionProperties(KafkaConnectionConfig.KAFKA_TYPE.PRODUCER);
                        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                                StringSerializer.class.getName());
                        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, (registry) ?
                                KafkaJsonSchemaSerializer.class.getName() :
                                KafkaJsonSerializer.class.getName());
                        try {
                            producer = new KafkaProducer<>(properties);
                            jsonProducers.put(key, producer);
                        } catch (Exception e) {
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

    private static KafkaProducer<String, DynamicMessage> getProducerProtobuf(String topic, int server,
                                                                             KafkaConnectionConfig kafkaConnectionConfig) {
        String key = topic.concat(Integer.toString(server).concat("True"));
        KafkaProducer<String, DynamicMessage> producer = protobufProducers.get(key);
        if (producer == null) {
            try (AdminClient adminClient = AdminClient.create(kafkaConnectionConfig.connectionProperties(KafkaConnectionConfig.KAFKA_TYPE.ADMIN))) {
                for (String name : adminClient.listTopics().names().get()) {
                    if (name.equals(topic)) {
                        Properties properties = kafkaConnectionConfig.connectionWithSchemaRegistryProperties(KafkaConnectionConfig.KAFKA_TYPE.PRODUCER);
                        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                                StringSerializer.class.getName());
                        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                                KafkaProtobufSerializer.class.getName());
                        try {
                            producer = new KafkaProducer<>(properties);
                            protobufProducers.put(key, producer);
                        } catch (Exception e) {
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


    private static KafkaProducer<String, GenericRecord> getProducerAvro(String topic, int server,
                                                                        KafkaConnectionConfig kafkaConnectionConfig) {
        String key = topic.concat(Integer.toString(server).concat("False"));
        KafkaProducer<String, GenericRecord> producer = avroProducers.get(key);
        if (producer == null) {
            try (AdminClient adminClient = AdminClient.create(kafkaConnectionConfig.connectionProperties(KafkaConnectionConfig.KAFKA_TYPE.ADMIN))) {
                for (String name : adminClient.listTopics().names().get()) {
                    if (name.equals(topic)) {
                        Properties properties = kafkaConnectionConfig.connectionWithSchemaRegistryProperties(KafkaConnectionConfig.KAFKA_TYPE.PRODUCER);
                        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                                StringSerializer.class.getName());
                        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                                KafkaAvroSerializer.class.getName());
                        try {
                            producer = new KafkaProducer<>(properties);
                            avroProducers.put(key, producer);
                        } catch (Exception e) {
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

    public static void clean() {
        Iterator<Map.Entry<String, KafkaProducer<String, DataInterface>>> jsonIterator = jsonProducers.entrySet().iterator();
        while (jsonIterator.hasNext()) {
            KafkaProducer<String, DataInterface> producer = jsonIterator.next().getValue();
            try {
                producer.flush();
                producer.close();
            } catch (Exception e) {
                log.error("Error flushing producer for topic {}", jsonIterator.next().getKey(), e);
            }
            jsonIterator.remove();
        }

        Iterator<Map.Entry<String, KafkaProducer<String, GenericRecord>>> avroIterator = avroProducers.entrySet().iterator();
        while (avroIterator.hasNext()) {
            KafkaProducer<String, GenericRecord> producer = avroIterator.next().getValue();
            try {
                producer.flush();
                producer.close();
            } catch (Exception e) {
                log.error("Error flushing producer for topic {}", jsonIterator.next().getKey(), e);
            }
            avroIterator.remove();
        }

        Iterator<Map.Entry<String, KafkaProducer<String, DynamicMessage>>> protoIterator = protobufProducers.entrySet().iterator();
        while (protoIterator.hasNext()) {
            KafkaProducer<String, DynamicMessage> producer = protoIterator.next().getValue();
            try {
                producer.flush();
                producer.close();
            } catch (Exception e) {
                log.error("Error flushing producer for topic {}", jsonIterator.next().getKey(), e);
            }
            protoIterator.remove();
        }

    }

    public static ProducerStatus generateLoad(String topic, int server, DataClass dataClass, int batchSize,
                                              long startId, int correlatedStartIdInc, int correlatedEndIdInc,
                                              KafkaConnectionConfig kafkaConnectionConfig) {
        ProducerStatus status = new ProducerStatus();
        KafkaProducer<String, GenericRecord> producerAvro = null;
        KafkaProducer<String, DataInterface> producerJSON = null;
        KafkaProducer<String, DynamicMessage> producerProtobuf = null;
        DataInterface dataInterface = dataClass.getDataInterface();

        int correlatedRange = correlatedEndIdInc - correlatedStartIdInc + 1;
        if (correlatedRange < 1) {
            correlatedStartIdInc = -1;
        }

        if (dataClass.getKafkaFormat() == DataClass.KafkaFormat.AVRO) {
            producerAvro = getProducerAvro(topic, server, kafkaConnectionConfig);
        } else if (dataClass.getKafkaFormat() == DataClass.KafkaFormat.JSON_NO_SCHEMA) {
            producerJSON = getProducerJSON(topic, server, false, kafkaConnectionConfig);
        } else if (dataClass.getKafkaFormat() == DataClass.KafkaFormat.JSON) {
            producerJSON = getProducerJSON(topic, server, true, kafkaConnectionConfig);
        } else if (dataClass.getKafkaFormat() == DataClass.KafkaFormat.PROTOBUF) {
            producerProtobuf = getProducerProtobuf(topic, server, kafkaConnectionConfig);
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
                        (correlatedStartIdInc < 0) ? -1 : correlatedStartIdInc + (i % correlatedRange));
                producerAvro.send(new ProducerRecord<>(topic, AvroUtils.serializeToAvro(dataInterface1,
                        dataInterface1.retAvroSchema())), (recordMetadata, e) -> {
                    if (e != null) {
                        log.error(e.getMessage());
                    }
                    log.trace("Metadata {}", recordMetadata.toString());
                });
            }
        } else if (dataClass.getKafkaFormat() == DataClass.KafkaFormat.JSON ||
                dataClass.getKafkaFormat() == DataClass.KafkaFormat.JSON_NO_SCHEMA) {
            for (int i = 0; i < batchSize; i++) {
                DataInterface dataInterface1 = dataInterface.generateData((startId < 0) ? -1 : startId + i,
                        (correlatedStartIdInc < 0) ? -1 : correlatedStartIdInc + (i % correlatedRange));
                producerJSON.send(new ProducerRecord<>(topic, dataInterface1),
                        (recordMetadata, e) -> {
                            if (e != null) {
                                log.error(e.getMessage());
                            }
                            log.trace("Metadata {}", recordMetadata.toString());
                        });
            }
        } else /*if (dataClass.getKafkaFormat() == DataClass.KafkaFormat.PROTOBUF)*/ {
            for (int i = 0; i < batchSize; i++) {
                DataInterface dataInterface1 = dataInterface.generateData((startId < 0) ? -1 : startId + i,
                        (correlatedStartIdInc < 0) ? -1 : correlatedStartIdInc + (i % correlatedRange));
                DynamicMessage dynamicMessage = ProtobufUtils.serializeToProtobuf(dataInterface1, dataInterface1.retProtoSchema());
                producerProtobuf.send(new ProducerRecord<>(topic, dynamicMessage),
                        (recordMetadata, e) -> {
                            if (e != null) {
                                log.error(e.getMessage());
                            }
                            log.trace("Metadata {}", recordMetadata.toString());
                        });
            }
        }
        log.debug("Completed sending {} message for topic {} of type {}!", batchSize, topic, dataInterface.getClass().getSimpleName());
        status.setError(false);
        status.setErrorMessage("");
        status.setStatus("Success");
        status.setCount(batchSize);
        return status;
    }
}
