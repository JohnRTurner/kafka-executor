package aiven.io.kafka_executor.batch.view;

import aiven.io.kafka_executor.config.model.ConnectionConfig;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.log.model.Statistics;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ConsumerBatchExecutor {
    private final String topic;
    private final DataClass dataClass;
    private final int batchSize;
    private final int maxTries;
    private final ConnectionConfig connectionConfig;
    private final long sleepMillis;
    private final Statistics statistics;
    private final Map<Thread, ConsumerBatchTask> threadTaskMap = new HashMap<>();

    public ConsumerBatchExecutor(String topic, DataClass dataClass, int batchSize, int maxTries, ConnectionConfig connectionConfig, long sleepMillis, Statistics statistics, int numThreads) {
        this.topic = topic;
        this.dataClass = dataClass;
        this.batchSize = batchSize;
        this.maxTries = maxTries;
        this.connectionConfig = connectionConfig;
        this.sleepMillis = sleepMillis;
        this.statistics = statistics;
        for (int i = 0; i < numThreads; i++) {
            ConsumerBatchTask consumerBatchTask = new ConsumerBatchTask(topic, i, dataClass, batchSize, maxTries,
                    connectionConfig, sleepMillis, statistics);
            Thread thread = new Thread(consumerBatchTask);
            threadTaskMap.put(thread, consumerBatchTask);
            thread.start();
        }
    }

    public void stopTasks() {
        for (Map.Entry<Thread, ConsumerBatchTask> entry : threadTaskMap.entrySet()) {
            ConsumerBatchTask task = entry.getValue();
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
            ConsumerBatchTask consumerBatchTask = new ConsumerBatchTask(topic, threadTaskMap.size(), dataClass,
                    batchSize, maxTries, connectionConfig, sleepMillis, statistics);
            Thread thread = new Thread(consumerBatchTask);
            threadTaskMap.put(thread, consumerBatchTask);
            thread.start();
        }
        if(threadTaskMap.size() > numThreads) {
            for (Map.Entry<Thread, ConsumerBatchTask> entry : threadTaskMap.entrySet()) {
                ConsumerBatchTask task = entry.getValue();
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
