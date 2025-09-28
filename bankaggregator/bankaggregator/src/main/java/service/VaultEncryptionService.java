package service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;
import util.AESUtil;

@Service
public class VaultEncryptionService {

    @Autowired
    private VaultTemplate vaultTemplate;

    private String encryptionKey;

    @PostConstruct
    public void init(){
        VaultResponseSupport<KeyValue> response = vaultTemplate.read("secret/bank-aggregator", KeyValue.class);
        this.encryptionKey = response.getData().getKey();
    }

    public String encrypt(String plainText) {
        return AESUtil.encrypt(plainText, encryptionKey);
    }

    public String decrypt(String cypherText) {
        return AESUtil.decrypt(cypherText, encryptionKey);
    }

    public static class KeyValue {
        private String key;
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
    }

}
