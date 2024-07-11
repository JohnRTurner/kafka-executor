package aiven.io.kafka_executor.config.controller;

import aiven.io.kafka_executor.config.model.ConnectionConfig;
import aiven.io.kafka_executor.config.model.ConnectionConfigDTO;
import aiven.io.kafka_executor.consumer.view.LoadConsumer;
import aiven.io.kafka_executor.producer.view.LoadProducer;
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
    private final ConnectionConfig connectionConfig;

    public ConfigController(ConnectionConfig connectionConfig ) {
        this.connectionConfig = connectionConfig;
    }


    @RequestMapping(value = "/compressionTypes", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getCompressionTypes() {
        List<String> compressionTypes = Arrays.stream(CompressionType.values())
                .map(CompressionType::name)
                .collect(Collectors.toList());
        return new ResponseEntity<>(compressionTypes, HttpStatus.OK);
    }

    @RequestMapping(value = "/ackTypes", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> getAckTypes() {
        Map<String, String> acks = Arrays.stream(ConnectionConfig.ACKS.values())
                .collect(Collectors.toMap(Enum::name, ConnectionConfig.ACKS::getValue));

        return new ResponseEntity<>(acks, HttpStatus.OK);
    }


    @RequestMapping(value = "/connection", method = RequestMethod.GET)
    public ResponseEntity<ConnectionConfigDTO> getStatus(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        ConnectionConfigDTO responseDTO = connectionConfig.retConfig();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/connection", method = RequestMethod.PUT)
    public void updateConnectionConfig(@RequestBody ConnectionConfigDTO configDTO, HttpServletRequest request ) {
        log.debug("Path: {}", request.getRequestURI());
        connectionConfig.loadConfig(configDTO);
        LoadConsumer.clean();
        LoadProducer.clean();
    }
}
