package aiven.io.kafka_executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
public class KafkaExecutorApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaExecutorApplication.class, args);
    }

}
