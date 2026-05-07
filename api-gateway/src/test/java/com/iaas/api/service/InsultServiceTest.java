package com.iaas.api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iaas.api.cache.InsultCacheService;
import com.iaas.api.client.PythonServiceClient;
import com.iaas.api.exception.DownstreamException;
import com.iaas.api.model.InsultRequest;
import com.iaas.api.model.InsultResponse;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class InsultServiceTest {

    @Mock
    private InsultCacheService cache;

    @Mock
    private PythonServiceClient pythonClient;

    @InjectMocks
    private InsultService insultService;

    @Test
    void returnsFromCacheWithoutCallingPython(){
        InsultResponse insultResponse = new InsultResponse();
        insultResponse.setInsult("cached insult");

        InsultRequest insultRequest = new InsultRequest();
        insultRequest.setName("John");
        insultRequest.setCharacteristics(List.of("slow", "arrogant"));

        when(cache.get(any())).thenReturn(Mono.just(insultResponse));

        StepVerifier.create(insultService.getInsult(insultRequest))
            .expectNext(insultResponse)
            .verifyComplete();

        verify(pythonClient, never()).generateInsult(any());

    }

    @Test
    void callsPythonAndCachesResultOnCacheMiss(){
        InsultResponse insultResponse = new InsultResponse();
        insultResponse.setInsult("cached insult");

        InsultRequest insultRequest = new InsultRequest();
        insultRequest.setName("John");
        insultRequest.setCharacteristics(List.of("slow", "arrogant"));

        when(cache.get(any())).thenReturn(Mono.empty());
        when(pythonClient.generateInsult(any())).thenReturn(Mono.just(insultResponse));
        when(cache.put(any(), any())).thenReturn(Mono.just(true));


        StepVerifier.create(insultService.getInsult(insultRequest))
            .expectNext(insultResponse)
            .verifyComplete();

        verify(pythonClient).generateInsult(any());
        verify(cache).put(any(), eq(insultResponse));

    }

    @Test
    void propagatesErrorWhenPythonFails(){
        InsultResponse insultResponse = new InsultResponse();
        insultResponse.setInsult("cached insult");

        InsultRequest insultRequest = new InsultRequest();
        insultRequest.setName("John");
        insultRequest.setCharacteristics(List.of("slow", "arrogant"));

        when(cache.get(any())).thenReturn(Mono.empty());
        when(pythonClient.generateInsult(any()))
        .thenReturn(Mono.error(new DownstreamException("Python service error: 500")));
    
        StepVerifier.create(insultService.getInsult(insultRequest))
            .expectError(DownstreamException.class)
            .verify();
    }
}
