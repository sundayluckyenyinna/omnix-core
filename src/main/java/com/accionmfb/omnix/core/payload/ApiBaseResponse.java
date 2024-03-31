package com.accionmfb.omnix.core.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiBaseResponse {
    protected String responseCode;
    protected String responseMessage;
    protected List<String> errors = new ArrayList<>();


    public static ApiBaseResponse getInstance(){
        return new ApiBaseResponse();
    }
    public ApiBaseResponse withResponseCode(String responseCode){
        setResponseCode(responseCode);
        return this;
    }

    public ApiBaseResponse withResponseMessage(String responseMessage){
        setResponseMessage(responseMessage);
        return this;
    }

    public ApiBaseResponse withErrors(Collection<String> errors){
        setErrors(errors.stream().toList());
        return this;
    }

    public ApiBaseResponse withError(String error){
        this.errors = this.errors == null ? new ArrayList<>() : this.errors;
        this.errors.add(error);
        return this;
    }
}
