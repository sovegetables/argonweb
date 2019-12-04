package cn.sovegetables.web;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;

/**
 * 视频全屏播放处理类
 */
public class DefaultVideoFullScreenHandler extends IWebModule.VideoFullScreenModule {


    private static final String TAG = "DefaultVideoFullScreenHandler";

    private View mCustomView;
    private Activity mActivity;
    private CustomViewCallback mCustomViewCallback;
    private FullscreenHolder mFullscreenHolder;
    private WebView mWebView;

    @Override
    public void attachWeb(WebView webView, Activity activity) {
        mWebView = webView;
        mActivity = activity;
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        Log.d(TAG, "onShowCustomView: " + "view: " + view + " CustomViewCallback: " + callback);
        if (mCustomView != null) {
            callback.onCustomViewHidden();
            return;
        }
        mActivity.getWindow().getDecorView();
        mCustomView = view;
        mCustomViewCallback = callback;
        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
        mFullscreenHolder = new FullscreenHolder(mActivity);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mFullscreenHolder.setLayoutParams(layoutParams);
        mFullscreenHolder.addView(view);
        decor.addView(mFullscreenHolder);
        setStatusBarVisibility(false);
        //设置横屏
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public View getVideoLoadingProgressView() {
        FrameLayout frameLayout = new FrameLayout(mActivity);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        return frameLayout;
    }

    public boolean onBackPressed(){
        if (mCustomView != null) {
            onHideCustomView();
            return true;
        }
        return false;
    }

    @Override
    public void onHideCustomView() {
        Log.d(TAG, "onHideCustomView: ");
        if (mCustomView == null) {
            return;
        }
        setStatusBarVisibility(true);
        // 设置竖屏
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
        decor.removeView(mFullscreenHolder);
        mFullscreenHolder = null;
        mCustomView = null;
        mCustomViewCallback.onCustomViewHidden();
        mWebView.setVisibility(View.VISIBLE);
    }

    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        mActivity.getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 全屏容器界面
     */
    private static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }
}
