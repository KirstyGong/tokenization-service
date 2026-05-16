package com.securevault.tokenization.integration;

import static com.securevault.tokenization.testdata.PrimitiveDataProvider.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securevault.tokenization.dto.TokenizeRequest;
import com.securevault.tokenization.dto.TokenizeResponse;
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
class TokenizeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class WhenRequestIsValid {

        @Test
        void shouldReturnOkWithTokens() throws Exception {
            TokenizeRequest request = createRequest(List.of(getRandomString()));

            MvcResult result = mockMvc.perform(post("/tokenize")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isOk())
                    .andReturn();

            TokenizeResponse response = parseResponse(result);
            assertThat(response.tokens()).hasSize(1);
            assertThat(response.tokens().get(0)).isNotEmpty();
        }

        @Test
        void shouldReturnSameTokenForSameValue() throws Exception {
            TokenizeRequest request = createRequest(List.of(getRandomString()));
            String requestBody = toJson(request);

            MvcResult firstResult = mockMvc.perform(post("/tokenize")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andReturn();

            MvcResult secondResult = mockMvc.perform(post("/tokenize")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andReturn();

            TokenizeResponse firstResponse = parseResponse(firstResult);
            TokenizeResponse secondResponse = parseResponse(secondResult);
            assertThat(firstResponse.tokens()).isEqualTo(secondResponse.tokens());
        }
    }

    @Nested
    class WhenValuesIsEmpty {

        @Test
        void shouldReturnBadRequest() throws Exception {
            TokenizeRequest request = createRequest(List.of());

            mockMvc.perform(post("/tokenize")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class WhenValuesIsNull {

        @Test
        void shouldReturnBadRequest() throws Exception {
            TokenizeRequest request = new TokenizeRequest();

            mockMvc.perform(post("/tokenize")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    private TokenizeRequest createRequest(List<String> values) {
        TokenizeRequest request = new TokenizeRequest();
        request.setValues(values);
        return request;
    }

    private String toJson(TokenizeRequest request) throws Exception {
        return objectMapper.writeValueAsString(request);
    }

    private TokenizeResponse parseResponse(MvcResult result) throws Exception {
        return objectMapper.readValue(result.getResponse().getContentAsString(), TokenizeResponse.class);
    }
}
