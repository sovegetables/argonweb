package cn.sovegetables.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * 长按WebView里图片弹窗，让用户保存图片
 */
@RestrictTo(LIBRARY_GROUP)
public class LongPressSavePictureHandler {

    private WebView mWebView;

    public LongPressSavePictureHandler(WebView webView) {
        mWebView = webView;
    }

    public void onCreate(Bundle bundle, Activity activity){
        activity.registerForContextMenu(mWebView);
    }

    private static class OnInternalMenuItemClickListener implements MenuItem.OnMenuItemClickListener{

        private WebView.HitTestResult webViewHitTestResult;
        private Context mContext;
        private final Handler mUIHandler = new Handler(Looper.getMainLooper());
        private final ExecutorService mExecutor = Executors.newScheduledThreadPool(3);

        OnInternalMenuItemClickListener(WebView.HitTestResult webViewHitTestResult, Context context) {
            this.webViewHitTestResult = webViewHitTestResult;
            mContext = context.getApplicationContext();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            final String downloadImageUrl = webViewHitTestResult.getExtra();
            if (!TextUtils.isEmpty(downloadImageUrl) && downloadImageUrl.startsWith("http")) {
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        boolean status = false;
                        OkHttpClient client = new OkHttpClient.Builder()
                                .addNetworkInterceptor(new Interceptor() {
                                    @NotNull
                                    @Override
                                    public Response intercept(@NotNull Chain chain) throws IOException {
                                        Request request = chain.request();
                                        Log.d("onMenuItemClick", "intercept: " + request);
                                        return chain.proceed(request);
                                    }
                                })
                                .build();
                        Request request = new Request.Builder()
                                .url(downloadImageUrl)
                                .cacheControl(CacheControl.FORCE_NETWORK)
                                .addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                                .build();
                        try{
                            Response response = client.newCall(request).execute();
                            status = response.isSuccessful();
                            byte[] bytes = response.body().bytes();
                            Log.d("onMenuItemClick", "run: " + new String(bytes, "utf-8"));
                            Log.d("onMenuItemClick", "run: " + response.message());
                            Log.d("onMenuItemClick", "run: " + response.headers());
                            if(status){
                                Util.saveBitmapToFile(mContext, bytes,
                                        Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + "/" + System.currentTimeMillis() + ".jpg");
                            }
                        }catch (Exception e){
                        }
                        onToastResult(status);
                    }
                });
            } else if (isBase64ImageUri(downloadImageUrl)) {
                saveImageForDataImageUri(downloadImageUrl);
            } else {
                Toast.makeText(mContext, R.string.s_save_img_failed, Toast.LENGTH_LONG).show();
            }
            return false;
        }

        private void saveImageForDataImageUri(final String imgUrl){
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    boolean flag = false;
                    String imgSuffix = "";
                    if(imgUrl.startsWith("data:image/")){
                        String[] split = imgUrl.split(";");
                        imgSuffix = split[0].replace("image/", "");
                        if(split[1].startsWith("base64,")){
                            split = split[1].split(",");
                            byte[] bytes = Base64.decode(split[1].getBytes(), Base64.DEFAULT);
                            String fileUrl = Environment.getExternalStorageDirectory().getAbsolutePath() +
                                    "/"+ System.currentTimeMillis() +"." + imgSuffix;
                            BufferedOutputStream os = null;
                            try {
                                File file = new File(fileUrl);
                                File parentFile = file.getParentFile();
                                if (parentFile != null && !parentFile.exists()) {
                                    parentFile.mkdirs();
                                }
                                file.createNewFile();
                                os = new BufferedOutputStream(new FileOutputStream(file));
                                os.write(bytes);
                                os.flush();
                                Intent intent_scan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Uri uri = Uri.fromFile(file);
                                intent_scan.setData(uri);
                                mContext.sendBroadcast(intent_scan);
                                flag = true;
                            }catch (Exception ignored){
                            }finally {
                                if (os != null) {
                                    try {
                                        os.flush();
                                        os.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                    onToastResult(flag);
                }
            });
        }

        private void onToastResult(boolean flag) {
            final boolean localFlag = flag;
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, localFlag? R.string.s_save_img_success : R.string.s_save_img_failed,
                            Toast.LENGTH_LONG)
                            .show();
                }
            });
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        final WebView.HitTestResult webViewHitTestResult = mWebView.getHitTestResult();
        if (webViewHitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                webViewHitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            menu.add(0, 1, 0, R.string.s_save_imge)
                    .setOnMenuItemClickListener(new OnInternalMenuItemClickListener(webViewHitTestResult, mWebView.getContext()));
        }
    }
    
    private static boolean isBase64ImageUri(String url){
        return url != null && url.startsWith("data:image/");
    }


}
