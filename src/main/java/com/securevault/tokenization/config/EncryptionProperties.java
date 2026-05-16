package com.securevault.tokenization.config;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "tokenization.encryption")
public class EncryptionProperties {

    private int activeKeyVersion;
    private Map<Integer, String> keys;
    private String salt;
}
