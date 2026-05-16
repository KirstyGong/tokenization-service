package com.securevault.tokenization.factory;

import com.securevault.tokenization.model.TokenRecord;

public interface TokenRecordFactory {

    TokenRecord create(String token, String encryptedValue, int keyVersion);
}
