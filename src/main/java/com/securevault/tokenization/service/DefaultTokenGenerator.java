package com.securevault.tokenization.service;

import org.springframework.stereotype.Component;

@Component
public class DefaultTokenGenerator implements TokenGenerator {

    private final UuidProvider uuidProvider;

    public DefaultTokenGenerator(UuidProvider uuidProvider) {
        this.uuidProvider = uuidProvider;
    }

    @Override
    public String generate() {
        return uuidProvider.randomUuid().toString();
    }
}
