package com.iaas.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.iaas.api.model.InsultResponse;

@Configuration
public class RedisConfig {
    
    @Bean
    public ReactiveRedisTemplate<String, InsultResponse> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory){
        var valueSerializer = new Jackson2JsonRedisSerializer<>(InsultResponse.class);
        var context = RedisSerializationContext
        .<String, InsultResponse> newSerializationContext(new StringRedisSerializer())
        .value(valueSerializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
