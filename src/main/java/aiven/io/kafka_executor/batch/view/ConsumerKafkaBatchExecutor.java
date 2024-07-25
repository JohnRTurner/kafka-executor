package aiven.io.kafka_executor.batch.view;

import aiven.io.kafka_executor.config.model.KafkaConnectionConfig;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.log.model.Statistics;
import lombok.Getter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Getter
public class ConsumerKafkaBatchExecutor {
    private final String topic;
    private final DataClass dataClass;
    private final int batchSize;
    private final int maxTries;
    private final KafkaConnectionConfig kafkaConnectionConfig;
    private final long sleepMillis;
    private final Statistics statistics;
    private final Map<Thread, ConsumerKafkaBatchTask> threadTaskMap = new HashMap<>();

    public ConsumerKafkaBatchExecutor(String topic, DataClass dataClass, int batchSize, int maxTries,
                                      KafkaConnectionConfig kafkaConnectionConfig, long sleepMillis, Statistics statistics,
                                      int numThreads) {
        this.topic = topic;
        this.dataClass = dataClass;
        this.batchSize = batchSize;
        this.maxTries = maxTries;
        this.kafkaConnectionConfig = kafkaConnectionConfig;
        this.sleepMillis = sleepMillis;
        this.statistics = statistics;
        for (int i = 0; i < numThreads; i++) {
            ConsumerKafkaBatchTask consumerKafkaBatchTask = new ConsumerKafkaBatchTask(topic, i, dataClass, batchSize, maxTries,
                    kafkaConnectionConfig, sleepMillis, statistics);
            Thread thread = new Thread(consumerKafkaBatchTask);
            threadTaskMap.put(thread, consumerKafkaBatchTask);
            thread.start();
        }
    }

    public void stopTasks() {
        for (Map.Entry<Thread, ConsumerKafkaBatchTask> entry : threadTaskMap.entrySet()) {
            ConsumerKafkaBatchTask task = entry.getValue();
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
            ConsumerKafkaBatchTask consumerKafkaBatchTask = new ConsumerKafkaBatchTask(topic, threadTaskMap.size(), dataClass,
                    batchSize, maxTries, kafkaConnectionConfig, sleepMillis, statistics);
            Thread thread = new Thread(consumerKafkaBatchTask);
            threadTaskMap.put(thread, consumerKafkaBatchTask);
            thread.start();
        }
        if (threadTaskMap.size() > numThreads) {
            Iterator<Map.Entry<Thread, ConsumerKafkaBatchTask>> iterator = threadTaskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Thread, ConsumerKafkaBatchTask> entry = iterator.next();
                ConsumerKafkaBatchTask task = entry.getValue();
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
