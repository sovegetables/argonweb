package cn.sovegetables.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import cn.sovegetables.web.ArgonWebView
import cn.sovegetables.web.CommonWebActivity
import cn.sovegetables.web.WebConfig
import com.sovegetables.topnavbar.TopBar

class MyWebActivity : CommonWebActivity() {

    companion object{
        private const val KEY_URL = "key.CommonWebActivity.url"

        fun start(activity: Activity, url: String){
            val intent = Intent(activity, MyWebActivity::class.java)
            intent.putExtra(KEY_URL, url)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_my_web)

        val webView = findViewById<ArgonWebView>(R.id.web)
        Handler().postDelayed({
            webView.scrollTo(0, 2000)
        }, 1000)


        /*Handler().postDelayed({
            setMouseClick(800f, 1500f)
        }, 1500)*/

//        Handler().postDelayed({
//            setMouseClick(100f, 1100f)
//        }, 1500)
//
//        Handler().postDelayed({
//            finish()
//        }, 2000)
    }

    private fun setMouseClick(x: Float, y: Float){
        val evenDownt = MotionEvent.obtain(System.currentTimeMillis(),
        System.currentTimeMillis() + 100, MotionEvent.ACTION_DOWN, x, y, 0);
        dispatchTouchEvent(evenDownt);
        val eventUp = MotionEvent.obtain(System.currentTimeMillis(),
        System.currentTimeMillis() + 100, MotionEvent.ACTION_UP, x, y, 0);
        dispatchTouchEvent(eventUp);
        evenDownt.recycle();
        eventUp.recycle();
    }

    override fun onPrepareWeb(web: ArgonWebView?, webConfig: WebConfig) {
        super.onPrepareWeb(web, webConfig)
    }

    override fun getTopBar(): TopBar {
        return TopBar.NO_ACTION_BAR
    }
}
