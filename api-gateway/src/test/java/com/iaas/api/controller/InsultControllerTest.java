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

import com.iaas.api.exception.DownstreamException;
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
                        .expectStatus()
                        .isOk()
                        .expectBody()
                        .jsonPath("$.insult")
                        .isEqualTo("You magnificent slow-witted walnut.");
        }

        @Test
        void retursHTTP400WhenNameIsEmpty(){
                InsultResponse insultResponse = new InsultResponse();
                insultResponse.setInsult("You magnificent slow-witted walnut.");
        
                when(insultService.getInsult(any())).thenReturn(Mono.just(insultResponse));

                webTestClient.post()
                        .uri("/api/iaas/insult")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("""
                                {"name": "", "characteristics": ["slow", "arrogant"]}
                                """)
                        .exchange()
                        .expectStatus().isBadRequest();
        }

        @Test
        void retursHTTP400WhenNameMissing(){
                InsultResponse insultResponse = new InsultResponse();
                insultResponse.setInsult("You magnificent slow-witted walnut.");
        
                when(insultService.getInsult(any())).thenReturn(Mono.just(insultResponse));

                webTestClient.post()
                        .uri("/api/iaas/insult")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("""
                                {"characteristics": ["slow", "arrogant"]}
                                """)
                        .exchange()
                        .expectStatus().isBadRequest();
        }

        @Test
        void retursHTTP400WhenCharacteristicsIsNull(){
        InsultResponse insultResponse = new InsultResponse();
        insultResponse.setInsult("You magnificent slow-witted walnut.");
        
        when(insultService.getInsult(any())).thenReturn(Mono.just(insultResponse));

                webTestClient.post()
                        .uri("/api/iaas/insult")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("""
                                {"name": "John"}
                                """)
                        .exchange()
                        .expectStatus().isBadRequest();
        }

        @Test
        void retursHTTP502WhenPythonServiceIsDown(){
                InsultResponse insultResponse = new InsultResponse();
                insultResponse.setInsult("You magnificent slow-witted walnut.");
        
                when(insultService.getInsult(any())).thenReturn(Mono.error(new DownstreamException("Python service error: 500")));

                webTestClient.post()
                        .uri("/api/iaas/insult")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("""
                                {"name": "John", "characteristics": ["slow", "arrogant"]}
                                """)
                        .exchange()
                        .expectStatus().isEqualTo(502)
                        .expectBody()
                        .jsonPath("$.status").isEqualTo(502)
                        .jsonPath("$.message").isEqualTo("Downstream service error: Python service error: 500");
        }
}
