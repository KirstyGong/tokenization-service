package com.securevault.tokenization.crypto;

import static com.securevault.tokenization.testdata.PrimitiveDataProvider.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import com.securevault.tokenization.exception.EncryptionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
class AesGcmCipherExecutorTest {

    @Mock
    private CipherFactory cipherFactory;

    @Mock
    private Cipher cipher;

    @Mock
    private GcmParameterSpecFactory gcmParameterSpecFactory;

    @Mock
    private SecretKey secretKey;

    @Mock
    private GCMParameterSpec gcmParameterSpec;

    private CipherExecutor cipherExecutor;
    private byte[] iv;
    private byte[] plaintext;
    private byte[] fakeOutput;

    @BeforeEach
    void setUp() throws Exception {
        when(cipherFactory.create()).thenReturn(cipher);
        when(gcmParameterSpecFactory.create(any())).thenReturn(gcmParameterSpec);
        fakeOutput = new byte[16];
        new SecureRandom().nextBytes(fakeOutput);
        when(cipher.doFinal(any(byte[].class))).thenReturn(fakeOutput);

        cipherExecutor = new AesGcmCipherExecutor(cipherFactory, gcmParameterSpecFactory);
        iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        plaintext = getRandomString().getBytes(StandardCharsets.UTF_8);
    }

    @Nested
    class Execute {

        private byte[] result;

        @BeforeEach
        void setUp() {
            result = cipherExecutor.execute(Cipher.ENCRYPT_MODE, secretKey, iv, plaintext);
        }

        @Test
        void shouldDelegateToCipherFactory() {
            verify(cipherFactory).create();
        }

        @Test
        void shouldDelegateToGcmParameterSpecFactory() {
            verify(gcmParameterSpecFactory).create(iv);
        }

        @Test
        void shouldInitCipherWithCorrectParameters() throws Exception {
            verify(cipher).init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
        }

        @Test
        void shouldReturnCipherOutput() {
            assertThat(result).isEqualTo(fakeOutput);
        }
    }

    @Nested
    class WithCipherFailure {

        @Test
        void shouldThrowEncryptionException() throws Exception {
            when(cipher.doFinal(any(byte[].class)))
                    .thenThrow(new javax.crypto.BadPaddingException("tampered"));

            assertThatThrownBy(() -> cipherExecutor.execute(Cipher.DECRYPT_MODE, secretKey, iv, plaintext))
                    .isInstanceOf(EncryptionException.class);
        }
    }
}
