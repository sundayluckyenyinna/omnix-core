package com.accionmfb.omnix.core.instrumentation;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamWrapper extends InputStream {
    private final InputStream originalInputStream;

    public InputStreamWrapper(InputStream originalInputStream) {
        this.originalInputStream = originalInputStream;
    }

    @Override
    public int read() throws IOException {
        return originalInputStream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return originalInputStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return originalInputStream.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return originalInputStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        return originalInputStream.available();
    }

    @Override
    public void close() throws IOException {
        // Do not close the original InputStream
    }
}
