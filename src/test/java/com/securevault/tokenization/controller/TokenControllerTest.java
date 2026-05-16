package com.securevault.tokenization.controller;

import static com.securevault.tokenization.testdata.PrimitiveDataProvider.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.securevault.tokenization.dto.TokenizeRequest;
import com.securevault.tokenization.dto.TokenizeResponse;
import com.securevault.tokenization.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
class TokenControllerTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private TokenizeRequest tokenizeRequest;

    private TokenController tokenController;

    private String value;
    private String otherValue;
    private String token;
    private String otherToken;

    @BeforeEach
    void setUp() {
        value = getRandomString();
        otherValue = getRandomString();
        token = getRandomString();
        otherToken = getRandomString();

        tokenController = new TokenController(tokenService);
    }

    @Nested
    class Tokenize {

        private ResponseEntity<TokenizeResponse> result;

        @BeforeEach
        void setUp() {
            when(tokenizeRequest.values()).thenReturn(List.of(value, otherValue));
            when(tokenService.tokenize(List.of(value, otherValue)))
                    .thenReturn(List.of(token, otherToken));

            result = tokenController.tokenize(tokenizeRequest);
        }

        @Test
        void shouldCallTokenServiceWithValues() {
            verify(tokenService).tokenize(List.of(value, otherValue));
        }

        @Test
        void shouldReturnOkStatus() {
            assertThat(result.getStatusCode().value()).isEqualTo(200);
        }

        @Test
        void shouldReturnTokensInResponse() {
            assertThat(result.getBody().tokens()).containsExactly(token, otherToken);
        }
    }

}
