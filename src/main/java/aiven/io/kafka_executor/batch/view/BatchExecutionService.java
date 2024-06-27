package aiven.io.kafka_executor.batch.view;

import aiven.io.kafka_executor.batch.model.BatchStatus;
import aiven.io.kafka_executor.config.model.ConnectionConfig;
import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.log.model.Statistics;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BatchExecutionService {
    private static final Map<String, ConsumerBatchExecutor> consumerBatchExecutorHashMap = new HashMap<>();
    private static final Map<String, ProducerBatchExecutor> producerBatchExecutorHashMap = new HashMap<>();
    public static final String CONSUMER = "Consumer";
    public static final String PRODUCER = "Producer";
    private final ConnectionConfig connectionConfig;
    private final Statistics statistics;


    BatchExecutionService(ConnectionConfig connectionConfig, Statistics statistics) {
        this.connectionConfig = connectionConfig;
        this.statistics = statistics;
    }

    public boolean createConsumerTask(String batchName, String topic, DataClass dataClass, int batchSize, int maxTries,
                                      long sleepMillis, int numThreads){
        ConsumerBatchExecutor consumerBatchExecutor = consumerBatchExecutorHashMap.get(batchName);
        if (consumerBatchExecutor == null) {
            consumerBatchExecutorHashMap.put( batchName,
                    new ConsumerBatchExecutor(topic, dataClass, batchSize, maxTries, connectionConfig, sleepMillis,
                            statistics, numThreads));
            return true;
        }
        return false;
    }

    public boolean createProducerTask(String batchName, String topic, DataClass dataClass, int batchSize, long startId,
                                      int correlatedStartIdInc, int correlatedEndIdInc, long sleepMillis, int numThreads){
        ProducerBatchExecutor producerBatchExecutor = producerBatchExecutorHashMap.get(batchName);
        if (producerBatchExecutor == null) {
            producerBatchExecutorHashMap.put( batchName,
                    new ProducerBatchExecutor(topic, dataClass, batchSize, startId, correlatedStartIdInc,
                            correlatedEndIdInc, connectionConfig, sleepMillis, statistics, numThreads));
            return true;
        }
        return false;
    }


    public boolean dropConsumerTask(String batchName){
        ConsumerBatchExecutor consumerBatchExecutor = consumerBatchExecutorHashMap.get(batchName);
        if (consumerBatchExecutor != null) {
            consumerBatchExecutor.stopTasks();
            consumerBatchExecutorHashMap.remove(batchName);
            return true;
        }
        return false;
    }

    public boolean dropProducerTask(String batchName){
        ProducerBatchExecutor producerBatchExecutor = producerBatchExecutorHashMap.get(batchName);
        if (producerBatchExecutor != null) {
            producerBatchExecutor.stopTasks();
            producerBatchExecutorHashMap.remove(batchName);
            return true;
        }
        return false;
    }


    public boolean changeConsumerTaskCount(String batchName, int numThreads){
        ConsumerBatchExecutor consumerBatchExecutor = consumerBatchExecutorHashMap.get(batchName);
        if (consumerBatchExecutor != null) {
            consumerBatchExecutor.changeTaskCount(numThreads);
            return true;
        }
        return false;
    }

    public boolean changeProducerTaskCount(String batchName, int numThreads){
        ProducerBatchExecutor producerBatchExecutor = producerBatchExecutorHashMap.get(batchName);
        if (producerBatchExecutor != null) {
            producerBatchExecutor.changeTaskCount(numThreads);
            return true;
        }
        return false;
    }

    public List<String> getConsumerBatchNames(){
        return consumerBatchExecutorHashMap.keySet().stream().toList();
    }

    public List<String> getProducerBatchNames(){
        return producerBatchExecutorHashMap.keySet().stream().toList();
    }

    public void stopAllTasks() {
        for (Map.Entry<String, ConsumerBatchExecutor> entry : consumerBatchExecutorHashMap.entrySet()) {
            ConsumerBatchExecutor task = entry.getValue();
            task.stopTasks();
        }
        consumerBatchExecutorHashMap.clear();
        for (Map.Entry<String, ProducerBatchExecutor> entry : producerBatchExecutorHashMap.entrySet()) {
            ProducerBatchExecutor task = entry.getValue();
            task.stopTasks();
        }
        producerBatchExecutorHashMap.clear();

    }

    public List<BatchStatus> getBatchStatuses() {
        List<BatchStatus> batchStatuses = new ArrayList<>();
        for (Map.Entry<String, ConsumerBatchExecutor> entry : consumerBatchExecutorHashMap.entrySet()) {
            ConsumerBatchExecutor task = entry.getValue();
            batchStatuses.add(new BatchStatus(entry.getKey(), CONSUMER, task.getTaskCount()));
        }
        for (Map.Entry<String, ProducerBatchExecutor> entry : producerBatchExecutorHashMap.entrySet()) {
            ProducerBatchExecutor task = entry.getValue();
            batchStatuses.add(new BatchStatus(entry.getKey(), PRODUCER, task.getTaskCount()));
       }
        return batchStatuses;
    }

}
