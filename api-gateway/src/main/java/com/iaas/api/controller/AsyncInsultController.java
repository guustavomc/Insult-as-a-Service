package com.iaas.api.controller;

import com.iaas.api.model.InsultRequest;
import com.iaas.api.service.AsyncInsultService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
@RestController
@RequestMapping("/api/iaas")
public class AsyncInsultController {

    private AsyncInsultService asyncInsultService;

    public AsyncInsultController(AsyncInsultService asyncInsultService){
        this.asyncInsultService = asyncInsultService;
    }

    @PostMapping("/insult/async")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Map<String, String>> submit(@Valid @RequestBody InsultRequest request) {
        return asyncInsultService.submit(request)
                .map(jobId -> Map.of("jobId", jobId));
    }

    @GetMapping("/insult/result/{jobId}")
    public Mono<Map<String, String>> getResult(@PathVariable String jobId) {
        return asyncInsultService.getResult(jobId)
                .map(insult -> Map.of("status", "ready", "insult", insult))
                .defaultIfEmpty(Map.of("status", "pending"));
    }

}
