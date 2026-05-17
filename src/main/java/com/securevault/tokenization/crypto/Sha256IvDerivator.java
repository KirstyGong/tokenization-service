package com.securevault.tokenization.crypto;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;

import com.securevault.tokenization.exception.EncryptionException;
import org.springframework.stereotype.Component;

@Component
public class Sha256IvDerivator implements IvDerivator {

    private static final int IV_LENGTH = 12;

    // SHA-256 produces 32 bytes — truncated to 12 for GCM IV, ensuring same input always yields same IV.
    @Override
    public byte[] deriveIv(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Arrays.copyOf(digest.digest(input), IV_LENGTH);
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("IV derivation failed", e);
        }
    }
}