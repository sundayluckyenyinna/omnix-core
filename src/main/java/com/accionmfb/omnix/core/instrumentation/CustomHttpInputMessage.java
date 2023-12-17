package com.accionmfb.omnix.core.instrumentation;

import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.InputStream;

@Data
public class CustomHttpInputMessage implements HttpInputMessage {
    private final InputStream body;
    private final HttpHeaders headers;

    public CustomHttpInputMessage(InputStream body, HttpHeaders headers) {
        this.body = body;
        this.headers = headers;
    }


    @Override
    public InputStream getBody() {
        return body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}
