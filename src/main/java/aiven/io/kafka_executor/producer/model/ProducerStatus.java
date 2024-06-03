package aiven.io.kafka_executor.producer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProducerStatus {
    private static final DateTimeFormatter dtfDateTime = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
    @JsonProperty("CurrentDateTime")
    private String dtTime =dtfDateTime.format(LocalDateTime.now());;
    @JsonProperty("Error")
    private boolean error;
    @JsonProperty("ErrorMessage")
    private String errorMessage;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("Count")
    private int count;

}
