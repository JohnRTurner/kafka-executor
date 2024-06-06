package aiven.io.kafka_executor.data;

import aiven.io.kafka_executor.data.avro.AvroUtils;
import com.google.protobuf.Descriptors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static aiven.io.kafka_executor.data.protobuf.ProtobufUtils.getDescriptorFromPojo;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Data
public class WeatherIOT implements DataInterface{
    private static final int MAX_CORRELATED_IDS = 20000; //Protects us from blowing out memory
    private static final Faker faker = new Faker();
    private static final Schema schema = AvroUtils.generateSchema(WeatherIOT.class);
//    private static final Schema schema = ReflectData.get().getSchema(WeatherIOT.class);
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

    public DataInterface generateData(long genId, int correlatedId) {
        Account account1;
        if(correlatedId >= 0) {
            while (ACCOUNTS.size() <= correlatedId % MAX_CORRELATED_IDS) {
                ACCOUNTS.add(new Account(faker.number().positive(), faker.address().city(), faker.address().state()));
            }
            account1 = ACCOUNTS.get(correlatedId % MAX_CORRELATED_IDS);
        }else {
            account1 = new Account(faker.number().positive(), faker.address().city(), faker.address().state());
        }
        return (new WeatherIOT(
                (genId >= 0)? genId:faker.random().nextLong(),
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
