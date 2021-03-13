package com.davidsoft.http;

import java.io.IOException;
import java.io.InputStream;

public class HttpContentBytesReceiver implements HttpContentReceiver<byte[]> {

    @Override
    public byte[] onReceive(InputStream in, long contentLength) throws IOException {
        if (contentLength == -1) {
            return com.davidsoft.compact.InputStream.readAllBytes(in);
        }
        else {
            return com.davidsoft.compact.InputStream.readNBytes(in, (int) contentLength);
        }
    }
}
