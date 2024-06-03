package aiven.io.kafka_executor.consumer.controller;

import aiven.io.kafka_executor.consumer.model.ConsumerStatus;
import aiven.io.kafka_executor.consumer.view.LoadConsumer;
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
@RequestMapping("/consumer")
@Slf4j
public class ConsumerController {

    private final LoadConsumer loadConsumer;


    public ConsumerController(LoadConsumer loadConsumer) {
        this.loadConsumer = loadConsumer;
    }


    @RequestMapping(value="/test/clean", method= RequestMethod.GET)
    public ResponseEntity<String> getClean(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        loadConsumer.clean();
        return new ResponseEntity<>("Executed Clean.", HttpStatus.OK);
    }

    @RequestMapping(value="/list", method= RequestMethod.GET)
    public ResponseEntity<DataClass[]> getList(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());

        return new ResponseEntity<>(DataClass.values(), HttpStatus.OK);
    }


    @RequestMapping(value="/generateLoad", method= RequestMethod.GET, params = {"topicName","server"})
    public ResponseEntity<ConsumerStatus> getStatus(@RequestParam(value="topicName",defaultValue = "CUSTOMER_JSON") String topicName,
                                                    @RequestParam(value="server",defaultValue = "1") int server,
                                                    @RequestParam(value="batchSize",defaultValue = "100000") int batchSize,
                                                    @RequestParam(value="maxTries",defaultValue = "100") int maxTries,
                                                    @RequestParam(value="dataClass",defaultValue = "CUSTOMER_JSON") String dataClass,
                                                    HttpServletRequest request){
        log.debug("Path: {}", request.getRequestURI());
        DataClass dataClass1 = null;
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

        return new ResponseEntity<>(loadConsumer.generateLoad(topicName, server, batchSize, maxTries, dataClass1),
                HttpStatus.OK);
    }

}
