package aiven.io.kafka_executor.data;

import aiven.io.kafka_executor.data.avro.AvroUtils;
import com.google.protobuf.Descriptors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.apache.avro.Schema;
import org.springframework.stereotype.Component;

import static aiven.io.kafka_executor.data.protobuf.ProtobufUtils.getDescriptorFromPojo;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Data
public class Customer implements DataInterface {
    private static final Faker faker = new Faker();
    private static final Schema schema = AvroUtils.generateSchema(Customer.class);

    public static final Descriptors.Descriptor protoSchema;
    static {
        Descriptors.Descriptor protoSchemaTmp = null;
        try {
            protoSchemaTmp = getDescriptorFromPojo(Customer.class);
        }catch (Exception ex){
            log.error("Failed to load proto schema", ex);
        } finally {
            protoSchema = protoSchemaTmp;
            log.debug("Loaded proto schema: {}", protoSchema.toProto().toString());

        }
    }

    private long id;
    private String firstName;
    private String lastName;
    private String gender;
    private int age;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zip;


    @Override
    public Descriptors.Descriptor retProtoSchema() {
        return protoSchema;
    }

    @Override
    public Schema retAvroSchema() {
        return schema;
    }

    @Override
    public DataInterface generateData(long genId, int correlatedId) {
        boolean gender = faker.bool().bool();
        if(correlatedId >= 0 ){
            // since relativeItem isn't implemented for this class, add to debug log and ignore
            log.debug("Generating data for genId: {}, correlatedId: {} but correlatedId is not implemented.", genId, correlatedId);
        }
        return new Customer( (genId >= 0)?genId:faker.random().nextLong(),
                (gender)?faker.name().malefirstName() : faker.name().femaleFirstName(),
                faker.name().lastName(),
                (gender)?"Male":"Female",
                faker.random().nextInt(18,80),
                faker.internet().emailAddress(),
                faker.phoneNumber().phoneNumber(),
                faker.address().streetAddress(),
                faker.address().city(),
                faker.address().state(),
                faker.address().zipCode()
        );
    }
}
