package com.accionmfb.omnix.core.encryption;

import com.accionmfb.omnix.core.encryption.manager.SimpleOmnixEncryptionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        SimpleOmnixEncryptionService.class,
        AesEncryptionAlgorithmService.class,
})
public class EncryptionModule {
}
