package aiven.io.kafka_executor.batch;

import aiven.io.kafka_executor.config.KafkaConnectionConfig;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.log.Statistics;
import lombok.Getter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Getter
public class ProducerKafkaBatchExecutor {
    private final String topic;
    private final DataClass dataClass;
    private final int batchSize;
    private final AtomicLong startId;
    private final int correlatedStartIdInc;
    private final int correlatedEndIdInc;
    private final KafkaConnectionConfig kafkaConnectionConfig;
    private final long sleepMillis;
    private final Statistics statistics;
    private final Map<Thread, ProducerKafkaBatchTask> threadTaskMap = new HashMap<>();

    public ProducerKafkaBatchExecutor(String topic, DataClass dataClass, int batchSize, long startId,
                                      int correlatedStartIdInc, int correlatedEndIdInc, KafkaConnectionConfig kafkaConnectionConfig,
                                      long sleepMillis, Statistics statistics, int numThreads) {
        this.topic = topic;
        this.dataClass = dataClass;
        this.batchSize = batchSize;
        this.startId = new AtomicLong(startId);
        this.correlatedStartIdInc = correlatedStartIdInc;
        this.correlatedEndIdInc = correlatedEndIdInc;
        this.kafkaConnectionConfig = kafkaConnectionConfig;
        this.sleepMillis = sleepMillis;
        this.statistics = statistics;
        for (int i = 0; i < numThreads; i++) {
            ProducerKafkaBatchTask producerKafkaBatchTask = new ProducerKafkaBatchTask(topic, i, dataClass, batchSize, this.startId,
                    correlatedStartIdInc, correlatedEndIdInc, kafkaConnectionConfig, sleepMillis, statistics);
            Thread thread = new Thread(producerKafkaBatchTask);
            threadTaskMap.put(thread, producerKafkaBatchTask);
            thread.start();
        }
    }

    public void stopTasks() {
        for (Map.Entry<Thread, ProducerKafkaBatchTask> entry : threadTaskMap.entrySet()) {
            ProducerKafkaBatchTask task = entry.getValue();
            task.stop();
            try {
                entry.getKey().join(); // Wait for the thread to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        threadTaskMap.clear();
    }

    public int getTaskCount() {
        return threadTaskMap.size();
    }

    public void changeTaskCount(int numThreads) {
        while (threadTaskMap.size() < numThreads) {
            ProducerKafkaBatchTask producerKafkaBatchTask = new ProducerKafkaBatchTask(topic, threadTaskMap.size(), dataClass, batchSize, startId,
                    correlatedStartIdInc, correlatedEndIdInc, kafkaConnectionConfig, sleepMillis, statistics);
            Thread thread = new Thread(producerKafkaBatchTask);
            threadTaskMap.put(thread, producerKafkaBatchTask);
            thread.start();
        }
        if (threadTaskMap.size() > numThreads) {
            Iterator<Map.Entry<Thread, ProducerKafkaBatchTask>> iterator = threadTaskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Thread, ProducerKafkaBatchTask> entry = iterator.next();
                ProducerKafkaBatchTask task = entry.getValue();
                if (task.getServer() >= numThreads) {
                    task.stop();
                    try {
                        entry.getKey().join(); // Wait for the thread to finish
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    iterator.remove(); // Remove the current entry from the iterator
                }
            }
        }

    }

}
