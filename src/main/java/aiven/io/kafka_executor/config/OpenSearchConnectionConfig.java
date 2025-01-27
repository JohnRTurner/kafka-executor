package aiven.io.kafka_executor.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@NoArgsConstructor
@Data
@Slf4j
public class OpenSearchConnectionConfig {
    @Value("${kafka_executor.opensearch_enable}")
    private boolean enable;
    /* Basic Connection */
    @Value("${kafka_executor.opensearch_host}")
    private String host;
    @Value("${kafka_executor.opensearch_port}")
    private int port;
    @Value("${kafka_executor.opensearch_user}")
    private String user;
    @Value("${kafka_executor.opensearch_password}")
    private String password;

    private RestHighLevelClient client;

    public RestHighLevelClient getClient() {
        if (client == null) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(this.user, this.password));

            RestClientBuilder builder = RestClient.builder(new HttpHost(this.host, this.port, "https"))
                    .setHttpClientConfigCallback(httpClientBuilder ->
                            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

            client = new RestHighLevelClient(builder);
        }
        return client;
    }

    public void closeClient() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                log.warn("Error closing OpenSearch rest client", e);
            } finally {
                client = null;
            }
        }
    }

    public void loadConfig(OpenSearchConnectionDTO connectionDTO) {
        this.enable = connectionDTO.isEnable();
        this.host = connectionDTO.getHost();
        this.port = connectionDTO.getPort();
        this.user = connectionDTO.getUser();
        this.password = connectionDTO.getPassword();
        closeClient();  //reset client whenever this is called.
    }

    public OpenSearchConnectionDTO retConfig() {
        OpenSearchConnectionDTO connectionDTO = new OpenSearchConnectionDTO();
        connectionDTO.setEnable(this.enable);
        connectionDTO.setHost(this.host);
        connectionDTO.setPort(this.port);
        connectionDTO.setUser(this.user);
        connectionDTO.setPassword(this.password);
        return connectionDTO;
    }
}
