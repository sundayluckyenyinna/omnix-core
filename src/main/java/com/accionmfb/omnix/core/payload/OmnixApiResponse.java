package com.accionmfb.omnix.core.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class OmnixApiResponse<T>{
    protected String responseCode;
    protected String responseMessage;
    protected List<String> errors = new ArrayList<>();
    private T responseData;

    public static <T> OmnixApiResponse<T> getInstance(Class<T> tClass){
        return new OmnixApiResponse<>();
    }

    public OmnixApiResponse<T> withResponseCode(String responseCode){
        setResponseCode(responseCode);
        return this;
    }

    public OmnixApiResponse<T> withResponseMessage(String responseMessage){
        setResponseMessage(responseMessage);
        return this;
    }

    public OmnixApiResponse<T> withErrors(Collection<String> errors){
        setErrors(errors.stream().toList());
        return this;
    }

    public OmnixApiResponse<T> withError(String error){
        this.errors = this.errors == null ? new ArrayList<>() : this.errors;
        this.errors.add(error);
        return this;
    }
}
