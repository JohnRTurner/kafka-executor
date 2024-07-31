package aiven.io.kafka_executor.log.model;

import aiven.io.kafka_executor.data.DataClass;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class Statistics {
    private final HashMap<String, Counter> consumerCount;
    private final HashMap<String, Counter> consumerAmount;
    private final HashMap<String, Counter> producerCount;
    private final HashMap<String, Counter> producerAmount;


    Statistics(MeterRegistry registry) {
        HashMap<String, Counter> c_count = new HashMap<>();
        HashMap<String, Counter> c_amount = new HashMap<>();
        HashMap<String, Counter> p_count = new HashMap<>();
        HashMap<String, Counter> p_amount = new HashMap<>();

        for (DataClass dataClass : DataClass.values()) {
            c_count.put(dataClass.name(), Counter.builder("kafka_executor").
                    tag("class", dataClass.name().toLowerCase()).
                    tag("job_type", "consumer").
                    tag("stat_type", "calls").
                    description("Kafka consumer calls").register(registry));
            c_amount.put(dataClass.name(), Counter.builder("kafka_executor").
                    tag("class", dataClass.name().toLowerCase()).
                    tag("job_type", "consumer").
                    tag("stat_type", "records").
                    description("Kafka consumer generated rows").register(registry));
            p_count.put(dataClass.name(), Counter.builder("kafka_executor").
                    tag("class", dataClass.name().toLowerCase()).
                    tag("job_type", "producer").
                    tag("stat_type", "calls").
                    description("Kafka producer calls").register(registry));
            p_amount.put(dataClass.name(), Counter.builder("kafka_executor").
                    tag("class", dataClass.name().toLowerCase()).
                    tag("job_type", "producer").
                    tag("stat_type", "records").
                    description("Kafka producer generated rows").register(registry));
        }
        this.consumerCount = c_count;
        this.consumerAmount = c_amount;
        this.producerCount = p_count;
        this.producerAmount = p_amount;
    }

    public void consumerSet(String className, int rows) {
        consumerCount.get(className).increment();
        consumerAmount.get(className).increment(rows);
    }

    public void producerSet(String className, int rows) {
        producerCount.get(className).increment();
        producerAmount.get(className).increment(rows);
    }

    public ClassStatistic getClassStatistic(String className) {

        return new ClassStatistic(className,
                consumerCount.get(className).count(), consumerAmount.get(className).count(),
                producerCount.get(className).count(), producerAmount.get(className).count());
    }

    public ClassStatistic[] getClassStatistics() {
        ClassStatistic[] cs = new ClassStatistic[DataClass.values().length];
        for (DataClass dataClass : DataClass.values()) {
            cs[dataClass.ordinal()] = getClassStatistic(dataClass.name());
        }
        return cs;
    }

}
