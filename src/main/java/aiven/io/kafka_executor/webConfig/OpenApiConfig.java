package aiven.io.kafka_executor.webConfig;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${kafka_executor.version}")
    private String version;

    @Value("${kafka_executor.name}")
    private String name;


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title(name)
                        .description("""
                                This customizable load generator will create producers and consumers.
                                Features include controllable parallel execution of different workloads.
                                There is an easy-to-use Swagger front end that can be used to integrate
                                with other programming languages such as React, Angular, Python...
                                The intention is to use JMeter or even siege to run and monitor the tests
                                using simple http calls.
                                """)
                        .version(version)
                );
    }
}