package com.securevault.tokenization.service;

import com.securevault.tokenization.dto.EncryptedData;

public interface EncryptionService {

    EncryptedData encrypt(String plainText);

    String decrypt(String cipherText, int keyVersion);
}
