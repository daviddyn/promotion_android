package com.davidsoft.http;

import com.davidsoft.utils.JsonNode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

public class HttpContentJsonReceiver implements HttpContentReceiver<JsonNode> {

    private final Charset charset;

    public HttpContentJsonReceiver(Charset charset) {
        this.charset = charset;
    }

    public HttpContentJsonReceiver() {
        this(StandardCharsets.UTF_8);
    }

    @Override
    public JsonNode onReceive(InputStream in, long contentLength) throws UnacceptableException, IOException {
        try {
            return JsonNode.parseJson(new HttpContentStringReceiver(charset).onReceive(in, contentLength));
        } catch (ParseException e) {
            throw new UnacceptableException("无法将Content的内容解析为Json", e);
        }
    }
}
