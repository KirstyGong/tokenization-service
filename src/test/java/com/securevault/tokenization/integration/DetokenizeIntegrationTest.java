package com.securevault.tokenization.integration;

import static com.securevault.tokenization.testdata.PrimitiveDataProvider.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securevault.tokenization.dto.DetokenizeRequest;
import com.securevault.tokenization.dto.DetokenizeResponse;
import com.securevault.tokenization.service.TokenService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class DetokenizeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenService tokenService;

    @Nested
    class WhenTokenExists {

        @Test
        void shouldReturnOriginalValue() throws Exception {
            String originalValue = getRandomString();

            DetokenizeResponse detokenizeResponse = getDetokenizeResponse(List.of(originalValue));

            assertThat(detokenizeResponse.values()).containsExactly(originalValue);
        }

        @Test
        void shouldReturnMultipleValues() throws Exception {
            String firstValue = getRandomString();
            String secondValue = getRandomString();

            DetokenizeResponse detokenizeResponse = getDetokenizeResponse(List.of(firstValue, secondValue));

            assertThat(detokenizeResponse.values()).containsExactly(firstValue, secondValue);
        }

        private DetokenizeResponse getDetokenizeResponse(List<String> originalValues) throws Exception {
            List<String> tokenizedValue = tokenize(originalValues);
            return detokenize(tokenizedValue);
        }
    }

    @Nested
    class WhenTokenDoesNotExist {

        @Test
        void shouldReturnNotFound() throws Exception {
            DetokenizeRequest request = createDetokenizeRequest(List.of(getRandomString()));

            mockMvc.perform(post("/detokenize")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class WhenTokensIsEmpty {

        @Test
        void shouldReturnBadRequest() throws Exception {
            DetokenizeRequest request = createDetokenizeRequest(List.of());

            mockMvc.perform(post("/detokenize")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class WhenTokensIsNull {

        @Test
        void shouldReturnBadRequest() throws Exception {
            DetokenizeRequest request = new DetokenizeRequest();

            mockMvc.perform(post("/detokenize")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    private List<String> tokenize(List<String> values) throws Exception {
        return tokenService.tokenize(values);
    }

    private DetokenizeResponse detokenize(List<String> tokens) throws Exception {
        DetokenizeRequest request = createDetokenizeRequest(tokens);

        MvcResult result = mockMvc.perform(post("/detokenize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), DetokenizeResponse.class);
    }

    private DetokenizeRequest createDetokenizeRequest(List<String> tokens) {
        DetokenizeRequest request = new DetokenizeRequest();
        request.setTokens(tokens);
        return request;
    }
}
