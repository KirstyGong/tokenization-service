package com.securevault.tokenization.utils;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class DefaultUuidProvider implements UuidProvider {

    @Override
    public UUID randomUuid() {
        return UUID.randomUUID();
    }
}
