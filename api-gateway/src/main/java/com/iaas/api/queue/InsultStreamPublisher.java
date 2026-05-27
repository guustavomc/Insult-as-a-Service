package com.iaas.api.queue;

import com.iaas.api.model.InsultRequest;

import java.util.Map;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class InsultStreamPublisher {

    private static final String STREAM_KEY = "insult-requests";

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public InsultStreamPublisher(ReactiveRedisTemplate<String, String> reactiveStringRedisTemplate) {
        this.redisTemplate = reactiveStringRedisTemplate;
    }

    public Mono<String> publish(String jobId, InsultRequest insultRequest){
        Map<String, String> fields = Map.of(
            "jobId",jobId,
            "name", insultRequest.getName(),
            "characteristics", String.join(",", insultRequest.getCharacteristics())
        );

        MapRecord<String, String, String> record = StreamRecords.mapBacked(fields).withStreamKey(STREAM_KEY);

        return redisTemplate.opsForStream().add(record).thenReturn(jobId);
    }

}
