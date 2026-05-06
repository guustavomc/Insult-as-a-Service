package com.iaas.api.service;

import org.springframework.stereotype.Service;

import com.iaas.api.cache.InsultCacheService;
import com.iaas.api.client.PythonServiceClient;
import com.iaas.api.model.*;

import reactor.core.publisher.Mono;

@Service
public class InsultService {

    private final InsultCacheService cache;
    private final PythonServiceClient pythonClient;

    public InsultService(InsultCacheService cache, PythonServiceClient pythonClient){
        this.cache = cache;
        this.pythonClient = pythonClient;
    }

    public Mono<InsultResponse> getInsult(InsultRequest request) {
    return cache.get(request)
            .switchIfEmpty(Mono.defer(() ->
                    pythonClient.generateInsult(request)
                            .flatMap(
                                response -> cache
                                    .put(request, response)
                                    .thenReturn(response))
            ));
    }
}
