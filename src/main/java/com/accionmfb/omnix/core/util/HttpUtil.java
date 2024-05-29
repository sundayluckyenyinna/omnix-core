package com.accionmfb.omnix.core.util;

import com.accionmfb.omnix.core.annotation.ServiceOperation;
import com.accionmfb.omnix.core.commons.ResponseCode;
import com.accionmfb.omnix.core.exception.OmnixApiException;

public class HttpUtil {

    private final static String CLIENT_PRETTY_RESPONSE = "Something went wrong";

    @ServiceOperation(
            description = "Resolve a give exception into the base OmnixApiException. If the exception passed is an instance of OmnixApiException," +
                    "it is returned, otherwise a new OmnixApiException object is returned to the caller."
    )
    public static OmnixApiException getResolvedException(Exception exception){
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
}
