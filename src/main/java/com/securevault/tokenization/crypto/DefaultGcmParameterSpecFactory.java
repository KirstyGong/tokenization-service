package com.securevault.tokenization.crypto;

import javax.crypto.spec.GCMParameterSpec;

import org.springframework.stereotype.Component;

@Component
public class DefaultGcmParameterSpecFactory implements GcmParameterSpecFactory {

    private static final int GCM_TAG_LENGTH = 128;

    @Override
    public GCMParameterSpec create(byte[] iv) {
        return new GCMParameterSpec(GCM_TAG_LENGTH, iv);
    }
}