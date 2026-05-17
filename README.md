# Tokenization Service

A Spring Boot service that replaces sensitive data with non-reversible tokens, storing the encrypted original securely. Supports idempotent tokenization and reverse lookup (detokenization).

## Tech Stack

- Java 17, Spring Boot 3.4.5
- AES-256-GCM deterministic encryption (same input always produces the same ciphertext)
- H2 in-memory database with Flyway migrations
- Spring Data JPA, Jakarta Bean Validation
- OpenAPI / Swagger UI

## Getting Started

### Prerequisites

- Java 17+

### Run

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

The service starts on **http://localhost:3000**.

### Run Tests

```bash
./gradlew test
```

## API

### Tokenize

```
POST http://localhost:3000/tokenize
Content-Type: application/json

{
    "values": ["sensitive-data-1", "sensitive-data-2"]
}
```

Response `200 OK`:
```json
{
    "tokens": ["uuid-token-1", "uuid-token-2"]
}
```

### Detokenize

```
POST http://localhost:3000/detokenize
Content-Type: application/json

{
    "tokens": ["uuid-token-1", "uuid-token-2"]
}
```

Response `200 OK`:
```json
{
    "values": ["sensitive-data-1", "sensitive-data-2"]
}
```

### Error Responses

| Status | Condition                    |
|--------|------------------------------|
| 400    | Values/tokens empty or null  |
| 404    | Token not found              |
| 500    | Encryption failure           |

## Other Endpoints

| URL                                      | Description         |
|------------------------------------------|---------------------|
| http://localhost:3000/swagger-ui.html     | Swagger UI          |
| http://localhost:3000/v3/api-docs         | OpenAPI spec (JSON) |
| http://localhost:3000/h2-console          | H2 database console |

### H2 Console Connection

- **JDBC URL**: `jdbc:h2:mem:tokendb`
- **Username**: `sa`
- **Password**: *(empty)*

## Architecture

```
controller/
  TokenController          - REST endpoints
  GlobalExceptionHandler   - Maps exceptions to HTTP responses

service/
  TokenService             - Tokenize / detokenize interface
  DefaultTokenService      - Orchestrates encryption + persistence
  EncryptionService        - Encrypt / decrypt interface
  DefaultEncryptionService - Delegates to BytesEncryptor per key version

crypto/
  DeterministicBytesEncryptor - Orchestrates IV derivation, encryption, and IV prepending
  IvDerivator / Sha256IvDerivator - Derives deterministic IV from plaintext via SHA-256
  CipherExecutor / AesGcmCipherExecutor - Executes AES-GCM cipher operations
  CipherFactory / AesGcmCipherFactory - Creates fresh Cipher instances per operation
  GcmParameterSpecFactory / DefaultGcmParameterSpecFactory - Creates GCM parameter specs

factory/
  EncryptorFactory / DefaultEncryptorFactory - Creates encryptor instances
  TokenRecordFactory / DefaultTokenRecordFactory - Creates TokenRecord entities

model/
  TokenRecord              - JPA entity (token, encrypted value, key version)

repository/
  TokenRepository          - Spring Data JPA repository

dto/
  TokenizeRequest / TokenizeResponse
  DetokenizeRequest / DetokenizeResponse
  EncryptedData / ErrorResponse

config/
  EncryptionProperties     - Key configuration from application.yml

exception/
  EncryptionException      - Encryption/decryption failures
  TokenNotFoundException   - Unknown token lookup

utils/
  TokenGenerator           - UUID-based token generation
```

## Key Design Decisions

- **Deterministic encryption**: Same plaintext + same key = same ciphertext. This enables idempotent tokenization via database lookup on `(encrypted_value, key_version)` without storing a separate hash.
- **Key versioning**: Encryption keys are versioned, supporting future key rotation. Each token record stores which key version was used.