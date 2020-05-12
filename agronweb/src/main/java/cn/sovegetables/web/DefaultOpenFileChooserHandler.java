package cn.sovegetables.web;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DefaultOpenFileChooserHandler extends OpenFileChooserHandler{

    private static final String TAG = "PopupOpenFileChooser";
    private static final int REQUEST_CODE_CHOOSE_FILE = 0x400;
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 0x500;

    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private PopupWindow mChooseFilePopup;

    private Activity mActivity;
    private WebView mWebView;
    private View mPopupView;
    private boolean mHasStart;
    private static final String PHOTO_TEMP_PATH_NAME = Environment.getExternalStorageDirectory() + "/DCIM/Camera";
    private static final File PHOTO_DIR = new File(PHOTO_TEMP_PATH_NAME);
    private File mCurrentPhotoFile;
    private WebChromeClient.FileChooserParams mFileChooserParams;

    @Override
    public void attachWeb(WebView webView, Activity activity) {
        mActivity = activity;
        mWebView = webView;
    }

    @Override
    protected void onChildOpenFileChooser(String acceptType, String capture) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        mActivity.startActivityForResult(Intent.createChooser(i, "File Chooser"), REQUEST_CODE_CHOOSE_FILE);
    }

    @Override
    protected void onChildShowFileChooser(WebView webView, final WebChromeClient.FileChooserParams fileChooserParams) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                showPopupWindow(fileChooserParams);
            }
        });
    }

    @Override
    protected void onChildActivityResult(int requestCode, int resultCode, Intent data) {
        Uri result;
        switch (requestCode){
            case REQUEST_CODE_CHOOSE_FILE:
                result = data == null ? null : data.getData();
                onUploadReceiveValue(result);
                break;
            case REQUEST_CODE_CHOOSE_PHOTO:
                result = mCurrentPhotoFile == null ? null :
                        Uri.fromFile(mCurrentPhotoFile);
                onUploadReceiveValue(result);
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mChooseFilePopup != null && mChooseFilePopup.isShowing()) {
            cancelUpload();
            mChooseFilePopup.dismiss();
            return true;
        }
        return false;
    }

    private void showPopupWindow(WebChromeClient.FileChooserParams fileChooserParams) {
        mFileChooserParams = fileChooserParams;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Log.i(TAG, mFileChooserParams.getAcceptTypes()[0]);
            } catch (Exception ignored) {
            }
        }
        if (mChooseFilePopup == null) {
            mPopupView = LayoutInflater.from(mActivity).inflate(R.layout.popup_select_file, null);
            mChooseFilePopup = new PopupWindow(mPopupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mChooseFilePopup.setTouchable(true);
            //点击外部消失
            mChooseFilePopup.setBackgroundDrawable(new BitmapDrawable());
            mChooseFilePopup.setOutsideTouchable(true);
            //点击外部空间 窗口消失并获得焦点， 外部控件不响应
            mChooseFilePopup.setFocusable(true);
            mChooseFilePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(mActivity,1.0f);
                    resetBackgroundAlpha(mActivity);
                    if (!mHasStart) {
                        cancelUpload();
                    }
                    mHasStart = false;
                    mWebView.setClickable(true);
                }
            });
            mPopupView.findViewById(R.id.camera_lly).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Util.isExternalStorageAvailable()) {
                        Toast.makeText(mActivity, mActivity.getString(R.string.SD_card_useless), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {

                        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            /*mActivity.requestPermissions(new BaseActivity.OnPermissionResultListener() {
                                @Override
                                public void onAllGranted() {
                                    super.onAllGranted();
                                    //适配小米和魅族手机
                                    if (!FileUtil.fileIsExists(PHOTO_TEMP_PATH_NAME)) {
                                        FileUtil.createDir(PHOTO_TEMP_PATH_NAME);
                                    }
                                    //7.0的FileUriExposedException
                                    if (Build.VERSION.SDK_INT >= 24) {
                                        mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName(getMIMEType(mFileChooserParams)));
                                        Uri uri = FileProvider.getUriForFile(mActivity, mActivity.getString(R.string.s_argon_web_file_provider), mCurrentPhotoFile);
                                        final Intent intent = getTakePickIntent(uri, getMIMEType(mFileChooserParams));
                                        //适配小米，需要循环拿权限
                                        List<ResolveInfo> resInfoList = mActivity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                        for (ResolveInfo resolveInfo : resInfoList) {
                                            String packageName = resolveInfo.activityInfo.packageName;
                                            mActivity.grantUriPermission(packageName, uri,
                                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                        }
                                        mActivity.startActivityForResult(intent, REQUEST_CODE_CHOOSE_PHOTO);
                                    } else {
                                        mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName(getMIMEType(mFileChooserParams)));
                                        final Intent intent = getTakePickIntent(Uri.fromFile(mCurrentPhotoFile), getMIMEType(mFileChooserParams));
                                        mActivity.startActivityForResult(intent, REQUEST_CODE_CHOOSE_PHOTO);
                                    }
                                    mHasStart = true;
                                    mChooseFilePopup.dismiss();
                                }

                                @Override
                                public void onAnyDenied(String[] permissions) {
                                    super.onAnyDenied(permissions);
                                }

                                @Override
                                public void onAlwaysDenied(String[] permissions) {
                                    String message = mActivity.getString(R.string.need_camera_permission);
                                    PermissionDeniedDialog permissionDeniedDialog = new PermissionDeniedDialog(mActivity, message);
                                    permissionDeniedDialog.show();
                                }
                            }, Manifest.permission.CAMERA);*/
                        } else {
                            if (!Util.fileIsExists(PHOTO_TEMP_PATH_NAME)) {
                                Util.createDir(PHOTO_TEMP_PATH_NAME);
                            }
                            //7.0的FileUriExposedException
                            if (Build.VERSION.SDK_INT >= 24) {
                                mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName(getMIMEType(mFileChooserParams)));
                                Uri uri = FileProvider.getUriForFile(mActivity, mActivity.getString(R.string.s_argon_web_file_provider), mCurrentPhotoFile);
                                final Intent intent = getTakePickIntent(uri, getMIMEType(mFileChooserParams));
                                //适配小米，需要循环拿权限
                                List<ResolveInfo> resInfoList = mActivity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                for (ResolveInfo resolveInfo : resInfoList) {
                                    String packageName = resolveInfo.activityInfo.packageName;
                                    mActivity.grantUriPermission(packageName, uri,
                                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                }
                                mActivity.startActivityForResult(intent, REQUEST_CODE_CHOOSE_PHOTO);
                            } else {
                                mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName(getMIMEType(mFileChooserParams)));
                                final Intent intent = getTakePickIntent(Uri.fromFile(mCurrentPhotoFile), getMIMEType(mFileChooserParams));
                                mActivity.startActivityForResult(intent, REQUEST_CODE_CHOOSE_PHOTO);
                            }
                            mHasStart = true;
                            mChooseFilePopup.dismiss();
                        }

                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(mActivity, "not found photo", Toast.LENGTH_LONG).show();
                    }

                }
            });
            mPopupView.findViewById(R.id.pic_lly).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    final String type = getMIMEType(mFileChooserParams);
                    if(!TextUtils.isEmpty(type) && type.contains("video")){
                        intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType(type);
                    }else {
                        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    }
                    mActivity.startActivityForResult(intent, REQUEST_CODE_CHOOSE_FILE);
                    mHasStart = true;
                    Log.d(TAG, "ACTION_PICK");
                    mChooseFilePopup.dismiss();
                }
            });
            mPopupView.findViewById(R.id.file_lly).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    mActivity.startActivityForResult(intent, REQUEST_CODE_CHOOSE_FILE);
                    mHasStart = true;
                    Log.d(TAG, "ACTION_GET_CONTENT");
                    mChooseFilePopup.dismiss();
                }
            });
        }
        if (!mChooseFilePopup.isShowing()) {
            backgroundAlpha(mActivity, 0.4f);
            mWebView.setClickable(false);
            mChooseFilePopup.showAtLocation(mPopupView, Gravity.BOTTOM, 0, 0);
        }
    }

    private static String getMIMEType(WebChromeClient.FileChooserParams fileChooserParams){
        String type = "";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            String[] acceptTypes = fileChooserParams.getAcceptTypes();
            if(acceptTypes != null && acceptTypes.length > 0){
                type = acceptTypes[0];
            }
        }
        return type;
    }

    private static void backgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        activity.getWindow().setAttributes(lp);
    }

    private static void resetBackgroundAlpha(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private static Intent getTakePickIntent(Uri uri, String type) {
        String action = MediaStore.ACTION_IMAGE_CAPTURE;
        if(!TextUtils.isEmpty(type) && type.contains("video")){
            action = MediaStore.ACTION_VIDEO_CAPTURE;
        }
        Intent intent = new Intent(action, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("path", uri);
        intent.putExtra("outputX", 1024);
        intent.putExtra("outputY", 1024);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        return intent;
    }

    private String getPhotoFileName(String type) {
        String suffix = ".jpg";
        if(!TextUtils.isEmpty(type) && type.contains("video")){
            suffix = ".mp4";
        }
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss", Locale.getDefault());
        return dateFormat.format(date) + suffix;
    }

}
