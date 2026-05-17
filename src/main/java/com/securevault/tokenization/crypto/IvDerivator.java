package com.securevault.tokenization.crypto;

public interface IvDerivator {

    byte[] deriveIv(byte[] input);
}