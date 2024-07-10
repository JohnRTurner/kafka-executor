package aiven.io.kafka_executor.batch.controller;

import aiven.io.kafka_executor.batch.model.BatchStatus;
import aiven.io.kafka_executor.batch.view.BatchExecutionService;
import aiven.io.kafka_executor.data.DataClass;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
@Slf4j
public class BatchController {
    private final BatchExecutionService batchExecutionService;

    public BatchController(BatchExecutionService batchExecutionService) {
        this.batchExecutionService = batchExecutionService;
    }

    @RequestMapping(value = "/stopAllTasks", method = RequestMethod.GET)
    public ResponseEntity<String> getClean(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        batchExecutionService.stopAllTasks();
        return new ResponseEntity<>("Stopped All Tasks.", HttpStatus.OK);
    }

    @RequestMapping(value = "/batchStatus", method = RequestMethod.GET)
    public ResponseEntity<BatchStatus[]> getBatchStatuses(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());

        return new ResponseEntity<>(batchExecutionService.getBatchStatuses().toArray(new BatchStatus[0]), HttpStatus.OK);
    }

    @RequestMapping(value = "/list/producers", method = RequestMethod.GET)
    public ResponseEntity<String[]> getProducers(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());

        return new ResponseEntity<>(batchExecutionService.getProducerBatchNames().toArray(new String[0]), HttpStatus.OK);
    }

    @RequestMapping(value = "/list/consumers", method = RequestMethod.GET)
    public ResponseEntity<String[]> getConsumers(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());

        return new ResponseEntity<>(batchExecutionService.getConsumerBatchNames().toArray(new String[0]), HttpStatus.OK);
    }


    @RequestMapping(value = "/generateConsumerTask", method = RequestMethod.POST)
    public ResponseEntity<Boolean> createConsumerTask(@RequestParam(value = "topicName", defaultValue = "CUSTOMER_JSON") String topicName,
                                                      @RequestParam(value = "numThreads", defaultValue = "1") int numThreads,
                                                      @RequestParam(value = "batchSize", defaultValue = "100000") int batchSize,
                                                      @RequestParam(value = "maxTries", defaultValue = "100") int maxTries,
                                                      @RequestParam(value = "sleepMillis", defaultValue = "100") long sleepMillis,
                                                      @RequestParam(value = "dataClass", defaultValue = "CUSTOMER_JSON") String dataClass,
                                                      HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        DataClass dataClass1;
        try {
            dataClass1 = DataClass.valueOf(dataClass);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(batchExecutionService.createConsumerTask(topicName.concat("_").concat(dataClass), topicName, dataClass1, batchSize, maxTries, sleepMillis, numThreads), HttpStatus.OK);
    }

    @RequestMapping(value = "/generateProducerTask", method = RequestMethod.POST)
    public ResponseEntity<Boolean> createProducerTask(@RequestParam(value = "topicName", defaultValue = "CUSTOMER_JSON") String topicName,
                                                      @RequestParam(value = "numThreads", defaultValue = "1") int numThreads,
                                                      @RequestParam(value = "batchSize", defaultValue = "100000") int batchSize,
                                                      @RequestParam(value = "startId", defaultValue = "-1") long startId,
                                                      @RequestParam(value = "correlatedStartIdInc", defaultValue = "-1") int correlatedStartIdInc,
                                                      @RequestParam(value = "correlatedEndIdInc", defaultValue = "-1") int correlatedEndIdInc,
                                                      @RequestParam(value = "sleepMillis", defaultValue = "100") long sleepMillis,
                                                      @RequestParam(value = "dataClass", defaultValue = "CUSTOMER_JSON") String dataClass,
                                                      HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        DataClass dataClass1;
        try {
            dataClass1 = DataClass.valueOf(dataClass);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(batchExecutionService.createProducerTask(topicName.concat("_").concat(dataClass),
                topicName, dataClass1, batchSize, startId, correlatedStartIdInc, correlatedEndIdInc, sleepMillis,
                numThreads), HttpStatus.OK);
    }

    @RequestMapping(value = "/dropProducerTask", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> dropProducerTask(@RequestParam(value = "taskName", defaultValue = "CUSTOMER_JSON_CUSTOMER_JSON") String taskName,
                                                    HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        return new ResponseEntity<>(batchExecutionService.dropProducerTask(taskName), HttpStatus.OK);
    }

    @RequestMapping(value = "/dropConsumerTask", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> dropConsumerTask(@RequestParam(value = "taskName", defaultValue = "CUSTOMER_JSON_CUSTOMER_JSON") String taskName,
                                                    HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        return new ResponseEntity<>(batchExecutionService.dropConsumerTask(taskName), HttpStatus.OK);
    }


    @RequestMapping(value = "/changeConsumerTaskCount", method = RequestMethod.PATCH)
    public ResponseEntity<Boolean> changeConsumerTaskCount(@RequestParam(value = "taskName", defaultValue = "CUSTOMER_JSON_CUSTOMER_JSON") String taskName,
                                                    @RequestParam(value = "numThreads", defaultValue = "4") int numThreads,
                                                    HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        return new ResponseEntity<>(batchExecutionService.changeConsumerTaskCount(taskName, numThreads), HttpStatus.OK);
    }

    @RequestMapping(value = "/changeProducerTaskCount", method = RequestMethod.PATCH)
    public ResponseEntity<Boolean> changeProducerTaskCount(@RequestParam(value = "taskName", defaultValue = "CUSTOMER_JSON_CUSTOMER_JSON") String taskName,
                                                           @RequestParam(value = "numThreads", defaultValue = "4") int numThreads,
                                                           HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        return new ResponseEntity<>(batchExecutionService.changeProducerTaskCount(taskName, numThreads), HttpStatus.OK);
    }

}
