package com.securevault.tokenization.service;

import static com.securevault.tokenization.testdata.PrimitiveDataProvider.getRandomInteger;
import static com.securevault.tokenization.testdata.PrimitiveDataProvider.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.securevault.tokenization.dto.EncryptedData;
import com.securevault.tokenization.exception.TokenNotFoundException;
import com.securevault.tokenization.factory.TokenRecordFactory;
import com.securevault.tokenization.model.TokenRecord;
import com.securevault.tokenization.repository.TokenRepository;
import com.securevault.tokenization.utils.TokenGenerator;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@MockitoSettings(strictness = Strictness.LENIENT)
class DefaultTokenServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private TokenGenerator tokenGenerator;

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private TokenRecordFactory tokenRecordFactory;

    @Mock
    private EncryptedData encryptedData;

    @Mock
    private TokenRecord existingRecord;

    @Mock
    private TokenRecord newRecord;

    @Mock
    private EncryptedData otherEncryptedData;

    @Mock
    private TokenRecord otherNewRecord;

    private DefaultTokenService tokenService;

    private String value;
    private String cipherText;
    private int keyVersion;
    private String token;
    private String otherToken;

    @BeforeEach
    void setUp() {
        value = getRandomString();
        cipherText = getRandomString();
        keyVersion = getRandomInteger(1, 10);
        token = getRandomString();
        otherToken = getRandomString();

        tokenService = new DefaultTokenService(
                tokenRepository, tokenGenerator, encryptionService, tokenRecordFactory);
    }

    @Nested
    class Tokenize {

        @Nested
        class WhenInputIsEmpty {

            @Test
            void shouldReturnEmptyList() {
                assertThat(tokenService.tokenize(List.of())).isEmpty();
            }
        }

        @Nested
        class WhenTokenDoesNotExist {

            private List<String> result;

            @BeforeEach
            void setUp() {
                when(encryptionService.encrypt(value)).thenReturn(encryptedData);
                when(encryptedData.cipherText()).thenReturn(cipherText);
                when(encryptedData.keyVersion()).thenReturn(keyVersion);
                when(tokenRepository.findByEncryptedValueAndKeyVersion(cipherText, keyVersion))
                        .thenReturn(Optional.empty());
                when(tokenGenerator.generate()).thenReturn(token);
                when(tokenRecordFactory.create(token, cipherText, keyVersion)).thenReturn(newRecord);

                result = tokenService.tokenize(List.of(value));
            }

            @Test
            void shouldEncryptValue() {
                verify(encryptionService).encrypt(value);
            }

            @Test
            void shouldGenerateToken() {
                verify(tokenGenerator).generate();
            }

            @Test
            void shouldSaveTokenRecord() {
                verify(tokenRepository).save(newRecord);
            }

            @Test
            void shouldReturnGeneratedToken() {
                assertThat(result).containsExactly(token);
            }

        }

        @Nested
        class WhenTokenAlreadyExists {

            private List<String> result;

            @BeforeEach
            void setUp() {
                when(encryptionService.encrypt(value)).thenReturn(encryptedData);
                when(encryptedData.cipherText()).thenReturn(cipherText);
                when(encryptedData.keyVersion()).thenReturn(keyVersion);
                when(existingRecord.getToken()).thenReturn(token);
                when(tokenRepository.findByEncryptedValueAndKeyVersion(cipherText, keyVersion))
                        .thenReturn(Optional.of(existingRecord));

                result = tokenService.tokenize(List.of(value));
            }

            @Test
            void shouldNotGenerateNewToken() {
                verify(tokenGenerator, never()).generate();
            }

            @Test
            void shouldNotSaveNewRecord() {
                verify(tokenRepository, never()).save(newRecord);
            }

            @Test
            void shouldReturnExistingToken() {
                assertThat(result).containsExactly(token);
            }
        }

        @Nested
        class WithMultipleValues {

            private List<String> result;

            @BeforeEach
            void setUp() {
                String value2 = getRandomString();
                String cipherText2 = getRandomString();

                when(encryptionService.encrypt(value)).thenReturn(encryptedData);
                when(encryptedData.cipherText()).thenReturn(cipherText);
                when(encryptedData.keyVersion()).thenReturn(keyVersion);

                when(encryptionService.encrypt(value2)).thenReturn(otherEncryptedData);
                when(otherEncryptedData.cipherText()).thenReturn(cipherText2);
                when(otherEncryptedData.keyVersion()).thenReturn(keyVersion);

                when(tokenRepository.findByEncryptedValueAndKeyVersion(cipherText, keyVersion))
                        .thenReturn(Optional.empty());
                when(tokenRepository.findByEncryptedValueAndKeyVersion(cipherText2, keyVersion))
                        .thenReturn(Optional.empty());

                when(tokenGenerator.generate()).thenReturn(token, otherToken);
                when(tokenRecordFactory.create(token, cipherText, keyVersion)).thenReturn(newRecord);
                when(tokenRecordFactory.create(otherToken, cipherText2, keyVersion)).thenReturn(otherNewRecord);

                result = tokenService.tokenize(List.of(value, value2));
            }

            @Test
            void shouldReturnTokensInOrder() {
                assertThat(result).containsExactly(token, otherToken);
            }
        }
    }

    @Nested
    class Detokenize {

        @Nested
        class WhenTokenExists {

            private List<String> result;

            @BeforeEach
            void setUp() {
                when(tokenRepository.findByToken(token)).thenReturn(Optional.of(existingRecord));
                when(existingRecord.getEncryptedValue()).thenReturn(cipherText);
                when(existingRecord.getKeyVersion()).thenReturn(keyVersion);
                when(encryptionService.decrypt(cipherText, keyVersion)).thenReturn(value);

                result = tokenService.detokenize(List.of(token));
            }

            @Test
            void shouldLookUpToken() {
                verify(tokenRepository).findByToken(token);
            }

            @Test
            void shouldDecryptValue() {
                verify(encryptionService).decrypt(cipherText, keyVersion);
            }

            @Test
            void shouldReturnDecryptedValue() {
                assertThat(result).containsExactly(value);
            }
        }

        @Nested
        class WhenTokenDoesNotExist {

            @BeforeEach
            void setUp() {
                when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());
            }

            @Test
            void shouldThrowTokenNotFoundException() {
                assertThatThrownBy(() -> tokenService.detokenize(List.of(token)))
                        .isInstanceOf(TokenNotFoundException.class);
            }
        }

        @Nested
        class WhenInputIsEmpty {

            @Test
            void shouldReturnEmptyList() {
                assertThat(tokenService.detokenize(List.of())).isEmpty();
            }
        }
    }
}
