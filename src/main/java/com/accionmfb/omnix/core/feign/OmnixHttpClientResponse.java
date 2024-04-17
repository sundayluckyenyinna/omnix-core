package com.accionmfb.omnix.core.feign;


import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class OmnixHttpClientResponse implements ClientHttpResponse {
    private final ClientHttpResponse clientHttpResponse;
    private final String body;
    @Getter
    private final String encryptedBody;


    public OmnixHttpClientResponse(ClientHttpResponse clientHttpResponse, String body, String encryptedBody) {
        this.clientHttpResponse = clientHttpResponse;
        this.body = body;
        this.encryptedBody = encryptedBody;
    }

    @Override
    public HttpStatus getStatusCode() throws IOException {
        return clientHttpResponse.getStatusCode();
    }

    @Override
    public int getRawStatusCode() throws IOException {
        return clientHttpResponse.getRawStatusCode();
    }

    @Override
    public String getStatusText() throws IOException {
        return this.clientHttpResponse.getStatusText();
    }

    @Override
    public void close() {
        clientHttpResponse.close();
    }

    @Override
    public InputStream getBody() throws IOException {
        return new ByteArrayInputStream(this.body.getBytes());
    }

    public String getBodyString(){
        return this.body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return clientHttpResponse.getHeaders();
    }
}
