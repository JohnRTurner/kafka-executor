package aiven.io.kafka_executor.batch;

import aiven.io.kafka_executor.config.KafkaConnectionConfig;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.log.Statistics;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BatchExecutionService {
    public static final String CONSUMER = "Consumer";
    public static final String PRODUCER = "Producer";
    private static final Map<String, ConsumerKafkaBatchExecutor> consumerBatchExecutorHashMap = new HashMap<>();
    private static final Map<String, ProducerKafkaBatchExecutor> producerBatchExecutorHashMap = new HashMap<>();
    private final KafkaConnectionConfig kafkaConnectionConfig;
    private final Statistics statistics;


    BatchExecutionService(KafkaConnectionConfig kafkaConnectionConfig, Statistics statistics) {
        this.kafkaConnectionConfig = kafkaConnectionConfig;
        this.statistics = statistics;
    }

    public boolean createKafkaConsumerTask(String batchName, String topic, DataClass dataClass, int batchSize, int maxTries,
                                           long sleepMillis, int numThreads) {
        ConsumerKafkaBatchExecutor consumerKafkaBatchExecutor = consumerBatchExecutorHashMap.get(batchName);
        if (consumerKafkaBatchExecutor == null) {
            consumerBatchExecutorHashMap.put(batchName,
                    new ConsumerKafkaBatchExecutor(topic, dataClass, batchSize, maxTries, kafkaConnectionConfig, sleepMillis,
                            statistics, numThreads));
            return true;
        }
        return false;
    }

    public boolean createKafkaProducerTask(String batchName, String topic, DataClass dataClass, int batchSize, long startId,
                                           int correlatedStartIdInc, int correlatedEndIdInc, long sleepMillis, int numThreads) {
        ProducerKafkaBatchExecutor producerKafkaBatchExecutor = producerBatchExecutorHashMap.get(batchName);
        if (producerKafkaBatchExecutor == null) {
            producerBatchExecutorHashMap.put(batchName,
                    new ProducerKafkaBatchExecutor(topic, dataClass, batchSize, startId, correlatedStartIdInc,
                            correlatedEndIdInc, kafkaConnectionConfig, sleepMillis, statistics, numThreads));
            return true;
        }
        return false;
    }


    public boolean dropKafkaConsumerTask(String batchName) {
        ConsumerKafkaBatchExecutor consumerKafkaBatchExecutor = consumerBatchExecutorHashMap.get(batchName);
        if (consumerKafkaBatchExecutor != null) {
            consumerKafkaBatchExecutor.stopTasks();
            consumerBatchExecutorHashMap.remove(batchName);
            return true;
        }
        return false;
    }

    public boolean dropKafkaProducerTask(String batchName) {
        ProducerKafkaBatchExecutor producerKafkaBatchExecutor = producerBatchExecutorHashMap.get(batchName);
        if (producerKafkaBatchExecutor != null) {
            producerKafkaBatchExecutor.stopTasks();
            producerBatchExecutorHashMap.remove(batchName);
            return true;
        }
        return false;
    }


    public boolean changeKafkaConsumerTaskCount(String batchName, int numThreads) {
        ConsumerKafkaBatchExecutor consumerKafkaBatchExecutor = consumerBatchExecutorHashMap.get(batchName);
        if (consumerKafkaBatchExecutor != null) {
            consumerKafkaBatchExecutor.changeTaskCount(numThreads);
            return true;
        }
        return false;
    }

    public boolean changeKafkaProducerTaskCount(String batchName, int numThreads) {
        ProducerKafkaBatchExecutor producerKafkaBatchExecutor = producerBatchExecutorHashMap.get(batchName);
        if (producerKafkaBatchExecutor != null) {
            producerKafkaBatchExecutor.changeTaskCount(numThreads);
            return true;
        }
        return false;
    }

    public List<String> getKafkaConsumerBatchNames() {
        return consumerBatchExecutorHashMap.keySet().stream().toList();
    }

    public List<String> getKafkaProducerBatchNames() {
        return producerBatchExecutorHashMap.keySet().stream().toList();
    }

    public void stopAllTasks() {
        for (Map.Entry<String, ConsumerKafkaBatchExecutor> entry : consumerBatchExecutorHashMap.entrySet()) {
            ConsumerKafkaBatchExecutor task = entry.getValue();
            task.stopTasks();
        }
        consumerBatchExecutorHashMap.clear();
        for (Map.Entry<String, ProducerKafkaBatchExecutor> entry : producerBatchExecutorHashMap.entrySet()) {
            ProducerKafkaBatchExecutor task = entry.getValue();
            task.stopTasks();
        }
        producerBatchExecutorHashMap.clear();

    }

    public List<BatchStatus> getBatchStatuses() {
        List<BatchStatus> batchStatuses = new ArrayList<>();
        for (Map.Entry<String, ConsumerKafkaBatchExecutor> entry : consumerBatchExecutorHashMap.entrySet()) {
            ConsumerKafkaBatchExecutor task = entry.getValue();
            batchStatuses.add(new BatchStatus(entry.getKey(), CONSUMER, task.getTaskCount()));
        }
        for (Map.Entry<String, ProducerKafkaBatchExecutor> entry : producerBatchExecutorHashMap.entrySet()) {
            ProducerKafkaBatchExecutor task = entry.getValue();
            batchStatuses.add(new BatchStatus(entry.getKey(), PRODUCER, task.getTaskCount()));
        }
        return batchStatuses;
    }

}
