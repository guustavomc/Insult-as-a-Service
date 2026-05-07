package com.iaas.api.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.iaas.api.exception.DownstreamException;
import com.iaas.api.model.InsultRequest;
import com.iaas.api.model.InsultResponse;

import reactor.core.publisher.Mono;

@Component
public class PythonServiceClient {

    private final WebClient webClient;

    public PythonServiceClient(WebClient pythonServiceWebClient){
        this.webClient = pythonServiceWebClient;
    }

    public Mono<InsultResponse> generateInsult(InsultRequest request){
        return webClient.post()
                        .uri("/insult")
                        .bodyValue(request)
                        .retrieve()
                        .onStatus(
                            status -> status.isError(), 
                            response -> response.bodyToMono(String.class)
                            .flatMap(
                                body -> Mono.error(new DownstreamException("Python service error: " + body)))
                        )
                        .bodyToMono(InsultResponse.class);
    }

}
