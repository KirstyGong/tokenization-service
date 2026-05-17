package com.securevault.tokenization.crypto;

import java.util.Arrays;
import java.util.HexFormat;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.encrypt.BytesEncryptor;

public class DeterministicBytesEncryptor implements BytesEncryptor {

    private static final int IV_LENGTH = 12;

    private final SecretKey secretKey;
    private final IvDerivator ivDerivator;
    private final CipherExecutor cipherExecutor;

    public DeterministicBytesEncryptor(String hexKey, IvDerivator ivDerivator, CipherExecutor cipherExecutor) {
        byte[] keyBytes = HexFormat.of().parseHex(hexKey);
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
        this.ivDerivator = ivDerivator;
        this.cipherExecutor = cipherExecutor;
    }

    // Derives a deterministic IV from plaintext, encrypts, and returns [12-byte IV | ciphertext | auth tag].
    @Override
    public byte[] encrypt(byte[] plaintext) {
        byte[] iv = ivDerivator.deriveIv(plaintext);
        byte[] ciphertext = cipherExecutor.execute(Cipher.ENCRYPT_MODE, secretKey, iv, plaintext);
        byte[] result = new byte[IV_LENGTH + ciphertext.length];
        System.arraycopy(iv, 0, result, 0, IV_LENGTH);
        System.arraycopy(ciphertext, 0, result, IV_LENGTH, ciphertext.length);
        return result;
    }

    // Extracts the prepended IV, then decrypts and verifies the auth tag in one step.
    @Override
    public byte[] decrypt(byte[] encryptedBytes) {
        byte[] iv = Arrays.copyOfRange(encryptedBytes, 0, IV_LENGTH);
        byte[] ciphertext = Arrays.copyOfRange(encryptedBytes, IV_LENGTH, encryptedBytes.length);
        return cipherExecutor.execute(Cipher.DECRYPT_MODE, secretKey, iv, ciphertext);
    }

}
