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
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;


@Service
public class InsultCacheService {

    private final ReactiveRedisTemplate<String, InsultResponse> redisTemplate;

    private final Duration ttl;

    private final Counter cacheHits;
    private final Counter cacheMisses;
    
    public InsultCacheService(ReactiveRedisTemplate<String, InsultResponse> redisTemplate,
        @Value("${cache.ttl-seconds:300}") long ttlSeconds, MeterRegistry meterRegistry){
            this.redisTemplate = redisTemplate;
            this.ttl = Duration.ofSeconds(ttlSeconds);
            this.cacheHits   = Counter.builder("insult.cache.hits").register(meterRegistry);
            this.cacheMisses = Counter.builder("insult.cache.misses").register(meterRegistry);
    }
    
    public Mono<InsultResponse> get(InsultRequest request) {
        return redisTemplate.opsForValue().get(cacheKey(request))
            .doOnSuccess(v -> {
                if (v != null) cacheHits.increment();
                else           cacheMisses.increment();
            });
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
