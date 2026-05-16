package com.securevault.tokenization.service;

import java.util.List;

public interface TokenService {

    List<String> tokenize(List<String> values);

    List<String> detokenize(List<String> tokens);
}
