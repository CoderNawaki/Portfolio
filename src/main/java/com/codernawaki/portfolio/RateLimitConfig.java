package com.codernawaki.portfolio;

import io.lettuce.core.RedisClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnExpression("T(org.springframework.util.StringUtils).hasText('${spring.data.redis.host:}')")
    public RedisClient redisClient(@Value("${spring.data.redis.host:}") String host,
                                   @Value("${spring.data.redis.port:6379}") int port) {
        return RedisClient.create(String.format("redis://%s:%d", host, port));
    }
}
