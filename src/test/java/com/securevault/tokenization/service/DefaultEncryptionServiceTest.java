package com.securevault.tokenization.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.securevault.tokenization.config.EncryptionProperties;
import com.securevault.tokenization.dto.EncryptedData;
import com.securevault.tokenization.exception.EncryptionException;
import com.securevault.tokenization.factory.EncryptorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.encrypt.BytesEncryptor;

import static com.securevault.tokenization.testdata.PrimitiveDataProvider.getRandomInteger;
import static com.securevault.tokenization.testdata.PrimitiveDataProvider.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class DefaultEncryptionServiceTest {

    @Mock
    private EncryptionProperties encryptionProperties;

    @Mock
    private EncryptorFactory encryptorFactory;

    @Mock
    private BytesEncryptor bytesEncryptor;

    private DefaultEncryptionService service;
    private String plainText;
    private byte[] encryptedBytes;
    private int keyVersion;

    @BeforeEach
    void setUp() {
        plainText = getRandomString();
        encryptedBytes = getRandomString().getBytes(StandardCharsets.UTF_8);
        keyVersion = getRandomInteger(1, 100);

        when(encryptionProperties.getActiveKeyVersion()).thenReturn(keyVersion);
        when(encryptorFactory.create(keyVersion)).thenReturn(bytesEncryptor);
        when(bytesEncryptor.encrypt(plainText.getBytes(StandardCharsets.UTF_8))).thenReturn(encryptedBytes);

        service = new DefaultEncryptionService(encryptionProperties, encryptorFactory);
    }

    @Nested
    class Encrypt {

        private EncryptedData encryptResult;

        @BeforeEach
        void setUp() {
            encryptResult = service.encrypt(plainText);
        }

        @Test
        void shouldUseActiveKeyVersion() {
            verify(encryptorFactory).create(keyVersion);
        }

        @Test
        void shouldReturnBase64EncodedCipherText() {
            assertThat(encryptResult.cipherText())
                    .isEqualTo(Base64.getEncoder().encodeToString(encryptedBytes));
        }

        @Test
        void shouldReturnActiveKeyVersion() {
            assertThat(encryptResult.keyVersion()).isEqualTo(keyVersion);
        }
    }

    @Nested
    class Decrypt {

        @Nested
        class WithValidKeyVersion {

            private String decryptResult;

            @BeforeEach
            void setUp() {
                when(bytesEncryptor.decrypt(encryptedBytes)).thenReturn(plainText.getBytes(StandardCharsets.UTF_8));
                String cipherText = Base64.getEncoder().encodeToString(encryptedBytes);
                decryptResult = service.decrypt(cipherText, keyVersion);
            }

            @Test
            void shouldUseEncryptorFactory() {
                verify(encryptorFactory).create(keyVersion);
            }

            @Test
            void shouldDecryptToOriginalPlainText() {
                assertThat(decryptResult).isEqualTo(plainText);
            }
        }

        @Nested
        class WithUnknownKeyVersion {

            private int unknownKeyVersion;

            @BeforeEach
            void setUp() {
                unknownKeyVersion = getRandomInteger(100, 200);
                when(encryptorFactory.create(unknownKeyVersion)).thenThrow(
                        new EncryptionException("Unknown key version: " + unknownKeyVersion, null));
            }

            @Test
            void shouldThrowEncryptionException() {
                assertThatThrownBy(() -> service.decrypt(getRandomString(), unknownKeyVersion))
                        .isInstanceOf(EncryptionException.class);
            }
        }
    }
}
