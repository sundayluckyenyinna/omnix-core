package com.accionmfb.omnix.core.util;

import com.accionmfb.omnix.core.annotation.ServiceOperation;
import com.accionmfb.omnix.core.commons.ResponseCode;
import com.accionmfb.omnix.core.exception.OmnixApiException;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(value = {HttpUtil.HttpUtilProperties.class })
public class HttpUtil {

    private static HttpUtilProperties statichttpUtilProperties;
    private final HttpUtilProperties httpUtilProperties;

    private final static String CLIENT_PRETTY_RESPONSE = "Something went wrong";

    @ServiceOperation(
            description = "Resolve a give exception into the base OmnixApiException. If the exception passed is an instance of OmnixApiException," +
                    "it is returned, otherwise a new OmnixApiException object is returned to the caller."
    )
    public static OmnixApiException getResolvedException(Exception exception){
        if(statichttpUtilProperties.getEnvironment().equalsIgnoreCase(OmnixHttpEnvironment.DEVELOPMENT.getDescription())){
            exception.printStackTrace();
        }
        if(exception instanceof OmnixApiException){
            return (OmnixApiException) exception;
        }
        return OmnixApiException.newInstance()
                .withCode(ResponseCode.INTERNAL_SERVER_ERROR)
                .withMessage(exception.getMessage())
                .withError(CLIENT_PRETTY_RESPONSE);
    }

    public static OmnixApiException getResolvedException(String message){
        return OmnixApiException.newInstance()
                .withCode(ResponseCode.INTERNAL_SERVER_ERROR)
                .withMessage(message)
                .withError(CLIENT_PRETTY_RESPONSE);
    }

    public static OmnixApiException getResolvedFailedModelException(String message){
        return OmnixApiException.newInstance()
                .withCode(ResponseCode.FAILED_MODEL)
                .withMessage(message)
                .withError(message);
    }

    public static OmnixApiException getResolvedFailedModelException(Exception exception){
        return OmnixApiException.newInstance()
                .withCode(ResponseCode.FAILED_MODEL)
                .withMessage(exception.getMessage())
                .withError(exception.getMessage());
    }

    public static OmnixApiException getResolvedException(String responseCode, String responseMessage){
        return OmnixApiException.newInstance()
                .withCode(responseCode)
                .withMessage(responseMessage)
                .withError(responseMessage);
    }

    @Data
    @ConfigurationProperties(prefix = "omnix.http")
    public static class HttpUtilProperties{
        private String environment = "Production";
    }

    @Getter
    @RequiredArgsConstructor
    public enum OmnixHttpEnvironment{
        PRODUCTION("Production"),
        DEVELOPMENT("Development");

        private final String description;
    }

    @Bean
    public String staticConfiguration(){
        statichttpUtilProperties = httpUtilProperties;
        return "Success";
    }
}
