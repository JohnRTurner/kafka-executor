package aiven.io.kafka_executor.batch.view;

import aiven.io.kafka_executor.config.model.ConnectionConfig;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.log.model.Statistics;
import aiven.io.kafka_executor.producer.model.ProducerStatus;
import aiven.io.kafka_executor.producer.view.LoadProducer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Thread.sleep;


@Slf4j
public class ProducerBatchTask implements Runnable {
    private final String topic;
    @Getter
    private final int server;
    private final DataClass dataClass;
    private final int batchSize;
    private final AtomicLong startId;
    private final int correlatedStartIdInc;
    private final int correlatedEndIdInc;
    private final ConnectionConfig connectionConfig;
    private final long sleepMillis;
    private final Statistics statistics;
    private volatile boolean running = true;

    public ProducerBatchTask(String topic, int server, DataClass dataClass, int batchSize, AtomicLong startId,
                             int correlatedStartIdInc, int correlatedEndIdInc, ConnectionConfig connectionConfig,
                             long sleepMillis, Statistics statistics) {
        this.topic = topic;
        this.server = server;
        this.dataClass = dataClass;
        this.batchSize = batchSize;
        this.startId = startId;
        this.correlatedStartIdInc = correlatedStartIdInc;
        this.correlatedEndIdInc = correlatedEndIdInc;
        this.connectionConfig = connectionConfig;
        this.sleepMillis = sleepMillis;
        this.statistics = statistics;
    }

    @Override
    public void run() {
        while (running) {
            try {
                ProducerStatus producerStatus = LoadProducer.generateLoad(topic, server, dataClass, batchSize,
                        (startId.get() >= 0) ? startId.addAndGet(batchSize) : -1,
                        correlatedStartIdInc, correlatedEndIdInc, connectionConfig);
                if (producerStatus.isError()) {
                    log.trace("Error in batch task: {}", producerStatus);
                } else {
                    statistics.producerSet(dataClass.name(), producerStatus.getCount());
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
