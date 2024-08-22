package aiven.io.kafka_executor.info;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
@Slf4j
public class RestInfoController {
    private final About about;

    public RestInfoController(About about) {
        this.about = about;
    }


    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public ResponseEntity<About> about(HttpServletRequest request) {
        log.debug("Path: {}", request.getRequestURI());
        about.updateTime();
        return new ResponseEntity<>(about, HttpStatus.OK);
    }

}
