package cn.sovegetables.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
        super(getFixedContext(context), null);
        init(context);
    }

    public ArgonWebView(Context context, AttributeSet attrs) {
        super(getFixedContext(context), attrs);
        init(context);
    }

    public ArgonWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(getFixedContext(context), attrs, defStyleAttr);
        init(context);
    }

    @SuppressWarnings({"unused", "deprecated"})
    public ArgonWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(getFixedContext(context), attrs, defStyleAttr, privateBrowsing);
        init(context);
    }


    //androidx在Android 5.1部分机型报Resources$NotFoundException

    /**
     * Caused by:
     * 5 android.content.res.Resources$NotFoundException:String resource ID #0x2040003
     * 6 android.content.res.Resources.getText(Resources.java:318)
     * 7 android.content.res.VivoResources.getText(VivoResources.java:123)
     * 8 android.content.res.Resources.getString(Resources.java:404)
     * 9 com.android.org.chromium.content.browser.ContentViewCore.setContainerView(ContentViewCore.java:694)
     * 10 com.android.org.chromium.content.browser.ContentViewCore.initialize(ContentViewCore.java:618)
     * 11 com.android.org.chromium.android_webview.AwContents.createAndInitializeContentViewCore(AwContents.java:631)
     * 12 com.android.org.chromium.android_webview.AwContents.setNewAwContents(AwContents.java:780)
     * 13 com.android.org.chromium.android_webview.AwContents.<init>(AwContents.java:619)
     * 14 com.android.org.chromium.android_webview.AwContents.<init>(AwContents.java:556)
     * 15 com.android.webview.chromium.WebViewChromium.initForReal(WebViewChromium.java:312)
     * 16 com.android.webview.chromium.WebViewChromium.access$100(WebViewChromium.java:96)
     * 17 com.android.webview.chromium.WebViewChromium$1.run(WebViewChromium.java:264)
     * 18 com.android.webview.chromium.WebViewChromium$WebViewChromiumRunQueue.drainQueue(WebViewChromium.java:123)
     * 19 com.android.webview.chromium.WebViewChromium$WebViewChromiumRunQueue$1.run(WebViewChromium.java:110)
     * 20 com.android.org.chromium.base.ThreadUtils.runOnUiThread(ThreadUtils.java:144)
     * 21 com.android.webview.chromium.WebViewChromium$WebViewChromiumRunQueue.addTask(WebViewChromium.java:107)
     * 22 com.android.webview.chromium.WebViewChromium.init(WebViewChromium.java:261)
     * 23 android.webkit.WebView.<init>(WebView.java:554)
     */
    private static Context getFixedContext(Context context) {
        // Android Lollipop 5.0 & 5.1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return context.createConfigurationContext(new Configuration());
        }
        return context;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        }
        webSettings.setDomStorageEnabled(true);
        String appCachePath = context.getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);
        webSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

        CookieManager.getInstance().removeAllCookie();
        String ua = webSettings.getUserAgentString();
        webSettings.setUserAgentString(ua +  "  Android AgronWeb:" + Util.getVersionName(context));
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
