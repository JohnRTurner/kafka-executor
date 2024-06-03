package aiven.io.kafka_executor.config.controller;

import aiven.io.kafka_executor.config.model.ConnectionConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/server")
@Slf4j
public class ConfigController {
    private final ConnectionConfig connectionConfig;

    public ConfigController(ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
    }


    @RequestMapping(value="/connection", method= RequestMethod.GET)
    public ResponseEntity<ConnectionConfig> getStatus(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        return new ResponseEntity<>(connectionConfig, HttpStatus.OK);
    }

    @RequestMapping(value="/connection", method= RequestMethod.PUT)
    public ResponseEntity<ConnectionConfig> setStatus(@RequestBody ConnectionConfig newConnectionConfig,
                                                      HttpServletRequest httpRequest) {
        log.debug("Path: {}", httpRequest.getRequestURI());
        try {
            PropertyUtils.copyProperties(connectionConfig, newConnectionConfig);
        } catch (Exception e) {
            return new ResponseEntity<>(connectionConfig, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        this.connectionConfig.setPort(newConnectionConfig.getPort());
        return new ResponseEntity<>(connectionConfig, HttpStatus.OK);
    }

}
