package com.smartcafe.config;

import com.xendit.Xendit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Xendit Configuration class
 */
@Configuration
public class XenditConfig {

    @Value("${xendit.apiKey}")
    private String apiKey;

    /**
     * Initialize Xendit SDK
     */
    @PostConstruct
    public void init() {
        Xendit.apiKey = apiKey;
    }
}
