package com.securevault.tokenization.repository;

import java.util.Optional;

import com.securevault.tokenization.model.TokenRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<TokenRecord, Long> {

    Optional<TokenRecord> findByToken(String token);

    Optional<TokenRecord> findByEncryptedValueAndKeyVersion(String encryptedValue, int keyVersion);
}
