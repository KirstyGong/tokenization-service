package com.securevault.tokenization.crypto;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import com.securevault.tokenization.exception.EncryptionException;
import org.springframework.stereotype.Component;

@Component
public class AesGcmCipherExecutor implements CipherExecutor {

    private final CipherFactory cipherFactory;
    private final GcmParameterSpecFactory gcmParameterSpecFactory;

    public AesGcmCipherExecutor(CipherFactory cipherFactory, GcmParameterSpecFactory gcmParameterSpecFactory) {
        this.cipherFactory = cipherFactory;
        this.gcmParameterSpecFactory = gcmParameterSpecFactory;
    }

    // Creates a fresh Cipher per call — required because GCM tracks used IVs and rejects reuse on the same instance.
    @Override
    public byte[] execute(int mode, SecretKey key, byte[] iv, byte[] input) {
        try {
            Cipher cipher = cipherFactory.create();
            cipher.init(mode, key, gcmParameterSpecFactory.create(iv));
            return cipher.doFinal(input);
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Cipher operation failed", e);
        }
    }
}
