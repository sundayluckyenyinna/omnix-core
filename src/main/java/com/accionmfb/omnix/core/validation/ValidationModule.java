package com.accionmfb.omnix.core.validation;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration

@Import({
        ConstraintValidationRegistry.class
})
public class ValidationModule {
}
