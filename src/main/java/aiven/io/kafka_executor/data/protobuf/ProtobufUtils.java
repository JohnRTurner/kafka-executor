package aiven.io.kafka_executor.data.protobuf;


import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import lombok.extern.slf4j.Slf4j;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class ProtobufUtils {
    private ProtobufUtils() {}

    private static final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static Descriptors.Descriptor getDescriptorFromPojo(Class<?> clazz) throws Descriptors.DescriptorValidationException {
        DescriptorProtos.DescriptorProto.Builder descriptorProtoBuilder = DescriptorProtos.DescriptorProto.newBuilder();

        descriptorProtoBuilder.setName(clazz.getSimpleName());
        int fieldNumber = 1;

        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue; // Skip static fields
            }
            DescriptorProtos.FieldDescriptorProto.Builder fieldDescriptorBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder();
            fieldDescriptorBuilder.setName(field.getName());
            fieldDescriptorBuilder.setNumber(fieldNumber++);
            fieldDescriptorBuilder.setLabel(DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL);

            Class<?> fieldType = field.getType();
            if (fieldType == String.class) {
                fieldDescriptorBuilder.setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING);
            } else if (fieldType == int.class || fieldType == Integer.class) {
                fieldDescriptorBuilder.setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32);
            } else if (fieldType == long.class || fieldType == Long.class) {
                fieldDescriptorBuilder.setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT64);
            } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                fieldDescriptorBuilder.setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL);
            } else if (fieldType == float.class || fieldType == Float.class) {
                fieldDescriptorBuilder.setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FLOAT);
            } else if (fieldType == double.class || fieldType == Double.class) {
                fieldDescriptorBuilder.setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_DOUBLE);
            } else if (fieldType == Date.class) {
                fieldDescriptorBuilder.setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING);
                //fieldDescriptorBuilder.setTypeName(Timestamp.getDescriptor().getFullName());
            } else {
                throw new UnsupportedOperationException("Unsupported field type: " + fieldType);
            }

            descriptorProtoBuilder.addField(fieldDescriptorBuilder);
        }

        DescriptorProtos.FileDescriptorProto fileDescriptorProto = DescriptorProtos.FileDescriptorProto.newBuilder()
                .addMessageType(descriptorProtoBuilder)
                .setName("DynamicProto")
                .build();

        Descriptors.FileDescriptor fileDescriptor = Descriptors.FileDescriptor.buildFrom(fileDescriptorProto, new Descriptors.FileDescriptor[0]);
        return fileDescriptor.getMessageTypes().get(0);
    }

    public static DynamicMessage serializeToProtobuf(Object object, Descriptors.Descriptor descriptor) {
        DynamicMessage.Builder messageBuilder = DynamicMessage.newBuilder(descriptor);

        for (Field field : object.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue; // Skip static fields
            }
            field.setAccessible(true);
            String fieldName = field.getName();
            Object fieldValue = null;
            try {
                fieldValue = field.get(object);
            } catch (IllegalAccessException e) {
                log.error("Error trying to get value for field {}", fieldName, e);
            }
            Descriptors.FieldDescriptor fieldDescriptor = descriptor.findFieldByName(fieldName);
            if (fieldDescriptor != null && fieldValue != null) {
                if (fieldValue instanceof Date) {
                    messageBuilder.setField(fieldDescriptor, df.format((Date) fieldValue));
                } else {
                    messageBuilder.setField(fieldDescriptor, fieldValue);
                }
            }
        }

        return messageBuilder.build();
    }
}
