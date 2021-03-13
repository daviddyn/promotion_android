package com.davidsoft.http;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class HttpContentStringProvider implements HttpContentProvider {

    private final HttpContentBytesProvider bytesProvider;

    public HttpContentStringProvider(String content, Charset encodingCharset, String mimeType, Charset declaredCharset) {
        bytesProvider = new HttpContentBytesProvider(content.getBytes(encodingCharset), mimeType, declaredCharset);
        System.out.println("企图发送：");
        System.out.println(content);
        System.out.println();
    }

    @Override
    public String getMimeType() {
        return bytesProvider.getMimeType();
    }

    @Override
    public Charset getCharset() {
        return bytesProvider.getCharset();
    }

    @Override
    public int getContentLength() {
        return bytesProvider.getContentLength();
    }

    @Override
    public void onProvide(OutputStream out) throws IOException {
        bytesProvider.onProvide(out);
    }
}
