package com.davidsoft.http;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpContentFormProvider implements HttpContentProvider {

    private final HttpContentStringProvider stringProvider;

    public HttpContentFormProvider(Map<String, String> formData) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            builder.append(UrlCodec.urlEncode(entry.getKey().getBytes(StandardCharsets.UTF_8)));
            if (entry.getValue() != null) {
                builder.append("=");
                builder.append(UrlCodec.urlEncode(entry.getKey().getBytes(StandardCharsets.UTF_8)));
            }
            builder.append("&");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        stringProvider = new HttpContentStringProvider(builder.toString(), StandardCharsets.UTF_8, "application/x-www-form-urlencoded", null);
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
