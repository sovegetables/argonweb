package cn.sovegetables.web;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

import androidx.annotation.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class WebChromeClientList extends WebChromeClientAdapter {

    final List<WebChromeClientAdapter> webChromeClients = new ArrayList<>();

    WebChromeClientList() {
        super();
    }

    @Override
    public void onProgressChanged(android.webkit.WebView view, int newProgress) {
        for (WebChromeClient c: webChromeClients){
            c.onProgressChanged(view, newProgress);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        for (WebChromeClient c: webChromeClients){
            c.onReceivedTitle(view, title);
        }
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        for (WebChromeClient c: webChromeClients){
            c.onReceivedIcon(view, icon);
        }
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        for (WebChromeClient c: webChromeClients){
            c.onReceivedTouchIconUrl(view, url, precomposed);
        }
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        for(WebChromeClient c: webChromeClients){
            c.onShowCustomView(view, callback);
        }
    }

    @Override
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        for(WebChromeClient c: webChromeClients){
            c.onShowCustomView(view, requestedOrientation, callback);
        }
    }

    @Override
    public void onHideCustomView() {
        for(WebChromeClient c: webChromeClients){
            c.onHideCustomView();
        }
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        for(WebChromeClient c: webChromeClients){
            if(c.onCreateWindow(view, isDialog, isUserGesture, resultMsg)){
                return true;
            }
        }
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }

    @Override
    public void onRequestFocus(WebView view) {
        for(WebChromeClient c: webChromeClients){
            c.onRequestFocus(view);
        }
    }

    @Override
    public void onCloseWindow(WebView window) {
        for(WebChromeClient c: webChromeClients){
            c.onCloseWindow(window);
        }
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        for(WebChromeClient c: webChromeClients){
            if(c.onJsAlert(view, url, message, result)){
                return true;
            }
        }
        return super.onJsAlert(view, url, message, result);
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        for(WebChromeClient c: webChromeClients){
            if(c.onJsConfirm(view, url, message, result)){
                return true;
            }
        }
        return super.onJsConfirm(view, url, message, result);
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        for(WebChromeClient c: webChromeClients){
            if(c.onJsPrompt(view, url, message, defaultValue, result)){
                return true;
            }
        }
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
        for(WebChromeClient c: webChromeClients){
            if(c.onJsBeforeUnload(view, url, message, result)){
                return true;
            }
        }
        return super.onJsBeforeUnload(view, url, message, result);
    }

    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
        for(WebChromeClient c: webChromeClients){
            c.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
        }
    }

    @Override
    public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
        for(WebChromeClient c: webChromeClients){
            c.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
        }
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        for(WebChromeClient c: webChromeClients){
            c.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        for(WebChromeClient c: webChromeClients){
            c.onGeolocationPermissionsHidePrompt();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPermissionRequest(PermissionRequest request) {
        for(WebChromeClient c: webChromeClients){
            c.onPermissionRequest(request);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPermissionRequestCanceled(PermissionRequest request) {
        for(WebChromeClient c: webChromeClients){
            c.onPermissionRequestCanceled(request);
        }
    }

    @Override
    public boolean onJsTimeout() {
        for(WebChromeClient c: webChromeClients){
            if(c.onJsTimeout()){
                return true;
            }
        }
        return super.onJsTimeout();
    }

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        for(WebChromeClient c: webChromeClients){
            c.onConsoleMessage(message, lineNumber, sourceID);
        }
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        for(WebChromeClient c: webChromeClients){
            if(c.onConsoleMessage(consoleMessage)){
                return true;
            }
        }
        return super.onConsoleMessage(consoleMessage);
    }

    @Nullable
    @Override
    public Bitmap getDefaultVideoPoster() {
        Bitmap temp;
        for(WebChromeClient c: webChromeClients){
            temp =  c.getDefaultVideoPoster();
            if(temp != null){
                return temp;
            }
        }
        return super.getDefaultVideoPoster();
    }

    @Nullable
    @Override
    public View getVideoLoadingProgressView() {
        View temp;
        for(WebChromeClient c: webChromeClients){
            temp =  c.getVideoLoadingProgressView();
            if(temp != null){
                return temp;
            }
        }
        return super.getVideoLoadingProgressView();
    }

    @Override
    public void getVisitedHistory(ValueCallback<String[]> callback) {
        for(WebChromeClient c: webChromeClients){
            c.getVisitedHistory(callback);
        }
    }

    // For Android > 4.1.1
    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        try {
            Method method = WebChromeClient.class.getMethod("openFileChooser", ValueCallback.class, String.class, String.class);
            for(WebChromeClient c: webChromeClients){
                method.invoke(c, uploadMsg, acceptType, capture);
            }
        } catch (Exception ignored) {
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        for(WebChromeClient c: webChromeClients){
            if(c.onShowFileChooser(webView, filePathCallback, fileChooserParams)){
                return true;
            }
        }
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
    }

    void addWebChromeClient(WebChromeClientAdapter client) {
        webChromeClients.add(client);
    }


}
