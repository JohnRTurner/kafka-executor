package aiven.io.kafka_executor.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@NoArgsConstructor
@Data
public class About {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");


    @Value("${kafka_executor.name}")
    @JsonProperty("Name")
    private String name;

    @Value("${kafka_executor.version}")
    @JsonProperty("Version")
    private String version;

    @JsonProperty("CurrentDateTime")
    private String dtTime;

    public void updateTime() {
        dtTime = dtf.format(LocalDateTime.now());
    }

}
