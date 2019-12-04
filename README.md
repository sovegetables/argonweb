### Dependencies
```gradle
    maven { 
        url "https://jitpack.io" 
    }
    
    implementation 'com.github.sovegetables:ArgonWeb:0.1.0'
```
### Usage
- One line of code
```kotlin
    CommonWebActivity.start(this, url)
```
- Custom
```xml
<cn.sovegetables.web.ArgonWebView
    android:id="@+id/web"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    />
```

```kotlin
webview.addWebChromeClient(webchromeclient1）
webview.addWebChromeClient(webchromeclient2）

webview.addWebViewClient(webviewclient1)
webview.addWebViewClient(webviewclient2)

val webCompatCallback = object: WebCompatCallback(){
    .... //you can receive WebViewClient's callback and WebChromeClient's callback
}
web.addWebChromeClient(WebChromeClientCompat(webCompatCallback))
web.addWebViewClient(WebViewClientCompat(webCompatCallback))

//support Play video with full screen, and you can custom a class that extends DefaultVideoFullScreenHandler or VideoFullScreenModule.
val fullScreenHandler = DefaultVideoFullScreenHandler()
fullScreenHandler.attachWeb(webview, activity)
web.addWebChromeClient(fullScreenHandler)
```

### Support
- Long press to save picture
- Add multi WebChromeClient
- Add multi WebViewClient
- Play video with full screen
- WebView loading progress bar
- WebCompatCallback compat WebViewClient and WebChromeClient