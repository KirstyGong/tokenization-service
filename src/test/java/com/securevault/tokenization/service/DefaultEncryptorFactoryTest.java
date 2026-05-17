package com.securevault.tokenization.service;

import java.util.Map;

import com.securevault.tokenization.config.EncryptionProperties;
import com.securevault.tokenization.crypto.CipherExecutor;
import com.securevault.tokenization.crypto.IvDerivator;
import com.securevault.tokenization.exception.EncryptionException;
import com.securevault.tokenization.factory.DefaultEncryptorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.encrypt.BytesEncryptor;

import static com.securevault.tokenization.testdata.PrimitiveDataProvider.getRandomHexKey;
import static com.securevault.tokenization.testdata.PrimitiveDataProvider.getRandomInteger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class DefaultEncryptorFactoryTest {

    @Mock
    private EncryptionProperties encryptionProperties;

    @Mock
    private IvDerivator ivDerivator;

    @Mock
    private CipherExecutor cipherExecutor;

    private DefaultEncryptorFactory factory;
    private int keyVersion;

    @BeforeEach
    void setUp() {
        keyVersion = getRandomInteger(1, 100);

        when(encryptionProperties.getKeys()).thenReturn(
                Map.of(keyVersion, getRandomHexKey()));

        factory = new DefaultEncryptorFactory(encryptionProperties, ivDerivator, cipherExecutor);
    }

    @Nested
    class WithValidKeyVersion {

        private BytesEncryptor result;

        @BeforeEach
        void setUp() {
            result = factory.create(keyVersion);
        }

        @Test
        void shouldReturnNonNullEncryptor() {
            assertThat(result).isNotNull();
        }
    }

    @Nested
    class WithUnknownKeyVersion {

        private int unknownKeyVersion;

        @BeforeEach
        void setUp() {
            unknownKeyVersion = getRandomInteger(100, 200);
        }

        @Test
        void shouldThrowEncryptionException() {
            assertThatThrownBy(() -> factory.create(unknownKeyVersion))
                    .isInstanceOf(EncryptionException.class);
        }
    }
}
