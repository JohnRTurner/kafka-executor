package aiven.io.kafka_executor.log;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfiguration {

    @PostConstruct
    public void setLogLevel() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("org.apache.kafka.clients.consumer.internals.LegacyKafkaConsumer").setLevel(Level.WARN);
        loggerContext.getLogger("org.apache.kafka.clients.producer.internals.DefaultProducer").setLevel(Level.WARN);
        loggerContext.getLogger("org.apache.kafka.clients.admin.AdminClient").setLevel(Level.WARN);
    }
}