package com.accionmfb.omnix.core.validation;

import com.accionmfb.omnix.core.validation.constraints.IsInValidator;
import com.accionmfb.omnix.core.validation.constraints.ListValidator;
import com.accionmfb.omnix.core.validation.constraints.NullEmptyValidator;
import com.accionmfb.omnix.core.validation.constraints.NumberStringValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class ConstraintValidationRegistry {

    @Bean()
    public IsInValidator isInValidator(){
        return new IsInValidator();
    }

    @Bean
    public ListValidator listValidator(){
        return new ListValidator();
    }

    @Bean
    public NullEmptyValidator nullEmptyValidator(){
        return new NullEmptyValidator();
    }

    @Bean
    public NumberStringValidator numberStringValidator(){
        return new NumberStringValidator();
    }
}
