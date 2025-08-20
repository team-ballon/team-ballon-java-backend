package com.ballon.global.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@ConditionalOnProperty(name = "toss.enabled", havingValue = "true") //로컬 확인용 db접속위해(스키마 생성후 죽는것 해결)
public class WebClientConfig {
    @Value("${toss.payments.secret.key}")
    private String secretKey;

    @Bean
    public WebClient tossWebClient(WebClient.Builder builder) {
        String encodedKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return builder
                .baseUrl("https://api.tosspayments.com/v1/payments/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey)
                .build();
    }
}

