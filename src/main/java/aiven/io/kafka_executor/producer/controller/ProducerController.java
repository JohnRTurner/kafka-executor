package aiven.io.kafka_executor.producer.controller;

import aiven.io.kafka_executor.config.model.KafkaConnectionConfig;
import aiven.io.kafka_executor.config.model.OpensearchConnectionConfig;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.log.model.Statistics;
import aiven.io.kafka_executor.producer.model.ProducerStatus;
import aiven.io.kafka_executor.producer.view.KafkaLoadProducer;
import aiven.io.kafka_executor.producer.view.OpensearchLoadProducer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

import static aiven.io.kafka_executor.data.DataClass.values;

@RestController
@RequestMapping("/producer")
@Slf4j
public class ProducerController {

    private final KafkaConnectionConfig kafkaConnectionConfig;
    private final OpensearchConnectionConfig opensearchConnectionConfig;
    private final Statistics statistics;

    public ProducerController(KafkaConnectionConfig kafkaConnectionConfig, OpensearchConnectionConfig opensearchConnectionConfig, Statistics statistics) {
        this.kafkaConnectionConfig = kafkaConnectionConfig;
        this.opensearchConnectionConfig = opensearchConnectionConfig;
        this.statistics = statistics;
    }


    @RequestMapping(value = "/cleanKafkaConnectionPool", method = RequestMethod.GET)
    public ResponseEntity<String> cleanKafkaConnectionPool(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        KafkaLoadProducer.clean();
        return new ResponseEntity<>("Executed Clean.", HttpStatus.OK);
    }

    @RequestMapping(value = "/listKafkaDataClasses", method = RequestMethod.GET)
    public ResponseEntity<DataClass[]> listKafkaDataClasses(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        return new ResponseEntity<>(values(), HttpStatus.OK);
    }

    @RequestMapping(value = "/listDataTypes", method = RequestMethod.GET)
    public ResponseEntity<String[]> listDataTypes(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        return new ResponseEntity<>(Arrays.stream(DataClass.values())
                .map(dataClass -> dataClass.getDataInterfaceClass().getSimpleName())
                .distinct() // Optional: To ensure no duplicate class names
                .toArray(String[]::new), HttpStatus.OK);
    }


    @RequestMapping(value = "/listKafkaTopics", method = RequestMethod.GET)
    public ResponseEntity<Set<String>> listKafkaTopics(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        try {
            Set<String> filteredSortedTopics = Objects.requireNonNull(KafkaLoadProducer.getTopics(kafkaConnectionConfig)).names().get().stream()
                    .filter(topic -> !topic.startsWith("_"))  //Remove underscore topics
                    .sorted() //alphabetical is nice.
                    .collect(Collectors.toCollection(TreeSet::new));
            return new ResponseEntity<>(filteredSortedTopics, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to load topics", e);
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }

    }

    @RequestMapping(value = "/createKafkaTopics", method = RequestMethod.PUT)
    public ResponseEntity<CreateTopicsResult> createKafkaTopics(
            @RequestParam(value = "topics[]", defaultValue = "Default List") String[] topics,
            @RequestParam(value = "partitions", defaultValue = "6") int partitions,
            @RequestParam(value = "replication", defaultValue = "2") short replication,
            HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        Collection<String> topicList = new ArrayList<>();
        if (topics.length == 1 && topics[0].equals("Default List")) {
            topicList.addAll(Arrays.stream(values()).map(Enum::name).toList());
        } else {
            topicList.addAll(Arrays.asList(topics));
        }
        if (topicList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(KafkaLoadProducer.createTopics(topicList, partitions, replication, kafkaConnectionConfig), HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteKafkaTopics", method = RequestMethod.DELETE)
    public ResponseEntity<DeleteTopicsResult> deleteKafkaTopics(
            @RequestParam(value = "topics[]", defaultValue = "Default List") String[] topics,
            HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        Collection<String> topicList = new ArrayList<>();
        if (topics.length == 1 && topics[0].equals("Default List")) {
            topicList.addAll(Arrays.stream(values()).map(Enum::name).toList());
        } else {
            topicList.addAll(Arrays.asList(topics));
        }
        if (topicList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(KafkaLoadProducer.deleteTopics(topicList, kafkaConnectionConfig), HttpStatus.OK);
    }


    @RequestMapping(value = "/generateKafkaLoad", method = RequestMethod.PUT, params = {"topicName", "server"})
    public ResponseEntity<ProducerStatus> generateKafkaLoad(@RequestParam(value = "topicName", defaultValue = "CUSTOMER_JSON") String topicName,
                                                          @RequestParam(value = "server", defaultValue = "1") int server,
                                                          @RequestParam(value = "batchSize", defaultValue = "100000") int batchSize,
                                                          @RequestParam(value = "startId", defaultValue = "-1") long startId,
                                                          @RequestParam(value = "correlatedStartIdInc", defaultValue = "-1") int correlatedStartIdInc,
                                                          @RequestParam(value = "correlatedEndIdInc", defaultValue = "-1") int correlatedEndIdInc,
                                                          @RequestParam(value = "dataClass", defaultValue = "CUSTOMER_JSON") String dataClass,
                                                          HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        DataClass dataClass1;
        try {
            dataClass1 = DataClass.valueOf(dataClass);
        } catch (IllegalArgumentException e) {
            ProducerStatus status = new ProducerStatus();
            status.setError(true);
            status.setErrorMessage(e.getMessage());
            status.setStatus("Error");
            status.setCount(0);
            return new ResponseEntity<>(status, HttpStatus.BAD_REQUEST);
        }
        ProducerStatus producerStatus = KafkaLoadProducer.generateLoad(topicName, server, dataClass1, batchSize,
                startId, correlatedStartIdInc, correlatedEndIdInc, kafkaConnectionConfig);
        statistics.producerSet(dataClass1.name(), producerStatus.getCount());
        return new ResponseEntity<>(producerStatus, HttpStatus.OK);
    }

    @RequestMapping(value = "/cleanOpenSearchConnection", method = RequestMethod.GET)
    public ResponseEntity<String> cleanOpenSearchConnection(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        OpensearchLoadProducer.clean(opensearchConnectionConfig);
        return new ResponseEntity<>("Executed Clean.", HttpStatus.OK);
    }

    @RequestMapping(value = "/listOpenSearchDataClasses", method = RequestMethod.GET)
    public ResponseEntity<DataClass[]> listOpenSearchDataClasses(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        DataClass[] filteredDataClasses = Arrays.stream(DataClass.values())
                .filter(val -> val.getKafkaFormat() == DataClass.KafkaFormat.JSON)
                .toArray(DataClass[]::new);
        return new ResponseEntity<>(filteredDataClasses, HttpStatus.OK);
    }


    @RequestMapping(value = "/listOpenSearchIndexes", method = RequestMethod.GET)
    public ResponseEntity<Set<String>> listOpenSearchIndexes(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        return new ResponseEntity<>((Set<String>) Arrays.stream(OpensearchLoadProducer.getIndexes(opensearchConnectionConfig)).
                collect(Collectors.toCollection(TreeSet::new)), HttpStatus.OK);
    }


    @RequestMapping(value = "/createOpenSearchIndexes", method = RequestMethod.PUT)
    public ResponseEntity<Boolean> createOpenSearchIndexes(
            @RequestParam(value = "indexes[]", defaultValue = "Default List") String[] indexes,
            @RequestParam(value = "shards", defaultValue = "6") int shards,
            @RequestParam(value = "replicas", defaultValue = "2") int replicas,
            @RequestParam(value = "refreshSeconds", defaultValue = "1") int refreshSeconds,
            HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        Collection<String> indexList;
        if (indexes.length == 1 && indexes[0].equals("Default List")) {
            indexList = Arrays.stream(values())
                    .filter(val -> val.getKafkaFormat() == DataClass.KafkaFormat.JSON)
                    .map(DataClass::name)
                    .collect(Collectors.toList());
        } else {
            indexList = new ArrayList<>(Arrays.asList(indexes));
        }
        if (indexList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(OpensearchLoadProducer.createIndexes(indexList, shards, replicas, refreshSeconds, opensearchConnectionConfig), HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteOpenSearchIndexes", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteOpenSearchIndexes(
            @RequestParam(value = "indexes[]", defaultValue = "Default List") String[] indexes,
            HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        Collection<String> indexList;
        if (indexes.length == 1 && indexes[0].equals("Default List")) {
            indexList = Arrays.stream(values())
                    .filter(val -> val.getKafkaFormat() == DataClass.KafkaFormat.JSON)
                    .map(DataClass::name)
                    .collect(Collectors.toList());
        } else {
            indexList = new ArrayList<>(Arrays.asList(indexes));
        }
        if (indexList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(OpensearchLoadProducer.deleteIndexes(indexList, opensearchConnectionConfig), HttpStatus.OK);
    }


    @RequestMapping(value = "/generateOpenSearchLoad", method = RequestMethod.PUT, params = {"topicName", "server"})
    public ResponseEntity<ProducerStatus> generateOpenSearchLoad(@RequestParam(value = "indexName", defaultValue = "CUSTOMER_JSON") String indexName,
                                                                 @RequestParam(value = "batchSize", defaultValue = "100000") int batchSize,
                                                                 @RequestParam(value = "startId", defaultValue = "-1") long startId,
                                                                 @RequestParam(value = "correlatedStartIdInc", defaultValue = "-1") int correlatedStartIdInc,
                                                                 @RequestParam(value = "correlatedEndIdInc", defaultValue = "-1") int correlatedEndIdInc,
                                                                 @RequestParam(value = "dataClass", defaultValue = "CUSTOMER_JSON") String dataClass,
                                                                 HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        DataClass dataClass1;
        try {
            dataClass1 = DataClass.valueOf(dataClass);
        } catch (IllegalArgumentException e) {
            ProducerStatus status = new ProducerStatus();
            status.setError(true);
            status.setErrorMessage(e.getMessage());
            status.setStatus("Error");
            status.setCount(0);
            return new ResponseEntity<>(status, HttpStatus.BAD_REQUEST);
        }
        ProducerStatus producerStatus = OpensearchLoadProducer.generateLoad(indexName, dataClass1, batchSize, startId,
                correlatedStartIdInc, correlatedEndIdInc, opensearchConnectionConfig);
        statistics.producerSet(dataClass1.name(), producerStatus.getCount());
        return new ResponseEntity<>(producerStatus, HttpStatus.OK);
    }

}
