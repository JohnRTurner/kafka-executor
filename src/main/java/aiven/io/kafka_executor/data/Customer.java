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
import java.util.Map;

import static aiven.io.kafka_executor.data.protobuf.ProtobufUtils.getDescriptorFromPojo;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Data
public class Customer implements DataInterface {
    private static final Faker faker = new Faker();
    private static final Schema schema = ReflectData.get().getSchema(Customer.class);
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
    public DataInterface generateData(long genId, int relativeItem) {
        if(genId == -1 && relativeItem == -1){
            boolean gender = faker.bool().bool();
            return new Customer( faker.random().nextLong(),
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
        else if(genId != -1) {
            boolean gender = faker.bool().bool();
            return new Customer( genId,
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
        else {
            boolean gender = faker.bool().bool();
            log.warn("Invalid genId: {} with relativeItem: {}", genId, relativeItem);
            return new Customer( faker.random().nextLong(),
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

    @Override
    public DataInterface generateData(GenericData.Record record) {
        Customer customer = new Customer();
        for (Schema.Field field : schema.getFields()) {
            ReflectData.get().setField(customer, field.name(), field.pos(), record.get(field.name()));
        }
        return customer;
    }

    @Override
    public DataInterface generateData(DynamicMessage dynamicMessage) {
        Customer customer = new Customer();
        Map<Descriptors.FieldDescriptor, Object> fields = dynamicMessage.getAllFields();
        for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : fields.entrySet()) {
            Descriptors.FieldDescriptor fieldDescriptor = entry.getKey();
            Object value = entry.getValue();
            String fieldName = fieldDescriptor.getName();
            Field pojoField = null;
            try {
                pojoField = Customer.class.getDeclaredField(fieldName);
                pojoField.setAccessible(true);
                pojoField.set(customer, value);
            } catch (Exception e) {
                //May want to let this silently fail...  Can change to log.debug.
                log.info("Failed to set field {}", fieldName, e);
            }
        }
        return customer;
    }

}
