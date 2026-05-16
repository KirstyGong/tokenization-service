package com.securevault.tokenization.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public class TokenizeRequest {

    @NotEmpty
    private List<String> values;

    public List<String> values() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
