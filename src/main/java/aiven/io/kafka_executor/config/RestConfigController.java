package aiven.io.kafka_executor.config;

import aiven.io.kafka_executor.consumer.KafkaLoadConsumer;
import aiven.io.kafka_executor.consumer.OpenSearchLoadConsumer;
import aiven.io.kafka_executor.producer.KafkaLoadProducer;
import aiven.io.kafka_executor.producer.OpenSearchLoadProducer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.record.CompressionType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/server")
@Slf4j
public class RestConfigController {
    private final KafkaConnectionConfig kafkaConnectionConfig;
    private final OpenSearchConnectionConfig openSearchConnectionConfig;

    public RestConfigController(KafkaConnectionConfig kafkaConnectionConfig, OpenSearchConnectionConfig openSearchConnectionConfig) {
        this.kafkaConnectionConfig = kafkaConnectionConfig;
        this.openSearchConnectionConfig = openSearchConnectionConfig;
    }


    @RequestMapping(value = "/kafkaCompressionTypes", method = RequestMethod.GET)
    public ResponseEntity<List<String>> kafkaCompressionTypes() {
        List<String> compressionTypes = Arrays.stream(CompressionType.values())
                .map(CompressionType::name)
                .collect(Collectors.toList());
        return new ResponseEntity<>(compressionTypes, HttpStatus.OK);
    }

    @RequestMapping(value = "/kafkaAckTypes", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> kafkaAckTypes() {
        Map<String, String> acks = Arrays.stream(KafkaConnectionConfig.ACKS.values())
                .collect(Collectors.toMap(Enum::name, KafkaConnectionConfig.ACKS::getValue));

        return new ResponseEntity<>(acks, HttpStatus.OK);
    }


    @RequestMapping(value = "/kafkaConnection", method = RequestMethod.GET)
    public ResponseEntity<KafkaConnectionConfigDTO> kafkaConnection(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        KafkaConnectionConfigDTO responseDTO = kafkaConnectionConfig.retConfig();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/kafkaConnection", method = RequestMethod.PUT)
    public void kafkaConnection(@RequestBody KafkaConnectionConfigDTO configDTO, HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        kafkaConnectionConfig.loadConfig(configDTO);
        KafkaLoadConsumer.clean();
        KafkaLoadProducer.clean();
    }

    @RequestMapping(value = "/enableKafka", method = RequestMethod.GET)
    public ResponseEntity<Boolean> enableKafka(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        return new ResponseEntity<>(kafkaConnectionConfig.isEnable(), HttpStatus.OK);
    }

    @RequestMapping(value = "/enableKafka", method = RequestMethod.PUT)
    public void enableKafka(@RequestBody boolean enable, HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        kafkaConnectionConfig.setEnable(enable);
    }

    @RequestMapping(value = "/openSearchConnection", method = RequestMethod.GET)
    public ResponseEntity<OpenSearchConnectionDTO> openSearchConnection(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        OpenSearchConnectionDTO responseDTO = openSearchConnectionConfig.retConfig();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/openSearchConnection", method = RequestMethod.PUT)
    public void openSearchConnection(@RequestBody OpenSearchConnectionDTO configDTO, HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        openSearchConnectionConfig.loadConfig(configDTO);
        OpenSearchLoadConsumer.clean(openSearchConnectionConfig);
        OpenSearchLoadProducer.clean(openSearchConnectionConfig);
    }

    @RequestMapping(value = "/enableOpenSearch", method = RequestMethod.GET)
    public ResponseEntity<Boolean> enableOpenSearch(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        return new ResponseEntity<>(openSearchConnectionConfig.isEnable(), HttpStatus.OK);
    }

    @RequestMapping(value = "/enableOpenSearch", method = RequestMethod.PUT)
    public void enableOpenSearch(@RequestBody boolean enable, HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        openSearchConnectionConfig.setEnable(enable);
    }

}
