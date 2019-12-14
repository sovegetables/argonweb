package cn.sovegetables.web;

import android.app.Activity;
import android.content.Intent;

interface WebResult {
    void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);
}
