package com.iaas.api.client;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iaas.api.exception.DownstreamException;
import com.iaas.api.model.InsultRequest;
import com.iaas.api.model.InsultResponse;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class PythonServiceClientTest {

    private MockWebServer mockWebServer;

    private PythonServiceClient client;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        client = new PythonServiceClient(webClient);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void returnsInsultOnSuccess() throws Exception {
        InsultResponse expected = new InsultResponse();
        expected.setInsult("You magnificent slow-witted walnut.");

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(objectMapper.writeValueAsString(expected)));

        StepVerifier.create(client.generateInsult(request()))
                .assertNext(res -> assertThat(res.getInsult())
                        .isEqualTo("You magnificent slow-witted walnut."))
                .verifyComplete();
    }

    @Test
    void throwsDownstreamExceptionOn5xxError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error"));

        StepVerifier.create(client.generateInsult(request()))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(DownstreamException.class);
                    assertThat(ex.getMessage()).contains("Python service error");
                })
                .verify();
    }

    @Test
    void throwsDownstreamExceptionOn4xxError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("Bad Request"));

        StepVerifier.create(client.generateInsult(request()))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(DownstreamException.class);
                    assertThat(ex.getMessage()).contains("Python service error");
                })
                .verify();
    }

    @Test
    void sendsRequestToCorrectEndpoint() throws Exception {
        InsultResponse response = new InsultResponse();
        response.setInsult("test insult");

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(objectMapper.writeValueAsString(response)));

        client.generateInsult(request()).block();

        assertThat(mockWebServer.takeRequest().getPath()).isEqualTo("/insult");
    }

    private InsultRequest request() {
        InsultRequest req = new InsultRequest();
        req.setName("John");
        req.setCharacteristics(List.of("slow", "arrogant"));
        return req;
    }

}
