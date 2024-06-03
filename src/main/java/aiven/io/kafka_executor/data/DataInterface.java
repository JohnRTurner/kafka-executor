package aiven.io.kafka_executor.data;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

public interface DataInterface {
    /* Note: Must manually add any new implementation of this class to DataClass.java */
    long getId();
    Descriptors.Descriptor retProtoSchema();
    Schema retAvroSchema();
    DataInterface generateData(long genId, int relativeItem);
    DataInterface generateData(GenericData.Record record);
    DataInterface generateData(DynamicMessage dynamicMessage);
}
