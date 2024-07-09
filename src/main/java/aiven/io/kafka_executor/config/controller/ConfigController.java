package aiven.io.kafka_executor.config.controller;

import aiven.io.kafka_executor.config.model.ConnectionConfig;
import aiven.io.kafka_executor.config.model.ConnectionConfigDTO;
import aiven.io.kafka_executor.consumer.view.LoadConsumer;
import aiven.io.kafka_executor.producer.view.LoadProducer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.record.CompressionType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
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

    @RequestMapping(value = "/connection", method = RequestMethod.GET)
    public ResponseEntity<ConnectionConfigDTO> getStatus(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        ConnectionConfigDTO responseDTO = new ConnectionConfigDTO();
        responseDTO.setHost(connectionConfig.getHost());
        responseDTO.setPort(connectionConfig.getPort());
        responseDTO.setCert_password(connectionConfig.getCert_password());
        responseDTO.setTruststore_location(connectionConfig.getTruststore_location());
        responseDTO.setKeystore_location(connectionConfig.getKeystore_location());
        responseDTO.setSchemaRegistryHost(connectionConfig.getSchemaRegistryHost());
        responseDTO.setSchemaRegistryPort(connectionConfig.getSchemaRegistryPort());
        responseDTO.setSchemaRegistryUser(connectionConfig.getSchemaRegistryUser());
        responseDTO.setSchemaRegistryPassword(connectionConfig.getSchemaRegistryPassword());
        responseDTO.setLingerMs(connectionConfig.getLingerMs());
        responseDTO.setBatchSize(connectionConfig.getBatchSize());
        responseDTO.setCompressionType(connectionConfig.getCompressionType());

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/connection", method = RequestMethod.PUT)
    public void updateConnectionConfig(@RequestBody ConnectionConfigDTO configDTO, HttpServletRequest request ) {
        log.debug("Path: {}", request.getRequestURI());
        connectionConfig.setHost(configDTO.getHost());
        connectionConfig.setPort(configDTO.getPort());
        connectionConfig.setCert_password(configDTO.getCert_password());
        connectionConfig.setTruststore_location(configDTO.getTruststore_location());
        connectionConfig.setKeystore_location(configDTO.getKeystore_location());
        connectionConfig.setSchemaRegistryHost(configDTO.getSchemaRegistryHost());
        connectionConfig.setSchemaRegistryPort(configDTO.getSchemaRegistryPort());
        connectionConfig.setSchemaRegistryUser(configDTO.getSchemaRegistryUser());
        connectionConfig.setSchemaRegistryPassword(configDTO.getSchemaRegistryPassword());
        connectionConfig.setLingerMs(configDTO.getLingerMs());
        connectionConfig.setBatchSize(configDTO.getBatchSize());
        connectionConfig.setCompressionType(configDTO.getCompressionType());
        LoadConsumer.clean();
        LoadProducer.clean();
    }
}
