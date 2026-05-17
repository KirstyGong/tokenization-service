package com.securevault.tokenization.crypto;

import javax.crypto.Cipher;

public interface CipherFactory {

    Cipher create();
}