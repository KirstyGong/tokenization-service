package com.securevault.tokenization.service;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class DefaultUuidProvider implements UuidProvider {

    @Override
    public UUID randomUuid() {
        return UUID.randomUUID();
    }
}
