package com.davidsoft.http;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public interface HttpContentProvider {

    String getMimeType();

    Charset getCharset();

    int getContentLength();

    void onProvide(OutputStream out) throws IOException;
}