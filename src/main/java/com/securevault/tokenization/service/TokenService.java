package com.securevault.tokenization.service;

import java.util.List;

public interface TokenService {

    List<String> tokenize(List<String> values);
}
