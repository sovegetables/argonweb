package cn.sovegetables.web;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

class WebViewClientList extends WebViewClient {

    final List<WebViewClientAdapter> mWebViewClient = new ArrayList<>();

    WebViewClientList() {
        super();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        for (WebViewClient c: mWebViewClient){
            if(c.shouldOverrideUrlLoading(view, url)){
                return true;
            }
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        for (WebViewClient c: mWebViewClient){
            if(c.shouldOverrideUrlLoading(view, request)){
                return true;
            }
        }
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        for (WebViewClient c: mWebViewClient){
            c.onPageStarted(view, url, favicon);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        for (WebViewClient c: mWebViewClient){
            c.onPageFinished(view, url);
        }
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        for (WebViewClient c: mWebViewClient){
            c.onLoadResource(view, url);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onPageCommitVisible(WebView view, String url) {
        for (WebViewClient c: mWebViewClient){
            c.onPageCommitVisible(view, url);
        }
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        WebResourceResponse temp;
        for (WebViewClient c: mWebViewClient){
            temp =  c.shouldInterceptRequest(view, url);
            if(temp != null){
                return temp;
            }
        }
        return super.shouldInterceptRequest(view, url);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        for (WebViewClient c: mWebViewClient){
            WebResourceResponse response =  c.shouldInterceptRequest(view, request);
            if (response != null) {
                return response;
            }
        }
        return super.shouldInterceptRequest(view, request);
    }

    @Override @Deprecated
    public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
        for (WebViewClient c: mWebViewClient){
            c.onTooManyRedirects(view, cancelMsg, continueMsg);
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        for (WebViewClient c: mWebViewClient){
            c.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        for (WebViewClient c: mWebViewClient){
            c.onReceivedError(view, request, error);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        for (WebViewClient c: mWebViewClient){
            c.onReceivedHttpError(view, request, errorResponse);
        }
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        for (WebViewClient c: mWebViewClient){
            c.onFormResubmission(view, dontResend, resend);
        }
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        for (WebViewClient c: mWebViewClient){
            c.doUpdateVisitedHistory(view, url, isReload);
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        for (WebViewClient c: mWebViewClient){
            c.onReceivedSslError(view, handler, error);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        for (WebViewClient c: mWebViewClient){
            c.onReceivedClientCertRequest(view, request);
        }
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        for (WebViewClient c: mWebViewClient){
            c.onReceivedHttpAuthRequest(view, handler, host, realm);
        }
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        for (WebViewClient c: mWebViewClient){
            if(c.shouldOverrideKeyEvent(view, event)){
                return true;
            }
        }
        return super.shouldOverrideKeyEvent(view, event);
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        for (WebViewClient c: mWebViewClient){
            c.onUnhandledKeyEvent(view, event);
        }
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        for (WebViewClient c: mWebViewClient){
            c.onScaleChanged(view, oldScale, newScale);
        }
    }

    @Override
    public void onReceivedLoginRequest(WebView view, String realm, @Nullable String account, String args) {
        for (WebViewClient c: mWebViewClient){
            c.onReceivedLoginRequest(view, realm, account, args);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
        for (WebViewClient c: mWebViewClient){
            if(c.onRenderProcessGone(view, detail)){
                return true;
            }
        }
        return super.onRenderProcessGone(view, detail);
    }

    @TargetApi(Build.VERSION_CODES.O_MR1)
    @Override
    public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
        for (WebViewClient c: mWebViewClient){
            c.onSafeBrowsingHit(view, request, threatType, callback);
        }
    }

    void addWebChromeClient(WebViewClientAdapter client) {
        mWebViewClient.add(client);
    }
}
