package edu.neu.promotion;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.PermissionRequest;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import java.util.HashMap;

import edu.neu.promotion.components.AlertDialog;
import edu.neu.promotion.components.Page;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.views.SimpleProgressView;

public class WebPage extends Page {

    private static final int PERMISSION_REQUEST_GEOLOCATION = 1;
    private static final int PERMISSION_REQUEST_CAMERA = 2;

    private static final String[] GEOLOCATION_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final String[] CAMERA_PERMISSIONS = new String[] {
            Manifest.permission.CAMERA
    };

    private final String url;

    private WebView webView;
    private SimpleProgressView simpleProgressView;

    private AlertDialog requestGeolocationDialog;
    private String geolocationOrigin;
    private GeolocationPermissions.Callback geolocationCallback;

    private AlertDialog requestCameraDialog;
    private PermissionRequest cameraPermissionRequest;

    public WebPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
        url = (String) args[0];
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate() {
        super.onCreate();

        FrameLayout frameLayout = new FrameLayout(getContext());
        webView = new WebView(getContext());
        frameLayout.addView(webView, -1, -1);
        simpleProgressView = new SimpleProgressView(getContext());
        frameLayout.addView(simpleProgressView, -1, (int) applyDimensions(TypedValue.COMPLEX_UNIT_DIP, 3));
        setContentView(frameLayout);

        webView.clearCache(true);
        webView.clearHistory();
        webView.clearFormData();

        WebSettings webSettings = webView.getSettings();

        //设置ua
        StringBuilder origUa = new StringBuilder(webSettings.getUserAgentString());
        int start = origUa.indexOf("(");
        int end = origUa.indexOf(")", start + 1);
        origUa.replace(start, end + 1, "(Linux; Android " + Build.VERSION.RELEASE + "; " + Build.MODEL + ")");
        webSettings.setUserAgentString(origUa.toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(false);

        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(false);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportMultipleWindows(true);

        System.err.println("UI线程的线程ID是：" + Thread.currentThread().getId());

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return false;
                }
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                catch (Exception ignored){}
                return true;
            }

            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                //TODO：重新提交表单
                super.onFormResubmission(view, dontResend, resend);
                System.err.println("onFormResubmission()的线程的线程ID是：" + Thread.currentThread().getId());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                simpleProgressView.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                simpleProgressView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                //TODO：页面加载错误
                super.onReceivedError(view, request, error);
                System.err.println("onReceivedError()的线程的线程ID是：" + Thread.currentThread().getId());
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                //TODO：需要登录
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
                System.err.println("onReceivedHttpAuthRequest()的线程的线程ID是：" + Thread.currentThread().getId());
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                //TODO：页面发生HTTP错误
                super.onReceivedHttpError(view, request, errorResponse);
                System.err.println("onReceivedHttpError()的线程的线程ID是：" + Thread.currentThread().getId());
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //TODO：显示证书错误
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
                //TODO：询问是否返回安全连接
                super.onSafeBrowsingHit(view, request, threatType, callback);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress != 100) {
                    simpleProgressView.setVisibility(View.VISIBLE);
                    simpleProgressView.setValue(newProgress / 100f);
                }
                else {
                    simpleProgressView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                setTitle(title);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                geolocationOrigin = origin;
                geolocationCallback = callback;
                requestGeolocationDialog = AlertDialog.Builder.getCenter(getContext())
                        .setMessage(origin + " 想使用您设备的位置信息。")
                        .setButton(DialogInterface.BUTTON_POSITIVE, "允许", true)
                        .setButton(DialogInterface.BUTTON_NEGATIVE, "禁止", true)
                        .setOnDialogButtonClickListener((dialog, which) -> {
                            dialog.dismiss();
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                requestPermissions(PERMISSION_REQUEST_GEOLOCATION, GEOLOCATION_PERMISSIONS);
                            }
                            else {
                                WebPage.this.geolocationCallback.invoke(WebPage.this.geolocationOrigin, false, false);
                            }
                        })
                        .setOnCancelListener(dialog -> WebPage.this.geolocationCallback.invoke(WebPage.this.geolocationOrigin, false, false))
                        .show();
            }

            @Override
            public void onGeolocationPermissionsHidePrompt() {
                if (requestGeolocationDialog != null) {
                    geolocationOrigin = null;
                    geolocationCallback = null;
                    requestGeolocationDialog.cancel();
                    requestGeolocationDialog = null;
                }
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if (request.getResources()[0].endsWith("VIDEO_CAPTURE")) {
                    cameraPermissionRequest = request;
                    requestCameraDialog = AlertDialog.Builder.getCenter(getContext())
                            .setMessage(request.getOrigin() + " 想使用您设备的摄像头")
                            .setButton(DialogInterface.BUTTON_POSITIVE, "允许", true)
                            .setButton(DialogInterface.BUTTON_NEGATIVE, "禁止", true)
                            .setOnDialogButtonClickListener((dialog, which) -> {
                                dialog.dismiss();
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    requestPermissions(PERMISSION_REQUEST_CAMERA, CAMERA_PERMISSIONS);
                                }
                                else {
                                    cameraPermissionRequest.deny();
                                }
                            })
                            .setOnCancelListener(dialog -> cameraPermissionRequest.deny())
                            .show();
                }
                else {
                    super.onPermissionRequest(request);
                }
            }

            @Override
            public void onPermissionRequestCanceled(PermissionRequest request) {
                if (request == cameraPermissionRequest) {
                    cameraPermissionRequest = null;
                    requestCameraDialog.cancel();
                }
                else {
                    super.onPermissionRequestCanceled(request);
                }
            }
        });

        HashMap<String, String> extraHeaders = new HashMap<>(1);
        extraHeaders.put("X-Requested-With", ServerInvoker.getXRequestWith());
        webView.loadUrl(url, extraHeaders);
    }

    @Override
    protected void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_GEOLOCATION:
                if (geolocationOrigin != null && geolocationCallback != null) {
                    WebPage.this.geolocationCallback.invoke(WebPage.this.geolocationOrigin, isPermissionsAllGranted(grantResults), false);
                }
                break;
            case PERMISSION_REQUEST_CAMERA:
                if (cameraPermissionRequest != null) {
                    if (isPermissionsAllGranted(grantResults)) {
                        cameraPermissionRequest.grant(cameraPermissionRequest.getResources());
                    }
                    else {
                        cameraPermissionRequest.deny();
                    }
                }
                break;
        }
    }

    @Override
    protected boolean onGoBack() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onGoBack();
    }
}