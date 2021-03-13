package com.davidsoft.http;

import com.davidsoft.utils.JsonNode;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpContentJsonProvider implements HttpContentProvider {

    private final HttpContentStringProvider stringProvider;

    public HttpContentJsonProvider(JsonNode jsonNode, Charset encodingCharset) {
        stringProvider = new HttpContentStringProvider(jsonNode.toString(), encodingCharset, "application/json", encodingCharset);
    }

    public HttpContentJsonProvider(JsonNode jsonNode) {
        this(jsonNode, StandardCharsets.UTF_8);
    }

    @Override
    public String getMimeType() {
        return stringProvider.getMimeType();
    }

    @Override
    public Charset getCharset() {
        return stringProvider.getCharset();
    }

    @Override
    public int getContentLength() {
        return stringProvider.getContentLength();
    }

    @Override
    public void onProvide(OutputStream out) throws IOException {
        stringProvider.onProvide(out);
    }
}
