package com.ballon.global.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager mgr = new CaffeineCacheManager();
        // 등록할 캐시 이름들
        mgr.setCacheNames(List.of("catTree", "partnerCats"));

        // 모든 캐시에 동일 적용될 정책
        mgr.setCaffeine(Caffeine.newBuilder()
                .maximumSize(200_000)
                .expireAfterWrite(Duration.ofHours(6))
                .recordStats());
        return mgr;
    }
}