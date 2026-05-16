package com.securevault.tokenization.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "token_records")
public class TokenRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String token;

    @Column(nullable = false)
    private String encryptedValue;

    @Column(nullable = false)
    private int keyVersion;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        modifiedAt = LocalDateTime.now();
    }
}
