package com.accionmfb.omnix.core.exception;

import com.accionmfb.omnix.core.commons.ResponseCodes;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class OmnixApiException  extends RuntimeException {

    private String code;
    private String message;
    private int statusCode = HttpStatus.OK.value();
    private List<String> errors;

    private OmnixApiException(){
        super();
    }

    private OmnixApiException(String message){
        super(message);
        this.code = ResponseCodes.INTERNAL_SERVER_ERROR.getResponseCode();
        this.message = message;
        this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.errors = new ArrayList<>();
    }

    public static OmnixApiException newInstance(){
        return new OmnixApiException();
    }

    public OmnixApiException withCode(String code){
        this.code = code;
        this.errors = new ArrayList<>();
        return this;
    }

    public OmnixApiException withMessage(String message){
        this.message = message;
        this.errors = new ArrayList<>();
        return this;
    }

    public OmnixApiException withStatusCode(int statusCode){
        this.statusCode = statusCode;
        this.errors = new ArrayList<>();
        return this;
    }

    public OmnixApiException withError(String error){
        this.errors = Objects.isNull(errors) ? new ArrayList<>() : errors;
        this.errors.add(error);
        return this;
    }

    public OmnixApiException withErrors(List<String> errors){
        this.errors = Objects.isNull(errors) ? new ArrayList<>() : errors;
        this.errors.addAll(errors);
        return this;
    }
}
