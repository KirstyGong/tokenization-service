package com.securevault.tokenization.crypto;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HexFormat;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.securevault.tokenization.exception.EncryptionException;
import org.springframework.security.crypto.encrypt.BytesEncryptor;

public class DeterministicBytesEncryptor implements BytesEncryptor {

    private static final int IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private final SecretKey secretKey;

    public DeterministicBytesEncryptor(String hexKey) {
        byte[] keyBytes = HexFormat.of().parseHex(hexKey);
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    @Override
    public byte[] encrypt(byte[] plaintext) {
        try {
            // Derive a deterministic IV from the plaintext so same input = same output
            byte[] iv = deriveIv(plaintext);
            // Create AES-GCM cipher with 128-bit authentication tag
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            // Encrypt — output includes ciphertext + 16-byte GCM auth tag
            byte[] ciphertext = cipher.doFinal(plaintext);
            // Prepend IV to ciphertext: [12-byte IV | ciphertext | 16-byte auth tag]
            byte[] result = new byte[IV_LENGTH + ciphertext.length];
            System.arraycopy(iv, 0, result, 0, IV_LENGTH);
            System.arraycopy(ciphertext, 0, result, IV_LENGTH, ciphertext.length);
            return result;
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Encryption failed", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] encryptedBytes) {
        try {
            // Extract the 12-byte IV from the beginning
            byte[] iv = Arrays.copyOfRange(encryptedBytes, 0, IV_LENGTH);
            // Extract the ciphertext + auth tag (everything after the IV)
            byte[] ciphertext = Arrays.copyOfRange(encryptedBytes, IV_LENGTH, encryptedBytes.length);
            // Create AES-GCM cipher for decryption — also verifies the auth tag
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            // Decrypt and verify integrity — throws if ciphertext was tampered with
            return cipher.doFinal(ciphertext);
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Decryption failed", e);
        }
    }

    // Derives a deterministic 12-byte IV from the plaintext using SHA-256.
    // Same plaintext always produces the same IV, making encryption deterministic.
    private byte[] deriveIv(byte[] plaintext) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // SHA-256 produces 32 bytes — truncate to 12 bytes for GCM IV
            return Arrays.copyOf(digest.digest(plaintext), IV_LENGTH);
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("IV derivation failed", e);
        }
    }
}
