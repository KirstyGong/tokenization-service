package com.securevault.tokenization.factory;

import com.securevault.tokenization.config.EncryptionProperties;
import com.securevault.tokenization.exception.EncryptionException;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Component;

@Component
public class DefaultEncryptorFactory implements EncryptorFactory {

    private final EncryptionProperties properties;

    public DefaultEncryptorFactory(EncryptionProperties properties) {
        this.properties = properties;
    }

    @Override
    public BytesEncryptor create(int keyVersion) {
        String encryptionKey = properties.getKeys().get(keyVersion);
        if (encryptionKey == null) {
            throw new EncryptionException("Unknown key version: " + keyVersion, null);
        }
        return new AesBytesEncryptor(encryptionKey, properties.getSalt(),
                KeyGenerators.secureRandom(16), AesBytesEncryptor.CipherAlgorithm.GCM);
    }
}
