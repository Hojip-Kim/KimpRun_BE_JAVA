package kimp.config.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${cmc.api.key}")
    private String cmcApiKey;
    @Value("${cmc.api.url}")
    private String cmcApiUrl;

    @Value("${cdn.cloudflare.api.token}")
    private String cdnApiToken;
    @Value("${cdn.cloudflare.api.url}")
    private String cdnApiUrl;

    @Bean
    public RestClient.Builder restClientBuilder() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(30000);

        return RestClient.builder()
                .requestFactory(requestFactory);
    }

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

    @Bean
    public RestClient coinMarketCapClient(){
        return RestClient.builder()
                .baseUrl(cmcApiUrl)
                .defaultHeader("X-CMC_PRO_API_KEY", cmcApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public RestClient cdnClient() {
        return RestClient.builder()
                .baseUrl(cdnApiUrl)
                .defaultHeader("Authorization", "Bearer " + cdnApiToken)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

}
