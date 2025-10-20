package exemplarservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestInterceptor authInterceptor) {
        RestTemplate rt = new RestTemplate();
        rt.getInterceptors().add(authInterceptor);
        return rt;
    }

    @Bean
    public ClientHttpRequestInterceptor authInterceptor() {
        return (request, body, execution) -> {
            String token = "YOUR_ACTUAL_TOKEN_HERE";
            request.getHeaders().setBearerAuth(token);
            return execution.execute(request, body);
        };
    }
}
