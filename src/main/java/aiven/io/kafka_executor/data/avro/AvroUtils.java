package aiven.io.kafka_executor.data.avro;

import aiven.io.kafka_executor.data.DataInterface;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.reflect.ReflectData;

@Slf4j
public class AvroUtils {
    private AvroUtils() {}
    public static GenericData.Record serializeToAvro(DataInterface data, Schema schema) {
        GenericData.Record record = new GenericData.Record(schema);
        for (Schema.Field field : schema.getFields()) {
            record.put(field.name(), ReflectData.get().getField(data, field.name(), field.pos()));
        }
        return record;
    }

}
