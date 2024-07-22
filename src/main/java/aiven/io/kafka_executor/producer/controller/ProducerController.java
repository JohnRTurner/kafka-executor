package aiven.io.kafka_executor.producer.controller;

import aiven.io.kafka_executor.config.model.ConnectionConfig;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.log.model.Statistics;
import aiven.io.kafka_executor.producer.model.ProducerStatus;
import aiven.io.kafka_executor.producer.view.LoadProducer;
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

    private final ConnectionConfig connectionConfig;
    private final Statistics statistics;

    public ProducerController(ConnectionConfig connectionConfig, Statistics statistics) {
        this.connectionConfig = connectionConfig;
        this.statistics = statistics;
    }


    @RequestMapping(value = "/clean", method = RequestMethod.GET)
    public ResponseEntity<String> getClean(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        LoadProducer.clean();
        return new ResponseEntity<>("Executed Clean.", HttpStatus.OK);
    }

    @RequestMapping(value = "/listDataClasses", method = RequestMethod.GET)
    public ResponseEntity<DataClass[]> getListDataClasses(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        return new ResponseEntity<>(values(), HttpStatus.OK);
    }

    @RequestMapping(value = "/listTopics", method = RequestMethod.GET)
    public ResponseEntity<Set<String>> getListTopics(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        try {
            Set<String> filteredSortedTopics = Objects.requireNonNull(LoadProducer.getTopics(connectionConfig)).names().get().stream()
                    .filter(topic -> !topic.startsWith("_"))  //Remove underscore topics
                    .sorted() //alphabetical is nice.
                    .collect(Collectors.toCollection(TreeSet::new));
            return new ResponseEntity<>(filteredSortedTopics, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to load topics", e);
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }

    }

    @RequestMapping(value = "/createTopics", method = RequestMethod.PUT)
    public ResponseEntity<CreateTopicsResult> createTopics(
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
        return new ResponseEntity<>(LoadProducer.createTopics(topicList, partitions, replication, connectionConfig), HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteTopics", method = RequestMethod.DELETE)
    public ResponseEntity<DeleteTopicsResult> deleteTopics(
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

        return new ResponseEntity<>(LoadProducer.deleteTopics(topicList, connectionConfig), HttpStatus.OK);
    }


    @RequestMapping(value = "/generateLoad", method = RequestMethod.PUT, params = {"topicName", "server"})
    public ResponseEntity<ProducerStatus> putGenerateLoad(@RequestParam(value = "topicName", defaultValue = "CUSTOMER_JSON") String topicName,
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
        ProducerStatus producerStatus = LoadProducer.generateLoad(topicName, server, dataClass1, batchSize,
                startId, correlatedStartIdInc, correlatedEndIdInc, connectionConfig);
        statistics.producerSet(dataClass1.name(), producerStatus.getCount());
        return new ResponseEntity<>(producerStatus, HttpStatus.OK);
    }

}
