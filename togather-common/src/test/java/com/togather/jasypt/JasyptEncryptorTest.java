package com.togather.jasypt;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class JasyptEncryptorTest {

    @Test
    @Disabled
    public void test() {
        String password = ""; //jasypt password

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setPoolSize(4);
        encryptor.setPassword(password);
        encryptor.setAlgorithm("PBEWithMD5AndTripleDES");

        String content = ""; //암호화 할 내용
        String encryptedContent = encryptor.encrypt(content); //암호화
        String decryptedContent = encryptor.decrypt(encryptedContent); //복호화
        System.out.println("Enc : " + encryptedContent + ", Dec : " + decryptedContent);
    }
}
