package aiven.io.kafka_executor.config;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpenSearchConnectionDTO {
    private boolean enable;
    private String host;
    private int port;
    private String user;
    private String password;

}
