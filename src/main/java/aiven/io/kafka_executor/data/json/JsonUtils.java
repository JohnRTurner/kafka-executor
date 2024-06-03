package aiven.io.kafka_executor.data.json;

import aiven.io.kafka_executor.data.DataInterface;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.reflect.ReflectData;

public class JsonUtils {
private JsonUtils() {   }
    public static GenericData.Record serializeToJson(DataInterface data, Schema schema) {
        GenericData.Record record = new GenericData.Record(schema);
        for (Schema.Field field : schema.getFields()) {
            record.put(field.name(), ReflectData.get().getField(data, field.name(), field.pos()));
        }
        return record;
    }

}
