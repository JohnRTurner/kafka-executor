package aiven.io.kafka_executor.batch.view;

import aiven.io.kafka_executor.config.model.ConnectionConfig;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.log.model.Statistics;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Getter
public class ProducerBatchExecutor {
    private final String topic;
    private final DataClass dataClass;
    private final int batchSize;
    private final AtomicLong startId;
    private final int correlatedStartIdInc;
    private final int correlatedEndIdInc;
    private final ConnectionConfig connectionConfig;
    private final long sleepMillis;
    private final Statistics statistics;
    private final Map<Thread, ProducerBatchTask> threadTaskMap = new HashMap<>();

    public ProducerBatchExecutor(String topic, DataClass dataClass, int batchSize, long startId,
                                 int correlatedStartIdInc, int correlatedEndIdInc, ConnectionConfig connectionConfig,
                                 long sleepMillis, Statistics statistics, int numThreads) {
        this.topic = topic;
        this.dataClass = dataClass;
        this.batchSize = batchSize;
        this.startId = new AtomicLong(startId);
        this.correlatedStartIdInc = correlatedStartIdInc;
        this.correlatedEndIdInc = correlatedEndIdInc;
        this.connectionConfig = connectionConfig;
        this.sleepMillis = sleepMillis;
        this.statistics = statistics;
        for (int i = 0; i < numThreads; i++) {
            ProducerBatchTask producerBatchTask = new ProducerBatchTask(topic, i, dataClass, batchSize, this.startId,
                    correlatedStartIdInc,correlatedEndIdInc, connectionConfig, sleepMillis, statistics);
            Thread thread = new Thread(producerBatchTask);
            threadTaskMap.put(thread, producerBatchTask);
            thread.start();
        }
    }

    public void stopTasks() {
        for (Map.Entry<Thread, ProducerBatchTask> entry : threadTaskMap.entrySet()) {
            ProducerBatchTask task = entry.getValue();
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
            ProducerBatchTask producerBatchTask = new ProducerBatchTask(topic, threadTaskMap.size(), dataClass, batchSize, startId,
                    correlatedStartIdInc,correlatedEndIdInc, connectionConfig, sleepMillis, statistics);
            Thread thread = new Thread(producerBatchTask);
            threadTaskMap.put(thread, producerBatchTask);
            thread.start();
        }
        if(threadTaskMap.size() > numThreads) {
            for (Map.Entry<Thread, ProducerBatchTask> entry : threadTaskMap.entrySet()) {
                ProducerBatchTask task = entry.getValue();
                if (task.getServer() >= numThreads) {
                    task.stop();
                    try {
                        entry.getKey().join(); // Wait for the thread to finish
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    threadTaskMap.remove(entry.getKey());
                }
            }
        }

    }

}
