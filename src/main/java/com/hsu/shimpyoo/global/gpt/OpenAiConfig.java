package com.hsu.shimpyoo.global.gpt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAiConfig {
    @Value("${gpt.api.key}")
    private String apiKey;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(); // http 요청을 간편하게 수행하도록 돕는 클래스

        // RestTemplate의 요청 인터셉터 목록 반환 -> 인터셉터는 http 요청 전후에 특정 작업을 수행할 수 있도록 도움
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + apiKey);
            return execution.execute(request, body);
        });
        return restTemplate;
    }

}