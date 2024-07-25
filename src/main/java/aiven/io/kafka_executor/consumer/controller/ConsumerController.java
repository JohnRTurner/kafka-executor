package aiven.io.kafka_executor.consumer.controller;

import aiven.io.kafka_executor.config.model.KafkaConnectionConfig;
import aiven.io.kafka_executor.consumer.model.ConsumerStatus;
import aiven.io.kafka_executor.consumer.view.KafkaLoadConsumer;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.log.model.Statistics;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/consumer")
@Slf4j
public class ConsumerController {
    private final KafkaConnectionConfig kafkaConnectionConfig;
    private final Statistics statistics;

    public ConsumerController(KafkaConnectionConfig kafkaConnectionConfig, Statistics statistics) {
        this.kafkaConnectionConfig = kafkaConnectionConfig;
        this.statistics = statistics;
    }


    @RequestMapping(value = "/cleanKafkaConnectionPool", method = RequestMethod.GET)
    public ResponseEntity<String> cleanKafkaConnectionPool(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        KafkaLoadConsumer.clean();
        return new ResponseEntity<>("Executed Clean.", HttpStatus.OK);
    }


    @RequestMapping(value = "/generateKafkaLoad", method = RequestMethod.GET, params = {"topicName", "server"})
    public ResponseEntity<ConsumerStatus> generateKafkaLoad(@RequestParam(value = "topicName", defaultValue = "CUSTOMER_JSON") String topicName,
                                                    @RequestParam(value = "server", defaultValue = "1") int server,
                                                    @RequestParam(value = "batchSize", defaultValue = "100000") int batchSize,
                                                    @RequestParam(value = "maxTries", defaultValue = "100") int maxTries,
                                                    @RequestParam(value = "dataClass", defaultValue = "CUSTOMER_JSON") String dataClass,
                                                    HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        DataClass dataClass1;
        try {
            dataClass1 = DataClass.valueOf(dataClass);
        } catch (IllegalArgumentException e) {
            ConsumerStatus status = new ConsumerStatus();
            status.setError(true);
            status.setErrorMessage(e.getMessage());
            status.setStatus("Error");
            status.setCount(0);
            return new ResponseEntity<>(status, HttpStatus.BAD_REQUEST);
        }
        ConsumerStatus consumerStatus = KafkaLoadConsumer.generateLoad(topicName, server, batchSize, maxTries, dataClass1, kafkaConnectionConfig);
        statistics.consumerSet(dataClass1.name(), consumerStatus.getCount());
        return new ResponseEntity<>(consumerStatus, HttpStatus.OK);
    }

}
