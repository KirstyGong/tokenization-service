package com.securevault.tokenization.dto;

public record EncryptedData(String cipherText, int keyVersion) {
}
