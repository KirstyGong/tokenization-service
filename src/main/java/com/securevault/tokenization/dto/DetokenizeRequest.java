package com.securevault.tokenization.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DetokenizeRequest {

    @NotEmpty
    private List<String> tokens;

}
