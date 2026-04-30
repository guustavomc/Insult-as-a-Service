package com.iaas.api.cache;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

import com.iaas.api.model.InsultRequest;
import com.iaas.api.model.InsultResponse;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;

@Service
public class InsultCacheService {

    private final ReactiveRedisTemplate<String, InsultResponse> redisTemplate;

    private final Duration ttl;
    
    public InsultCacheService(ReactiveRedisTemplate<String, InsultResponse> redisTemplate,
        @Value("${cache.ttl-seconds:300}") long ttlSeconds){
            this.redisTemplate = redisTemplate;
            this.ttl = Duration.ofSeconds(ttlSeconds);
    }
    
    public Mono<InsultResponse> get(InsultRequest request){
        return redisTemplate.opsForValue().get(cacheKey(request));
    }

    public Mono<Boolean> put(InsultRequest request, InsultResponse response) {
        return redisTemplate.opsForValue().set(cacheKey(request), response, ttl);
    }

    private String cacheKey(InsultRequest request) {
        String raw = request.getName() + ":" + request.getCharacteristics();
        try{
            byte[] hash = MessageDigest.getInstance("SHA-256")
            .digest(raw.getBytes(StandardCharsets.UTF_8));
            return "insult:" + HexFormat.of().formatHex(hash);
        } 
        catch(Exception exception){
            return "insult:" + raw.hashCode();
        }
    }

}
