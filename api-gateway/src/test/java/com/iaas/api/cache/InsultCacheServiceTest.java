package com.iaas.api.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;

import com.iaas.api.model.InsultRequest;
import com.iaas.api.model.InsultResponse;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@ExtendWith(MockitoExtension.class)
public class InsultCacheServiceTest {

    @Mock
    private ReactiveRedisTemplate<String, InsultResponse> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, InsultResponse> valueOperations;

    private InsultCacheService insultCacheService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        // constructed manually because @InjectMocks can't inject @Value constructor params
        insultCacheService = new InsultCacheService(redisTemplate, 300);
    }


    @Test
    void returnsValueOnCacheHit(){
        InsultResponse insultResponse = new InsultResponse();
        insultResponse.setInsult("cached insult");

        InsultRequest insultRequest = new InsultRequest();
        insultRequest.setName("John");
        insultRequest.setCharacteristics(List.of("slow", "arrogant"));

        when(valueOperations.get(any(String.class)))
        .thenReturn(Mono.just(insultResponse));

        StepVerifier.create(insultCacheService.get(insultRequest))
            .expectNext(insultResponse)
            .verifyComplete();
    }

    @Test
    void returnsEmptyOnCacheMiss(){
        InsultResponse insultResponse = new InsultResponse();
        insultResponse.setInsult("cached insult");

        InsultRequest insultRequest = new InsultRequest();
        insultRequest.setName("John");
        insultRequest.setCharacteristics(List.of("slow", "arrogant"));

        when(valueOperations.get(any(String.class))).thenReturn(Mono.empty());

        StepVerifier.create(insultCacheService.get(insultRequest))
            .verifyComplete();
    }

    @Test
    void storesValueWithConfiguredTtl(){
        InsultResponse insultResponse = new InsultResponse();
        insultResponse.setInsult("fresh insult");

        InsultRequest insultRequest = new InsultRequest();
        insultRequest.setName("John");
        insultRequest.setCharacteristics(List.of("slow", "arrogant"));

       when(valueOperations.set(any(String.class), eq(insultResponse), any(Duration.class)))
                .thenReturn(Mono.just(true));

        StepVerifier.create(insultCacheService.put(insultRequest, insultResponse))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<Duration> ttlCaptor = ArgumentCaptor.forClass(Duration.class);
        verify(valueOperations).set(any(), any(),  ttlCaptor.capture());
        assertThat(ttlCaptor.getValue()).isEqualTo(Duration.ofSeconds(300));

    }

    @Test
    void sameInputAlwaysProducesSameCacheKey(){
        InsultResponse insultResponse = new InsultResponse();
        insultResponse.setInsult("insult:");

        InsultRequest insultRequest = new InsultRequest();
        insultRequest.setName("John");
        insultRequest.setCharacteristics(List.of("slow", "arrogant"));

        when(valueOperations.get(any(String.class)))
                .thenReturn(Mono.just(insultResponse));

        insultCacheService.get(insultRequest).block();
        insultCacheService.get(insultRequest).block();

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations, times(2)).get(keyCaptor.capture());

        List<String> keys = keyCaptor.getAllValues();
        assertThat(keys.get(0)).isEqualTo(keys.get(1));
        assertThat(keys.get(0)).startsWith("insult:");

    }

    @Test
    void differentInputsProduceDifferentCacheKeys(){
        when(valueOperations.get(any(String.class)))
                .thenReturn(Mono.empty());

        InsultResponse insultResponse1 = new InsultResponse();
        insultResponse1.setInsult("insult:");

        InsultRequest insultRequest1 = new InsultRequest();
        insultRequest1.setName("John");
        insultRequest1.setCharacteristics(List.of("slow"));

        InsultResponse insultResponse2 = new InsultResponse();
        insultResponse2.setInsult("insult:");

        InsultRequest insultRequest2 = new InsultRequest();
        insultRequest2.setName("Jane");
        insultRequest2.setCharacteristics(List.of("rude"));


        insultCacheService.get(insultRequest1).block();
        insultCacheService.get(insultRequest2).block();

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations, times(2)).get(keyCaptor.capture());

        List<String> keys = keyCaptor.getAllValues();
        assertThat(keys.get(0)).isNotEqualTo(keys.get(1));
    }
}
