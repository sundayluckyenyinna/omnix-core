package com.accionmfb.omnix.core.encryption;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EncryptionConfig {

    private String encryptionKey;
    private String encryptionAlgorithm;
    private String privateKeyFile;
    private String publicKeyFile;
}
