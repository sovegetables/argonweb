package cn.sovegetables.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Method;

/**
 * Created by albert on 2018/10/18.
 * 对WebView进行了封装：
 * 1. 对WebSettings进行通用初始化
 * 2. 对WebChromeClient和WebViewClient进行了二次封装，可以调用addWebChromeClient和addWebViewClient方法添加多个。
 * 3. 设置了默认的DefaultDownloadListener，是使用系统浏览器进行下载
 */

public class ArgonWebView extends WebView {

    @SuppressWarnings("unused")
    private static final String TAG = "RcsWebView";

    private static final String UTF_8 = "UTF-8";
    private WebChromeClientList mWebChromeClientList;
    private WebViewClientList mWebViewClientList;

    public ArgonWebView(Context context) {
        super(context, null);
        init(context);
    }

    public ArgonWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ArgonWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("unused")
    public ArgonWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @SuppressWarnings({"unused", "deprecated"})
    public ArgonWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        init(context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(Context context) {
        final WebSettings webSettings = getSettings();
        webSettings.setGeolocationEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDefaultTextEncodingName(UTF_8);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setDomStorageEnabled(true);
        String appCachePath = context.getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);
        webSettings.setAllowFileAccess(true);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        } else {
            try {
                Method m = WebSettings.class.getMethod("setMixedContentMode", int.class);
                m.invoke(webSettings, 0);
            } catch (Exception ex) {
                Log.e("WebSettings", "Error calling setMixedContentMode: " + ex.getMessage(), ex);
            }
        }
        String ua = webSettings.getUserAgentString();
        webSettings.setUserAgentString(ua + " argonweb" + Util.getVersionName(context));
        webSettings.setSupportMultipleWindows(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadsImagesAutomatically(true);
        mWebChromeClientList = new WebChromeClientList();
        super.setWebChromeClient(mWebChromeClientList);
        mWebViewClientList = new WebViewClientList();
        super.setWebViewClient(mWebViewClientList);
    }

    public final void addWebChromeClient(WebChromeClientAdapter client){
        mWebChromeClientList.addWebChromeClient(client);
    }

    @SuppressWarnings("unused")
    public final void addWebViewClient(WebViewClientAdapter client) {
        mWebViewClientList.addWebChromeClient(client);
    }

    WebChromeClientList getWebChromeClientList() {
        return mWebChromeClientList;
    }

    WebViewClientList getWebViewClientList() {
        return mWebViewClientList;
    }

    /**
     * Deprecated use#addWebChromeClient
     * @param client WebChromeClient
     */
    @Override
    @Deprecated
    public void setWebChromeClient(WebChromeClient client) {
        mWebChromeClientList.addWebChromeClient(new WebChromeClientWrapper(client));
    }

    /**
     * Deprecated use#addWebViewClient
     * @param client WebViewClient
     */
    @Override
    @Deprecated
    public void setWebViewClient(WebViewClient client) {
        mWebViewClientList.addWebChromeClient(new WebViewClientWrapper(client));
    }
}
