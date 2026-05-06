package com.iaas.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.iaas.api.exception.GlobalExceptionHandler;
import com.iaas.api.model.InsultResponse;
import com.iaas.api.service.InsultService;

import reactor.core.publisher.Mono;

@WebFluxTest(InsultController.class)
@Import(GlobalExceptionHandler.class)
public class InsultControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private InsultService insultService;

    @Test
    void retursHTTP200WithInsultOnValidRequest(){
        InsultResponse insultResponse = new InsultResponse();
        insultResponse.setInsult("You magnificent slow-witted walnut.");
    
        when(insultService.getInsult(any())).thenReturn(Mono.just(insultResponse));

        webTestClient.post()
                .uri("/api/iaas/insult")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"name": "John", "characteristics": ["slow", "arrogant"]}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.insult").isEqualTo("You magnificent slow-witted walnut.");
    }
}
