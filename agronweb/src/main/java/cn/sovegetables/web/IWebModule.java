package cn.sovegetables.web;

import android.graphics.Bitmap;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

public interface IWebModule {
    VideoFullScreenModule videoFullScreenModule();
    LongPressSavePictureModule longPressSavePictureModule();
    DownloadListenerModule downloadListenerModule();
    OpenFileChooserHandler openFIleChooserModule();
    WebProgressViewModule webProgressView();
    List<WebChromeClientAdapter> listWebChromeClient();
    List<WebViewClientAdapter> listWebViewClientAdapter();

    class Default implements IWebModule{

        @Override
        public VideoFullScreenModule videoFullScreenModule() {
            return new DefaultVideoFullScreenHandler();
        }

        @Override
        public LongPressSavePictureModule longPressSavePictureModule() {
            return new LongPressSavePictureHandler();
        }

        @Override
        public DownloadListenerModule downloadListenerModule() {
            return new DefaultDownloadListener();
        }

        @Override
        public OpenFileChooserHandler openFIleChooserModule() {
            return new DefaultOpenFileChooserHandler();
        }

        @Override
        public WebProgressViewModule webProgressView() {
            return new DefaultWebProgressView();
        }

        @Override
        public List<WebChromeClientAdapter> listWebChromeClient() {
            return null;
        }

        @Override
        public List<WebViewClientAdapter> listWebViewClientAdapter() {
            return null;
        }
    }

    abstract class WebProgressViewModule extends WebCompatCallback implements WebAttach{
        public abstract void onCreateProgressVIew(LayoutInflater inflater, FrameLayout webHeaderContainer, ConstraintLayout container);
        public abstract void onProgressChanged(WebView view, int progress);
        public abstract void onPageStarted(WebView view, String url, Bitmap favicon);
        public abstract void onPageFinished(WebView view, String url);
    }

    interface DownloadListenerModule extends DownloadListener, WebAttach {
    }

    interface LongPressSavePictureModule extends WebAttach {
        void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo);
    }

    abstract class VideoFullScreenModule extends WebChromeClientAdapter implements WebAttach {

        @Override
        public abstract void onShowCustomView(View view, CustomViewCallback callback);

        @Override
        public abstract View getVideoLoadingProgressView();

        public abstract boolean onBackPressed();

        @Override
        public abstract void onHideCustomView();
    }
}
