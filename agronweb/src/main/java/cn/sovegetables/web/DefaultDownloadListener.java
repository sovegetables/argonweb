package cn.sovegetables.web;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.DownloadListener;

public class DefaultDownloadListener implements DownloadListener {

    private static final String TAG = "DefaultDownloadListener";

    private Context mContext;

    DefaultDownloadListener(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        // 使用系统浏览器进行下载
        try{
            Intent i = new Intent(Intent.ACTION_VIEW);
            Uri downloadUri = Uri.parse(url);
            Log.i(TAG, downloadUri == null? "Null": downloadUri.toString());
            i.setData(downloadUri);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
        }catch (ActivityNotFoundException a){
            Log.e(TAG, a.getMessage());
        }
    }
}
