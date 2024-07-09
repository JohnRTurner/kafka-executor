package aiven.io.kafka_executor.data;

import com.google.protobuf.Descriptors;
import org.apache.avro.Schema;

public interface DataInterface {
    /* Each record must have an id field */
    long getId();

    /* schema format created only once for Protobuf per class*/
    Descriptors.Descriptor retProtoSchema();

    /* schema format created only once for Avro per class*/
    Schema retAvroSchema();

    /*this generates the fake data*/
    DataInterface generateData(long genId, int correlatedId);
}
