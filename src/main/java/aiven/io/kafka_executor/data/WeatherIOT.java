package aiven.io.kafka_executor.data;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.reflect.ReflectData;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static aiven.io.kafka_executor.data.protobuf.ProtobufUtils.getDescriptorFromPojo;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Data
public class WeatherIOT implements DataInterface{
    private static final Faker faker = new Faker();
    private static final Schema schema = ReflectData.get().getSchema(WeatherIOT.class);
    public static final Descriptors.Descriptor protoSchema;
    static {
        Descriptors.Descriptor protoSchemaTmp = null;
        try {
            protoSchemaTmp = getDescriptorFromPojo(WeatherIOT.class);
        }catch (Exception ex){
            log.error("Failed to load proto schema", ex);
        } finally {
            protoSchema = protoSchemaTmp;
            log.debug("Loaded proto schema: {}", protoSchema.toProto().toString());
        }
    }

    private static final List<String> directions = List.of("N", "NE", "E", "SE", "S", "SW", "W", "NW");
    private static final List<Account> ACCOUNTS = new ArrayList<>();

    @AllArgsConstructor
    private static final class Account {
        int account;
        String city;
        String state;
    }

    private long id;
    private int account;
    private String city;
    private String state;
    private Date captureTime;
    private double temperature;
    private double humidity;
    private double pressure;
    private double windSpeed;
    private String windDirection;


    @Override
    public Descriptors.Descriptor retProtoSchema() {
        return protoSchema;
    }

    @Override
    public Schema retAvroSchema() {
        return schema;
    }

    public DataInterface generateData(long genId, int relativeItem) {
        if(genId == -1 && relativeItem == -1) {
            return new WeatherIOT(faker.random().nextLong(),
                    faker.number().positive(),
                    faker.address().city(),
                    faker.address().state(),
                    new Date(),  //record current time
                    faker.number().randomDouble(2,-10,38),
                    faker.number().randomDouble(2,0,100),
                    29.6 + (faker.number().randomDouble(1,1,6) / 10),
                    faker.number().randomDouble(1,0,20),
                    directions.get(faker.random().nextInt(directions.size())));
        }
        else if(genId != -1) {
            return new WeatherIOT(genId,
                    faker.number().positive(),
                    faker.address().city(),
                    faker.address().state(),
                    new Date(),  //record current time
                    faker.number().randomDouble(2,-10,38),
                    faker.number().randomDouble(2,0,100),
                    29.6 + (faker.number().randomDouble(1,1,6) / 10),
                    faker.number().randomDouble(1,0,20),
                    directions.get(faker.random().nextInt(directions.size())));
        } else {
            while (ACCOUNTS.size() < relativeItem) {
                ACCOUNTS.add(new Account(faker.number().positive(), faker.address().city(), faker.address().state()));
            }
            Account account1 = ACCOUNTS.get(relativeItem);
            return (new WeatherIOT(
                    genId,
                    account1.account,
                    account1.city,
                    account1.state,
                    new Date(),
                    faker.number().randomDouble(2, -10, 38),
                    faker.number().randomDouble(2, 0, 100),
                    29.6 + (faker.number().randomDouble(1, 1, 6) / 10),
                    faker.number().randomDouble(1, 0, 20),
                    directions.get(faker.random().nextInt(directions.size()))));
        }
    }

    @Override
    public DataInterface generateData(GenericData.Record record) {
        WeatherIOT weatherIOT = new WeatherIOT();
        for (Schema.Field field : schema.getFields()) {
            ReflectData.get().setField(weatherIOT, field.name(), field.pos(), record.get(field.name()));
        }
        return weatherIOT;
    }

    @Override
    public DataInterface generateData(DynamicMessage dynamicMessage) {
        WeatherIOT weatherIOT = new WeatherIOT();
        Map<Descriptors.FieldDescriptor, Object> fields = dynamicMessage.getAllFields();
        for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : fields.entrySet()) {
            Descriptors.FieldDescriptor fieldDescriptor = entry.getKey();
            Object value = entry.getValue();
            String fieldName = fieldDescriptor.getName();
            Field pojoField = null;
            try {
                pojoField = WeatherIOT.class.getDeclaredField(fieldName);
                pojoField.setAccessible(true);
                pojoField.set(weatherIOT, value);
            } catch (Exception e) {
                //May want to let this silently fail...  Can change to log.debug.
                log.info("Failed to set field {}", fieldName, e);
            }
        }
        return weatherIOT;

    }
}
