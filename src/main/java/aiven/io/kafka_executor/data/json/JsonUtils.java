package aiven.io.kafka_executor.data.json;

import aiven.io.kafka_executor.data.DataInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.common.xcontent.XContentFactory;
import org.opensearch.core.xcontent.XContentBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.Date;

@Slf4j
public class JsonUtils {
    /* holder class if need additional JSON tools, however appears that JSON works without it */
    private JsonUtils() {
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String convertToJson(DataInterface dataInterface) {
        try {
            return objectMapper.writeValueAsString(dataInterface);
        } catch (JsonProcessingException e) {
            log.warn("Error generating data", e);
            return dataInterface.toString();
        }
    }

    public static XContentBuilder generateMapping(Class<?> clazz) {
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {
                builder.startObject("properties");
                {
                    for (Field field : clazz.getDeclaredFields()) {
                        if (Modifier.isStatic(field.getModifiers())) {
                            continue; // Skip static fields
                        }
                        field.setAccessible(true);
                        String fieldName = field.getName();
                        Class<?> fieldType = field.getType();

                        builder.startObject(fieldName);
                        {
                            if (fieldType == String.class) {
                                builder.field("type", "text");
                            } else if (fieldType == int.class || fieldType == Integer.class) {
                                builder.field("type", "integer");
                            } else if (fieldType == long.class || fieldType == Long.class) {
                                builder.field("type", "long");
                            } else if (fieldType == float.class || fieldType == Float.class) {
                                builder.field("type", "float");
                            } else if (fieldType == double.class || fieldType == Double.class) {
                                builder.field("type", "double");
                            } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                                builder.field("type", "boolean");
                            } else if (fieldType == Date.class || fieldType == Timestamp.class) {
                                builder.field("type", "date");
                                builder.field("format", "strict_date_optional_time||epoch_millis");
                            }
                            // Add more types as needed
                        }
                        builder.endObject();
                    }
                }
                builder.endObject();
            }
            builder.endObject();
            return builder;
        } catch (Exception e) {
            log.error("Error generating mapping", e);
            return null;
        }
    }

}
