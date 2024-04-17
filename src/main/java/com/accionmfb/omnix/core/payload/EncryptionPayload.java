package com.accionmfb.omnix.core.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EncryptionPayload {
    private String request;
    private String response;

    public static EncryptionPayload withRequest(String encRequest){
        return EncryptionPayload.builder().request(encRequest).build();
    }

    public static EncryptionPayload withResponse(String encResponse){
        return EncryptionPayload.builder().response(encResponse).build();
    }
}
