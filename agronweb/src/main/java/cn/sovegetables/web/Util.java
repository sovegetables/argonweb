package cn.sovegetables.web;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class Util {

    static String getVersionName(Context context) {
        return getVersionName(context, context.getPackageName());
    }

    private static String getVersionName(Context context, String packageName) {
        String versionName = "1.0.0";
        try {
            versionName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (Exception var4) {
            var4.printStackTrace();
        }
        return versionName;
    }

    static File createDir(String path) throws SecurityException {
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir;
    }

    static boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    static void saveBitmapToFile(Context context, Bitmap bitmap, String _file)
            throws IOException {
        if(null == bitmap || null == _file) {
            return;
        }
        BufferedOutputStream os = null;
        try {
            File file = new File(_file);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            Intent intent_scan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent_scan.setData(uri);
            context.sendBroadcast(intent_scan);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static void saveBitmapToFile(Context context, byte[] bytes, String filePath)
            throws IOException {
        if(null == bytes || null == filePath) {
            return;
        }
        BufferedOutputStream os = null;
        try {
            File file = new File(filePath);
            File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                boolean mkdirs = parentFile.mkdirs();
            }
            boolean b = file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));
            os.write(bytes, 0, bytes.length);
            Intent intent_scan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent_scan.setData(uri);
            context.sendBroadcast(intent_scan);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
