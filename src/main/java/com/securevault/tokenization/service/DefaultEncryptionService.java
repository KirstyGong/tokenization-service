package com.securevault.tokenization.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.securevault.tokenization.config.EncryptionProperties;
import com.securevault.tokenization.dto.EncryptedData;
import com.securevault.tokenization.factory.EncryptorFactory;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Component;

@Component
public class DefaultEncryptionService implements EncryptionService {

    private final EncryptionProperties properties;
    private final EncryptorFactory encryptorFactory;

    public DefaultEncryptionService(EncryptionProperties properties, EncryptorFactory encryptorFactory) {
        this.properties = properties;
        this.encryptorFactory = encryptorFactory;
    }

    @Override
    public EncryptedData encrypt(String plainText) {
        int activeVersion = properties.getActiveKeyVersion();
        BytesEncryptor encryptor = encryptorFactory.create(activeVersion);
        byte[] encryptedText = encryptor.encrypt(plainText.getBytes(StandardCharsets.UTF_8));
        return new EncryptedData(Base64.getEncoder().encodeToString(encryptedText), activeVersion);
    }

    @Override
    public String decrypt(String cipherText, int keyVersion) {
        BytesEncryptor encryptor = encryptorFactory.create(keyVersion);
        byte[] decryptedText = encryptor.decrypt(Base64.getDecoder().decode(cipherText));
        return new String(decryptedText, StandardCharsets.UTF_8);
    }
}
