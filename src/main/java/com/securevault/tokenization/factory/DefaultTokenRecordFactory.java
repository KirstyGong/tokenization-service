package com.securevault.tokenization.factory;

import com.securevault.tokenization.model.TokenRecord;
import org.springframework.stereotype.Component;

@Component
public class DefaultTokenRecordFactory implements TokenRecordFactory {

    @Override
    public TokenRecord create(String token, String encryptedValue, int keyVersion) {
        return new TokenRecord(token, encryptedValue, keyVersion);
    }
}
