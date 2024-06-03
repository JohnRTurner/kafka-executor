package aiven.io.kafka_executor.producer.controller;

import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.producer.model.ProducerStatus;
import aiven.io.kafka_executor.producer.view.LoadProducer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Set;

@RestController
@RequestMapping("/producer")
@Slf4j
public class ProducerController {

    private final LoadProducer loadProducer;

    public ProducerController(LoadProducer loadProducer) {
        this.loadProducer = loadProducer;
    }


    @RequestMapping(value="/clean", method= RequestMethod.GET)
    public ResponseEntity<String> getClean(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        loadProducer.clean();
        return new ResponseEntity<>("Executed Clean.", HttpStatus.OK);
    }

    @RequestMapping(value="/listDataClasses", method= RequestMethod.GET)
    public ResponseEntity<DataClass[]> getListDataClasses(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        return new ResponseEntity<>(DataClass.values(), HttpStatus.OK);
    }

    @RequestMapping(value="/listTopics", method= RequestMethod.GET)
    public ResponseEntity<Set<String>> getListTopics(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        Set<String> topics;
        try {
             topics = loadProducer.getTopics().names().get();
            return new ResponseEntity<>(topics, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to load topics", e);
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }

    }

    @RequestMapping(value="/createTopics", method= RequestMethod.PUT)
    public ResponseEntity<CreateTopicsResult> createTopics( //@Parameter(description = "Comma-separated list of topics. Leave empty to generate same names as Data Classes.")
                                                           @RequestParam(value="topics[]",required = false)  String[] topics,
                                                           @RequestParam(value="partitions",defaultValue = "4") int partitions,
                                                           @RequestParam(value="replication",defaultValue = "2") short replication,
                                                           HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        if(topics == null || topics.length == 0) {
            topics = Arrays.stream(DataClass.values())
                    .map(Enum::name)
                    .toArray(String[]::new);;
        }
        return new ResponseEntity<>(loadProducer.createTopics(topics,partitions,replication), HttpStatus.OK);
    }


    @RequestMapping(value="/generateLoad", method= RequestMethod.PUT, params = {"topicName","server"})
    public ResponseEntity<ProducerStatus> putGenerateLoad(@RequestParam(value="topicName",defaultValue = "CustomerJSON") String topicName,
                                                          @RequestParam(value="server",defaultValue = "1") int server,
                                                          @RequestParam(value="batchSize",defaultValue = "100000") int batchSize,
                                                          @RequestParam(value="startId",defaultValue = "-1") long startId,
                                                          @RequestParam(value="relativeId",defaultValue = "-1") int relativeId,
                                                          @RequestParam(value="dataClass",defaultValue = "CUSTOMER_JSON") String dataClass,
                                                          HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        DataClass dataClass1 = null;
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

        return new ResponseEntity<>(loadProducer.generateLoad(topicName,server,
                dataClass1,
                batchSize, startId, relativeId), HttpStatus.OK);
    }

}
