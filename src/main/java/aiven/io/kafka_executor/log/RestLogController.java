package aiven.io.kafka_executor.log;

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
public class RestLogController {
    private final Statistics statistic;

    public RestLogController(Statistics statistic) {
        this.statistic = statistic;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<ClassStatistic[]> list(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());

        return new ResponseEntity<>(statistic.getClassStatistics(), HttpStatus.OK);
    }

    @RequestMapping(value = "/listClass", method = RequestMethod.GET)
    public ResponseEntity<ClassStatistic> listClass(@RequestParam(value = "className", defaultValue = "CUSTOMER_JSON") String className,
                                                  HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());

        return new ResponseEntity<>(statistic.getClassStatistic(className), HttpStatus.OK);
    }


}
