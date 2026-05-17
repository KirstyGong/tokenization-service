package com.securevault.tokenization.crypto;

import static com.securevault.tokenization.testdata.PrimitiveDataProvider.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
class Sha256IvDerivatorTest {

    private static final int IV_LENGTH = 12;

    private IvDerivator derivator;
    private byte[] input;
    private byte[] result;

    @BeforeEach
    void setUp() {
        derivator = new Sha256IvDerivator();
        input = getRandomString().getBytes(StandardCharsets.UTF_8);
        result = derivator.deriveIv(input);
    }

    @Test
    void shouldReturnTwelveBytes() {
        assertThat(result).hasSize(IV_LENGTH);
    }

    @Test
    void shouldReturnSameIvForSameInput() {
        assertThat(derivator.deriveIv(input)).isEqualTo(result);
    }

    @Test
    void shouldReturnDifferentIvForDifferentInput() {
        byte[] otherInput = getRandomString().getBytes(StandardCharsets.UTF_8);

        assertThat(derivator.deriveIv(otherInput)).isNotEqualTo(result);
    }
}