package cn.sovegetables.web;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public abstract class OpenFileChooserHandler implements WebAttach, WebResult {

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessages;

    /**
     * For android 4.1+
     * @param uploadMsg ValueCallback<Uri>
     * @param acceptType String
     * @param capture String
     */
    public final void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
        if (mUploadMessage != null) {
            return;
        }
        mUploadMessage = uploadMsg;
        onChildOpenFileChooser(acceptType, capture);
    }

    /**
     * For android 4.1+
     * @param acceptType String
     * @param capture String
     */
    protected abstract void onChildOpenFileChooser(String acceptType, String capture);

    /**
     * For android 5.0+
     * @param webView WebView
     * @param filePathCallback ValueCallback<Uri[]>
     * @param fileChooserParams WebChromeClient.FileChooserParams
     */
    public final void onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams){
        if (mUploadMessages != null) {
            return;
        }
        mUploadMessages = filePathCallback;
        onChildShowFileChooser(webView, fileChooserParams);
    }

    /**
     * For android 5.0+
     * @param webView WebView
     * @param fileChooserParams WebChromeClient.FileChooserParams
     */
    protected abstract void onChildShowFileChooser(WebView webView, WebChromeClient.FileChooserParams fileChooserParams);

    @Override
    public final void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK) {
            onChildActivityResult(requestCode, resultCode, data);
        }else {
            cancelUpload();
        }
    }

    protected abstract void onChildActivityResult(int requestCode, int resultCode, Intent data);

    public abstract boolean onBackPressed();

    protected final void onUploadReceiveValue(Uri result) {
        if (null == mUploadMessage && null == mUploadMessages) {
            return;
        }
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else {
            if (result == null) {
                mUploadMessages.onReceiveValue(null);
            } else {
                mUploadMessages.onReceiveValue(new Uri[]{result});
            }
            mUploadMessages = null;
        }
    }

    protected final void cancelUpload() {
        if (null != mUploadMessage) {
            mUploadMessage.onReceiveValue(null);
            mUploadMessage = null;
        }
        if (null != mUploadMessages) {
            mUploadMessages.onReceiveValue(null);
            mUploadMessages = null;
        }
    }

}
