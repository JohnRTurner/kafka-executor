package aiven.io.kafka_executor.config.controller;

import aiven.io.kafka_executor.config.model.KafkaConnectionConfig;
import aiven.io.kafka_executor.config.model.KafkaConnectionConfigDTO;
import aiven.io.kafka_executor.consumer.view.KafkaLoadConsumer;
import aiven.io.kafka_executor.producer.view.KafkaLoadProducer;
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
public class ConfigController {
    private final KafkaConnectionConfig kafkaConnectionConfig;

    public ConfigController(KafkaConnectionConfig kafkaConnectionConfig) {
        this.kafkaConnectionConfig = kafkaConnectionConfig;
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
}
