package com.assignment.walnut.config;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@Component
public class HmacValidator {

    private final SecureDataConfig secureDataConfig;

    public HmacValidator(SecureDataConfig secureDataConfig) {
        this.secureDataConfig = secureDataConfig;
    }

    public boolean isValidHmac(String data, String hmac) {
        try {
            String secretKey = Files.readString(Paths.get(secureDataConfig.getSecretKeyFile())).trim();
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey.getBytes(), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(hash).equals(hmac);
        } catch (Exception e) {
            return false;
        }
    }
}
