package aiven.io.kafka_executor.data.avro;

import aiven.io.kafka_executor.data.DataClass;
import aiven.io.kafka_executor.data.DataInterface;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.util.Utf8;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;

@Slf4j
public class AvroUtils {
    private AvroUtils() {
    }

    public static Schema generateSchema(Class<?> clazz) {
        SchemaBuilder.RecordBuilder<Schema> recordBuilder = SchemaBuilder.record(clazz.getSimpleName());
        SchemaBuilder.FieldAssembler<Schema> fieldsAssembler = recordBuilder.fields();

        for (Field field : clazz.getDeclaredFields()) {
            Schema fieldSchema;
            if (Modifier.isStatic(field.getModifiers())) {
                continue; // Skip static fields
            }

            if (field.getType() == Date.class) {
                // Create the schema for the date field with logical type
                fieldSchema = LogicalTypes.timestampMillis().addToSchema(Schema.create(Schema.Type.LONG));
                //fieldSchema = ReflectData.get().getSchema(Schema.Type.LONG);
            } else {
                fieldSchema = ReflectData.get().getSchema(field.getType());
            }
            fieldsAssembler.name(field.getName()).type(fieldSchema).noDefault();
        }

        return fieldsAssembler.endRecord();
    }

    public static GenericRecord serializeToAvro(DataInterface data, Schema schema) {
        GenericRecord record = new GenericData.Record(schema);
        for (Schema.Field field : schema.getFields()) {
            try {
                Field dataField = data.getClass().getDeclaredField(field.name());
                dataField.setAccessible(true);
                Object value = dataField.get(data);
                if (value instanceof Date) {
                    value = ((Date) value).getTime();
                }
                record.put(field.name(), value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to set field " + field.name(), e);
            }
        }
        return record;
    }

    public static DataInterface generateData(GenericRecord record, DataClass dataClass) {
        DataInterface dataInterface = null;
        try {
            dataInterface = dataClass.getDataInterfaceClass().getConstructor().newInstance();
        } catch (Exception e) {
            log.error("Error instantiating data interface", e);
            return null;
        }
        for (Schema.Field field : dataInterface.retAvroSchema().getFields()) {
            Object value = record.get(field.name());

            // Convert milliseconds since epoch to Date for Date fields
            if (value instanceof Long && field.schema().getLogicalType() != null) {
                if ("timestamp-millis".equals(field.schema().getLogicalType().getName())) {
                    value = new Date((Long) value);
                }
            } else if (value instanceof Utf8) {
                value = ((Utf8) value).toString();
            }

            try {
                Field dataField = dataInterface.getClass().getDeclaredField(field.name());
                dataField.setAccessible(true);
                dataField.set(dataInterface, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to set field " + field.name(), e);
            }
        }
        return dataInterface;
    }

}
