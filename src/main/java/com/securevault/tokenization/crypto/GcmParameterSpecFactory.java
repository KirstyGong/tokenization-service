package com.securevault.tokenization.crypto;

import javax.crypto.spec.GCMParameterSpec;

public interface GcmParameterSpecFactory {

    GCMParameterSpec create(byte[] iv);
}