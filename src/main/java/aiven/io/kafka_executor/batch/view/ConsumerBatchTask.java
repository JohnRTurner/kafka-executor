package aiven.io.kafka_executor.batch.view;

import aiven.io.kafka_executor.config.model.ConnectionConfig;
import aiven.io.kafka_executor.consumer.model.ConsumerStatus;
import aiven.io.kafka_executor.consumer.view.LoadConsumer;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.log.model.Statistics;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static java.lang.Thread.sleep;


@Slf4j
public class ConsumerBatchTask implements Runnable {
    private volatile boolean running = true;
    private final String topic;
    @Getter
    private final int server;
    private final DataClass dataClass;
    private final int batchSize;
    private final int maxTries;
    private final ConnectionConfig connectionConfig;
    private final long sleepMillis;
    private final Statistics statistics;

    public ConsumerBatchTask(String topic, int server, DataClass dataClass, int batchSize, int maxTries,
                             ConnectionConfig connectionConfig, long sleepMillis, Statistics statistics) {
        this.topic = topic;
        this.server = server;
        this.dataClass = dataClass;
        this.batchSize = batchSize;
        this.connectionConfig = connectionConfig;
        this.sleepMillis = sleepMillis;
        this.statistics = statistics;
        this.maxTries = maxTries;
    }

    @Override
    public void run() {
        while (running) {
            try {
                ConsumerStatus consumerStatus = LoadConsumer.generateLoad(topic, server, batchSize, maxTries, dataClass,
                         connectionConfig);
                if(consumerStatus.isError()) {
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
