package com.securevault.tokenization.controller;

import java.util.List;

import com.securevault.tokenization.dto.DetokenizeRequest;
import com.securevault.tokenization.dto.DetokenizeResponse;
import com.securevault.tokenization.dto.TokenizeRequest;
import com.securevault.tokenization.dto.TokenizeResponse;
import com.securevault.tokenization.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/tokenize")
    public ResponseEntity<TokenizeResponse> tokenize(@Valid @RequestBody TokenizeRequest request) {
        List<String> tokens = tokenService.tokenize(request.getValues());
        return ResponseEntity.ok(new TokenizeResponse(tokens));
    }

    @PostMapping("/detokenize")
    public ResponseEntity<DetokenizeResponse> detokenize(@Valid @RequestBody DetokenizeRequest request) {
        List<String> values = tokenService.detokenize(request.getTokens());
        return ResponseEntity.ok(new DetokenizeResponse(values));
    }
}
