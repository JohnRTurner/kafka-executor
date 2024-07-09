package aiven.io.kafka_executor.batch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@Data
public class BatchStatus {
    private static final DateTimeFormatter dtfDateTime = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
    @Getter
    @JsonProperty("CurrentDateTime")
    private final String dtTime = dtfDateTime.format(LocalDateTime.now());
    @JsonProperty("BatchName")
    private String batchName;
    @JsonProperty("BatchType")
    private String batchType;
    @JsonProperty("RunningJobs")
    private int count;
}
