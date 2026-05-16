package com.securevault.tokenization.service;

import java.util.List;

import com.securevault.tokenization.dto.EncryptedData;
import com.securevault.tokenization.exception.TokenNotFoundException;
import com.securevault.tokenization.factory.TokenRecordFactory;
import com.securevault.tokenization.model.TokenRecord;
import com.securevault.tokenization.repository.TokenRepository;
import com.securevault.tokenization.utils.TokenGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultTokenService implements TokenService {

    private final TokenRepository tokenRepository;
    private final TokenGenerator tokenGenerator;
    private final EncryptionService encryptionService;
    private final TokenRecordFactory tokenRecordFactory;

    public DefaultTokenService(TokenRepository tokenRepository,
                               TokenGenerator tokenGenerator,
                               EncryptionService encryptionService,
                               TokenRecordFactory tokenRecordFactory) {
        this.tokenRepository = tokenRepository;
        this.tokenGenerator = tokenGenerator;
        this.encryptionService = encryptionService;
        this.tokenRecordFactory = tokenRecordFactory;
    }

    @Override
    @Transactional
    public List<String> tokenize(List<String> values) {
        return values.stream()
                .map(this::tokenizeValue)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> detokenize(List<String> tokens) {
        return tokens.stream()
                .map(this::detokenizeValue)
                .toList();
    }

    private String detokenizeValue(String token) {
        TokenRecord record = tokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException(token));
        return encryptionService.decrypt(record.getEncryptedValue(), record.getKeyVersion());
    }

    private String tokenizeValue(String value) {
        EncryptedData encrypted = encryptionService.encrypt(value);
        return tokenRepository
                .findByEncryptedValueAndKeyVersion(encrypted.cipherText(), encrypted.keyVersion())
                .map(TokenRecord::getToken)
                .orElseGet(() -> createToken(encrypted));
    }

    private String createToken(EncryptedData encrypted) {
        String token = tokenGenerator.generate();
        tokenRepository.save(tokenRecordFactory.create(token, encrypted.cipherText(), encrypted.keyVersion()));
        return token;
    }
}
