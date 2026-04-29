package com.iaas.api.service;

import org.springframework.stereotype.Service;

import com.iaas.api.client.PythonServiceClient;

@Service
public class InsultService {

    private final InsultCacheService cache;
    private final PythonServiceClient pythonClient;
}
