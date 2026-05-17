package com.securevault.tokenization.crypto;

import javax.crypto.SecretKey;

public interface CipherExecutor {

    byte[] execute(int mode, SecretKey key, byte[] iv, byte[] input);
}