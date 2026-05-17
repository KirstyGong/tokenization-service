package com.securevault.tokenization.crypto;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;

import com.securevault.tokenization.exception.EncryptionException;
import org.springframework.stereotype.Component;

@Component
public class AesGcmCipherFactory implements CipherFactory {

    @Override
    public Cipher create() {
        try {
            return Cipher.getInstance("AES/GCM/NoPadding");
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Failed to create cipher", e);
        }
    }
}