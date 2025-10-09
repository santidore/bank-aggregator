package com.example.bankaggregator.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;
import com.example.bankaggregator.util.AESUtil;

import java.util.Map;

@Service
public class VaultEncryptionService {

    @Autowired
    private VaultTemplate vaultTemplate;

    private String encryptionKey;



    @PostConstruct
    public void init() {
        VaultResponse response = vaultTemplate.read("secret/data/bank-aggregator");
        if(response != null && response.getData() != null) {

            @SuppressWarnings("unchecked")
            Map<String,Object> dataData = (Map<String,Object>) response.getData().get("data");
            this.encryptionKey = (String) dataData.get("encryptionKey");
        }
    }

    public String encrypt(String plainText) {
        return AESUtil.encrypt(plainText, encryptionKey);
    }

    public String decrypt(String cypherText) {
        return AESUtil.decrypt(cypherText, encryptionKey);
    }

    public static class KeyValue {
        private String encryptionKey;
        public String getEncryptionKey() { return encryptionKey; }
        public void setEncryptionKey(String encryptionKey) { this.encryptionKey = encryptionKey; }
    }

}
