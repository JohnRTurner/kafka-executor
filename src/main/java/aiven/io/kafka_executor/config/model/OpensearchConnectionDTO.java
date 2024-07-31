package aiven.io.kafka_executor.config.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpensearchConnectionDTO {
    private boolean enable;
    private String host;
    private int port;
    private String user;
    private String password;

}
