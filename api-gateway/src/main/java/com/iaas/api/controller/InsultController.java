package com.iaas.api.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iaas.api.model.InsultRequest;
import com.iaas.api.model.InsultResponse;
import com.iaas.api.service.InsultService;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/iaas")
@Validated
public class InsultController {

    private InsultService insultService;

    public InsultController(InsultService insultService){
        this.insultService=insultService;
    }
    @PostMapping("/insult")
    public Mono<InsultResponse> insult(@Valid @RequestBody InsultRequest request){
        return insultService.getInsult(request);
    }

}
