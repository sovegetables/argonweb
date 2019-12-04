package cn.sovegetables.web;

import android.app.Activity;
import android.content.Intent;

interface WebResulter {
    void onActivityResult(Activity activity, int reqeustCode, int resultCode, Intent data);
}
