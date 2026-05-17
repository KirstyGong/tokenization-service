package com.securevault.tokenization.factory;

import com.securevault.tokenization.config.EncryptionProperties;
import com.securevault.tokenization.crypto.CipherExecutor;
import com.securevault.tokenization.crypto.DeterministicBytesEncryptor;
import com.securevault.tokenization.crypto.IvDerivator;
import com.securevault.tokenization.exception.EncryptionException;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Component;

@Component
public class DefaultEncryptorFactory implements EncryptorFactory {

    private final EncryptionProperties properties;
    private final IvDerivator ivDerivator;
    private final CipherExecutor cipherExecutor;

    public DefaultEncryptorFactory(EncryptionProperties properties, IvDerivator ivDerivator, CipherExecutor cipherExecutor) {
        this.properties = properties;
        this.ivDerivator = ivDerivator;
        this.cipherExecutor = cipherExecutor;
    }

    @Override
    public BytesEncryptor create(int keyVersion) {
        String encryptionKey = properties.getKeys().get(keyVersion);
        if (encryptionKey == null) {
            throw new EncryptionException("Unknown key version: " + keyVersion, null);
        }
        return new DeterministicBytesEncryptor(encryptionKey, ivDerivator, cipherExecutor);
    }
}
