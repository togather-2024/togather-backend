package com.togather.jasypt;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.junit.jupiter.api.Test;

public class JasyptEncryptorTest {

    @Test
    public void test() {
        String password = "ckazmfozj"; //jasypt password

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setPoolSize(4);
        encryptor.setPassword(password);
        encryptor.setAlgorithm("PBEWithMD5AndTripleDES");

        String content = "jdbc:mysql://party-develop.cf6m4i00us8k.ap-northeast-2.rds.amazonaws.com:3306/togather"; //암호화 할 내용
        String encryptedContent = encryptor.encrypt(content); //암호화
        //String encryptedContent = "gCjaSeiCVvFsvVxv09npObiEYRW9JQ9/b+Bdxe7Vabcv/J+ALWaWpTzIFsp2Z/qZMKhrn6ZgLBSQDt332tv9V2c7CfuTciCtVs6uknPc0RJ4ZW8YdXgopYPF5Rve14XselGhA5zlonC5icuuqvKXFGwMeDtvf0m2j5dKCXuf7mI=";
        String decryptedContent = encryptor.decrypt(encryptedContent); //복호화

        System.out.println("Enc : " + encryptedContent + ", Dec : " + decryptedContent);
    }
}
