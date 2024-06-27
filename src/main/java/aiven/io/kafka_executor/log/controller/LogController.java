package aiven.io.kafka_executor.log.controller;

import aiven.io.kafka_executor.log.model.ClassStatistic;
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
@RequestMapping("/log")
@Slf4j
public class LogController {
    private final Statistics statistic;
    public LogController(Statistics statistic) {
        this.statistic = statistic;
    }

    @RequestMapping(value="/list", method= RequestMethod.GET)
    public ResponseEntity<ClassStatistic[]> getList(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());

        return new ResponseEntity<>( statistic.getClassStatistics(), HttpStatus.OK);
    }

    @RequestMapping(value="/listClass", method= RequestMethod.GET)
    public ResponseEntity<ClassStatistic> getList(@RequestParam(value="className",defaultValue = "CUSTOMER_JSON") String className,
                                                    HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());

        return new ResponseEntity<>( statistic.getClassStatistic(className), HttpStatus.OK);
    }


}
