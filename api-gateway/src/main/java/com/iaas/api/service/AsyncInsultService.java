package com.iaas.api.service;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import com.iaas.api.model.InsultRequest;
import com.iaas.api.queue.InsultStreamPublisher;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Service
public class AsyncInsultService {

     private final InsultStreamPublisher publisher;
    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public AsyncInsultService(InsultStreamPublisher publisher,
                               ReactiveRedisTemplate<String, String> reactiveStringRedisTemplate) {
        this.publisher = publisher;
        this.redisTemplate = reactiveStringRedisTemplate;
    }

    public Mono<String> submit(InsultRequest request) {
        String jobId = UUID.randomUUID().toString();
        return publisher.publish(jobId, request);
    }

    public Mono<String> getResult(String jobId) {
        return redisTemplate.opsForValue().get("result:" + jobId);
    }

}
