package aiven.io.kafka_executor.consumer;

import aiven.io.kafka_executor.config.KafkaConnectionConfig;
import aiven.io.kafka_executor.config.OpenSearchConnectionConfig;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.log.Statistics;
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
public class RestConsumerController {
    private final KafkaConnectionConfig kafkaConnectionConfig;
    private final OpenSearchConnectionConfig openSearchConnectionConfig;
    private final Statistics statistics;

    public RestConsumerController(KafkaConnectionConfig kafkaConnectionConfig,
                                  OpenSearchConnectionConfig openSearchConnectionConfig,
                                  Statistics statistics) {
        this.kafkaConnectionConfig = kafkaConnectionConfig;
        this.openSearchConnectionConfig = openSearchConnectionConfig;
        this.statistics = statistics;
    }


    @RequestMapping(value = "/cleanKafkaConnectionPool", method = RequestMethod.GET)
    public ResponseEntity<String> cleanKafkaConnectionPool(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        KafkaLoadConsumer.clean();
        return new ResponseEntity<>("Executed Clean.", HttpStatus.OK);
    }


    @RequestMapping(value = "/cleanOpenSearchConnectionPool", method = RequestMethod.GET)
    public ResponseEntity<String> cleanOpenSearchConnectionPool(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        OpenSearchLoadConsumer.clean(openSearchConnectionConfig);
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
        ConsumerStatus consumerStatus = KafkaLoadConsumer.generateLoad(topicName, server, batchSize, maxTries,
                dataClass1, kafkaConnectionConfig);
        statistics.consumerSet(dataClass1.name(), consumerStatus.getCount());
        return new ResponseEntity<>(consumerStatus, HttpStatus.OK);
    }

    @RequestMapping(value = "/generateOpenSearchLoad", method = RequestMethod.GET, params = {"topicName", "server"})
    public ResponseEntity<ConsumerStatus> generateOpenSearchLoad(@RequestParam(value = "indexName", defaultValue = "CUSTOMER_JSON") String indexName,
                                                                 @RequestParam(value = "startId", defaultValue = "1") long startId,
                                                                 @RequestParam(value = "endId", defaultValue = "1000000") long endId,
                                                                 @RequestParam(value = "batchSize", defaultValue = "1000") int batchSize,
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

        ConsumerStatus consumerStatus = OpenSearchLoadConsumer.generateLoadPrim(indexName, dataClass1, batchSize,
                startId, endId, openSearchConnectionConfig);
        statistics.consumerSet(dataClass1.name(), consumerStatus.getCount());
        return new ResponseEntity<>(consumerStatus, HttpStatus.OK);
    }


}
