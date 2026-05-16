package com.securevault.tokenization.crypto;

import static com.securevault.tokenization.testdata.PrimitiveDataProvider.getRandomHexKey;
import static com.securevault.tokenization.testdata.PrimitiveDataProvider.getRandomHexString;
import static com.securevault.tokenization.testdata.PrimitiveDataProvider.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;

import com.securevault.tokenization.exception.EncryptionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DeterministicBytesEncryptorTest {

    private DeterministicBytesEncryptor encryptor;
    private byte[] plaintext;

    @BeforeEach
    void setUp() {
        encryptor = new DeterministicBytesEncryptor(getRandomHexKey());
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
        void shouldReturnNonEmptyResult() {
            assertThat(result).isNotEmpty();
        }

        @Test
        void shouldProduceSameCiphertextForSamePlaintext() {
            assertThat(encryptor.encrypt(plaintext)).isEqualTo(result);
        }

        @Test
        void shouldProduceDifferentCiphertextForDifferentPlaintext() {
            byte[] otherPlaintext = getRandomString().getBytes(StandardCharsets.UTF_8);

            assertThat(encryptor.encrypt(otherPlaintext)).isNotEqualTo(result);
        }
    }

    @Nested
    class WithInvalidKey {

        @Test
        void shouldThrowEncryptionExceptionForWrongKeyLength() {
            DeterministicBytesEncryptor invalidEncryptor =
                    new DeterministicBytesEncryptor(getRandomHexString());

            assertThatThrownBy(() -> invalidEncryptor.encrypt(plaintext))
                    .isInstanceOf(EncryptionException.class);
        }

        @Test
        void shouldThrowExceptionForInvalidHex() {
            assertThatThrownBy(() -> new DeterministicBytesEncryptor("not-valid-hex"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class Decrypt {

        @Nested
        class WithValidCiphertext {

            @Test
            void shouldRecoverOriginalPlaintext() {
                byte[] encrypted = encryptor.encrypt(plaintext);

                assertThat(encryptor.decrypt(encrypted)).isEqualTo(plaintext);
            }
        }

        @Nested
        class WithTamperedCiphertext {

            @Test
            void shouldThrowEncryptionException() {
                byte[] encrypted = encryptor.encrypt(plaintext);
                encrypted[encrypted.length - 1] ^= 0xFF;

                assertThatThrownBy(() -> encryptor.decrypt(encrypted))
                        .isInstanceOf(EncryptionException.class);
            }
        }

        @Nested
        class WithWrongKey {

            @Test
            void shouldThrowEncryptionException() {
                byte[] encrypted = encryptor.encrypt(plaintext);
                DeterministicBytesEncryptor wrongKeyEncryptor =
                        new DeterministicBytesEncryptor(getRandomHexKey());

                assertThatThrownBy(() -> wrongKeyEncryptor.decrypt(encrypted))
                        .isInstanceOf(EncryptionException.class);
            }
        }
    }
}
