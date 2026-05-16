package com.securevault.tokenization.service;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class DefaultTokenGeneratorTest {

    @Mock
    private UuidProvider uuidProvider;

    private UUID uuid;
    private String result;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        when(uuidProvider.randomUuid()).thenReturn(uuid);

        result = new DefaultTokenGenerator(uuidProvider).generate();
    }

    @Test
    void shouldUseUuidProvider() {
        verify(uuidProvider).randomUuid();
    }

    @Test
    void shouldReturnUuidAsString() {
        assertThat(result).isEqualTo(uuid.toString());
    }
}
