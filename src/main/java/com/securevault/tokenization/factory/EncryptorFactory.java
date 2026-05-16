package com.securevault.tokenization.factory;

import org.springframework.security.crypto.encrypt.BytesEncryptor;

public interface EncryptorFactory {

    BytesEncryptor create(int keyVersion);
}
