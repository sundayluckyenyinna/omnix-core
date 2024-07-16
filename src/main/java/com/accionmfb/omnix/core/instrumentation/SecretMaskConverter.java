package com.accionmfb.omnix.core.instrumentation;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;
import ch.qos.logback.core.pattern.Converter;

public class SecretMaskConverter extends CompositeConverter<ILoggingEvent> {
    @Override
    public String transform(ILoggingEvent event, String message) {
        if (message == null) {
            return null;
        }
        message = message.replace("password=[^&\\s]*", "password=****").replace("password:[^&\\s]*", "password:****");
        message = message.replace("pin=[^&\\s]*", "pin=****").replace("pin:[^&\\s]*", "pin:****");
        message = message.replace("ssn=[^&\\s]*", "ssn=****").replace("ssn:[^&\\s]*", "ssn:****");
        message = message.replace("creditCardNumber=[^&\\s]*", "creditCardNumber=****");
        message = message.replace("cvv=[^&\\s]*", "cvv=****").replace("cvv:[^&\\s]*", "cvv:****");
        message = message.replace("transactionPin=[^&\\s]*", "transactionPin=****").replace("transactionPin:[^&\\s]*", "transactionPin:****");
        message = message.replace("authValue=[^&\\s]*", "authValue=****").replace("authValue:[^&\\s]*", "authValue:****");
        return message;
    }
}
