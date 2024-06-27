package aiven.io.kafka_executor.log.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Data
public class ClassStatistic {
    private String className;
    private double consumerCount;
    private double consumerAmount;
    private double producerCount;
    private double producerAmount;
}
