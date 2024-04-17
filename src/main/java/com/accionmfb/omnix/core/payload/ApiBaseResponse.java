package com.accionmfb.omnix.core.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiBaseResponse {

    @Schema(example = "00", description = "response code for the request", required = true)
    protected String responseCode;

    @Schema(example = "Successful operation", description = "response message for the request and describing the API response", required = true)
    protected String responseMessage;

    @Schema(example = "[]", description = "List of customer side errors")
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
        setErrors(new ArrayList<>(errors));
        return this;
    }

    public ApiBaseResponse withError(String error){
        this.errors = this.errors == null ? new ArrayList<>() : this.errors;
        this.errors.add(error);
        return this;
    }
}
