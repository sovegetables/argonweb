package cn.sovegetables.web

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_common_web.*
import java.lang.IllegalArgumentException

open class CommonWebActivity : AppCompatActivity() {

    private var videoFullScreenHandler: DefaultVideoFullScreenHandler? = null
    private var longPressSavePictureHandler: LongPressSavePictureHandler? = null

    companion object{

        private const val KEY_URL = "key.CommonWebActivity.url"

        fun start(activity: Activity, url: String){
            val intent = Intent(activity, CommonWebActivity::class.java)
            intent.putExtra(KEY_URL, url)
            activity.startActivity(intent)
        }

        fun start(fragment: Fragment, url: String){
            val intent = Intent(fragment.requireContext(), CommonWebActivity::class.java)
            intent.putExtra(KEY_URL, url)
            fragment.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_web)
        val url = intent!!.extras!![KEY_URL] as String
        videoFullScreenHandler = DefaultVideoFullScreenHandler(web, this);
        web.addWebChromeClient(videoFullScreenHandler)
        web.addWebViewClient(object : WebViewClientAdapter(){

        })
        web.loadUrl(url)
        longPressSavePictureHandler = LongPressSavePictureHandler(web)
        longPressSavePictureHandler?.onCreate(savedInstanceState, this)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        longPressSavePictureHandler?.onCreateContextMenu(menu, v, menuInfo)
    }


    override fun onBackPressed() {
        /* 回退键 事件处理 优先级:视频播放全屏-网页回退-关闭页面 */
        if (videoFullScreenHandler != null && videoFullScreenHandler!!.onBackPressed()) {
            return
        }
        if(web.canGoBack()){
            web.goBack()
        }else{
            super.onBackPressed()
        }
    }
}
