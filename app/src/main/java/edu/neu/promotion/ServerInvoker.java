package edu.neu.promotion;

import com.davidsoft.http.HttpContentProvider;
import com.davidsoft.http.HttpContentReceiver;
import com.davidsoft.http.UnacceptableException;
import com.davidsoft.utils.JsonNode;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import edu.neu.promotion.enties.ServerResponseNode;
import edu.neu.promotion.tasks.Task;


public final class ServerInvoker implements Task {

    private static String userAgent;
    private static String xRequestWith;

    public static void initialize(String appPackageName, String appVersionString) {
        String javaVersion = System.getProperty("java.version");
        if (javaVersion == null) {
            userAgent = "AndroidApp/" + appPackageName + " (" + appPackageName + ")" + " HttpURLConnection";
        }
        else {
            int findPos = javaVersion.indexOf("_");
            if (findPos == -1) {
                findPos = javaVersion.length();
            }
            userAgent = "AndroidApp/" + appPackageName + " (" + appPackageName + ")" + " HttpURLConnection/" + javaVersion.substring(0, findPos);
        }
        xRequestWith = "AndroidAppInterface/" + appVersionString;
    }

    private final String url;
    private final HttpContentProvider contentProvider;
    private final HttpContentReceiver<?> contentReceiver;
    private final Map<String, String> extraHeaders;

    public static final class InvokeResult {

        private final HttpURLConnection urlConnectionObject;
        private final Object contentObject;

        private InvokeResult(HttpURLConnection urlConnectionObject, Object contentObject) {
            this.urlConnectionObject = urlConnectionObject;
            this.contentObject = contentObject;
        }

        public <T> T getContent() {
            return (T) contentObject;
        }

        public void close() {
            urlConnectionObject.disconnect();
        }

        public String getResponseHeader(String field) {
            return urlConnectionObject.getHeaderField(field);
        }
    }

    private HttpURLConnection httpURLConnection;
    private InvokeResult result;

    /**
     * @param contentProvider 为null代表使用GET方法，否则使用POST
     */
    public ServerInvoker(String url, HttpContentProvider contentProvider, HttpContentReceiver<?> contentReceiver, Map<String, String> extraHeaders) {
        this.url = url;
        this.contentProvider = contentProvider;
        this.contentReceiver = contentReceiver;
        this.extraHeaders = extraHeaders;
    }

    @Override
    public boolean onExecute() {
        try {
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            if (contentProvider == null) {
                httpURLConnection.setRequestMethod("GET");
            }
            else {
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                if (contentProvider.getMimeType() != null) {
                    if (contentProvider.getCharset() == null) {
                        httpURLConnection.setRequestProperty("Content-Type", contentProvider.getMimeType());
                    }
                    else {
                        httpURLConnection.setRequestProperty("Content-Type", contentProvider.getMimeType() + "; charset=" + contentProvider.getCharset().displayName());
                    }
                }
                httpURLConnection.setRequestProperty("Content-Length", String.valueOf(contentProvider.getContentLength()));
            }
            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.setRequestProperty("User-Agent", userAgent);
            httpURLConnection.setRequestProperty("X-Requested-With", xRequestWith);
            httpURLConnection.setRequestProperty("connection", "close");
            if (extraHeaders != null) {
                for (Map.Entry<String, String> entry : extraHeaders.entrySet()) {
                    httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            httpURLConnection.connect();
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            if (contentProvider != null) {
                contentProvider.onProvide(httpURLConnection.getOutputStream());
            }
            if (contentReceiver == null) {
                result = new InvokeResult(httpURLConnection, httpURLConnection.getInputStream());
            }
            else {
                result = new InvokeResult(httpURLConnection, contentReceiver.onReceive(httpURLConnection.getInputStream(), -1));
                httpURLConnection.disconnect();
            }
        }
        catch (IOException | UnacceptableException e) {
            e.printStackTrace();
            httpURLConnection.disconnect();
            return false;
        }
        return true;
    }

    @Override
    public boolean onRetry() {
        return onExecute();
    }

    @Override
    public void onCancel() {
        httpURLConnection.disconnect();
    }

    @Override
    public Object onGetResult() {
        return result;
    }

    public static String getUserAgent() {
        return userAgent;
    }

    public static String getXRequestWith() {
        return xRequestWith;
    }
}
