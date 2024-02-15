package com.togather.config.jasypt;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableEncryptableProperties
public class JasyptConfig {

    @Value("${jasypt.encryption.key}")
    private String jasyptEncryptionKey;

    @Bean
    public PooledPBEStringEncryptor jasyptStringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor(); //멀티코어 시스템에서 해독을 병렬 처리하는 인크립터

        encryptor.setPoolSize(4); //머신 코어 수와 동일하게 지정
        encryptor.setPassword(jasyptEncryptionKey);
        encryptor.setAlgorithm("PBEWithMD5AndTripleDES"); // 암호화 알고리즘 설정

        return encryptor;
    }

}
