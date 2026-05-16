package com.securevault.tokenization.dto;

import static com.securevault.tokenization.testdata.PrimitiveDataProvider.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TokenizeRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private Set<ConstraintViolation<TokenizeRequest>> violations;

    @Nested
    class WhenValuesIsNull {

        @BeforeEach
        void setUp() {
            TokenizeRequest request = new TokenizeRequest();
            violations = validator.validate(request);
        }

        @Test
        void shouldHaveViolation() {
            assertThat(violations).isNotEmpty();
        }
    }

    @Nested
    class WhenValuesIsEmpty {

        @BeforeEach
        void setUp() {
            TokenizeRequest request = new TokenizeRequest();
            request.setValues(List.of());
            violations = validator.validate(request);
        }

        @Test
        void shouldHaveViolation() {
            assertThat(violations).isNotEmpty();
        }
    }

    @Nested
    class WhenValuesIsValid {

        @BeforeEach
        void setUp() {
            TokenizeRequest request = new TokenizeRequest();
            request.setValues(List.of(getRandomString()));
            violations = validator.validate(request);
        }

        @Test
        void shouldHaveNoViolations() {
            assertThat(violations).isEmpty();
        }
    }
}
