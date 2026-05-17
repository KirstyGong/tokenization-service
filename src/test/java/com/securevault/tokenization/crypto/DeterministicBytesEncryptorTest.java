package com.securevault.tokenization.crypto;

import static com.securevault.tokenization.testdata.PrimitiveDataProvider.getRandomHexKey;
import static com.securevault.tokenization.testdata.PrimitiveDataProvider.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
class DeterministicBytesEncryptorTest {

    @Mock
    private IvDerivator ivDerivator;

    @Mock
    private CipherExecutor cipherExecutor;

    private DeterministicBytesEncryptor encryptor;
    private byte[] plaintext;
    private byte[] fakeCiphertext;
    private byte[] iv;

    @BeforeEach
    void setUp() {
        iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        fakeCiphertext = new byte[16];
        new SecureRandom().nextBytes(fakeCiphertext);

        when(ivDerivator.deriveIv(any())).thenReturn(iv);
        when(cipherExecutor.execute(eq(Cipher.ENCRYPT_MODE), any(), any(), any())).thenReturn(fakeCiphertext);
        when(cipherExecutor.execute(eq(Cipher.DECRYPT_MODE), any(), any(), any())).thenReturn(new byte[]{1, 2, 3});

        encryptor = new DeterministicBytesEncryptor(getRandomHexKey(), ivDerivator, cipherExecutor);
        plaintext = getRandomString().getBytes(StandardCharsets.UTF_8);
    }

    @Nested
    class Encrypt {

        private byte[] result;

        @BeforeEach
        void setUp() {
            result = encryptor.encrypt(plaintext);
        }

        @Test
        void shouldDelegateIvDerivationToIvDerivator() {
            verify(ivDerivator).deriveIv(plaintext);
        }

        @Test
        void shouldDelegateToCipherExecutor() {
            verify(cipherExecutor).execute(eq(Cipher.ENCRYPT_MODE), any(), eq(iv), eq(plaintext));
        }

        @Test
        void shouldPrependIvToCiphertext() {
            byte[] ivFromResult = Arrays.copyOfRange(result, 0, 12);
            byte[] ciphertextFromResult = Arrays.copyOfRange(result, 12, result.length);

            assertThat(ivFromResult).isEqualTo(iv);
            assertThat(ciphertextFromResult).isEqualTo(fakeCiphertext);
        }
    }

    @Nested
    class Decrypt {

        @Test
        void shouldExtractIvAndDelegateToCipherExecutor() {
            byte[] input = new byte[28];
            new SecureRandom().nextBytes(input);

            encryptor.decrypt(input);

            byte[] expectedIv = Arrays.copyOfRange(input, 0, 12);
            byte[] expectedCiphertext = Arrays.copyOfRange(input, 12, input.length);
            verify(cipherExecutor).execute(eq(Cipher.DECRYPT_MODE), any(), eq(expectedIv), eq(expectedCiphertext));
        }
    }

    @Nested
    class WithInvalidHex {

        @Test
        void shouldThrowExceptionForInvalidHex() {
            assertThatThrownBy(() -> new DeterministicBytesEncryptor("not-valid-hex", ivDerivator, cipherExecutor))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}