package aiven.io.kafka_executor.batch;

import aiven.io.kafka_executor.config.KafkaConnectionConfig;
import aiven.io.kafka_executor.consumer.ConsumerStatus;
import aiven.io.kafka_executor.consumer.KafkaLoadConsumer;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.log.Statistics;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static java.lang.Thread.sleep;


@Slf4j
public class ConsumerKafkaBatchTask implements Runnable {
    private final String topic;
    @Getter
    private final int server;
    private final DataClass dataClass;
    private final int batchSize;
    private final int maxTries;
    private final KafkaConnectionConfig kafkaConnectionConfig;
    private final long sleepMillis;
    private final Statistics statistics;
    private volatile boolean running = true;

    public ConsumerKafkaBatchTask(String topic, int server, DataClass dataClass, int batchSize, int maxTries,
                                  KafkaConnectionConfig kafkaConnectionConfig, long sleepMillis, Statistics statistics) {
        this.topic = topic;
        this.server = server;
        this.dataClass = dataClass;
        this.batchSize = batchSize;
        this.kafkaConnectionConfig = kafkaConnectionConfig;
        this.sleepMillis = sleepMillis;
        this.statistics = statistics;
        this.maxTries = maxTries;
    }

    @Override
    public void run() {
        while (running) {
            try {
                ConsumerStatus consumerStatus = KafkaLoadConsumer.generateLoad(topic, server, batchSize, maxTries, dataClass,
                        kafkaConnectionConfig);
                if (consumerStatus.isError()) {
                    log.trace("Error in batch task: {}", consumerStatus);
                } else {
                    statistics.consumerSet(dataClass.name(), consumerStatus.getCount());
                }
                if (sleepMillis > 0) {
                    sleep(sleepMillis);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public void stop() {
        running = false;
    }
}
