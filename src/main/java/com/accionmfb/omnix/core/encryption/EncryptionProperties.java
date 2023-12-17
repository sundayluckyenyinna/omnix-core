package com.accionmfb.omnix.core.encryption;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;

@Data
@Primary
@ConfigurationProperties(prefix = "omnix.encryption")
public class EncryptionProperties {
    boolean enableEncryption = true;
    private String algorithm = "AES";
    private String aesEncryptionKey = "77T18925x42783H7508302949Q618671";
    private String pgpPublicKey = "C:/Omnix/security/encryption/pgp/public_key.pub";
    private String pgpPrivateKey = "C:/Omnix/security/encryption/pgp/private_key.pub";
    private String privateKeyPassword = "ENC(Y73o3wf5u9FIJYQj7f+g7XekcBQ7bnig)";
}
