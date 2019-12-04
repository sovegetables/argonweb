package cn.sovegetables.web

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ContextMenu
import android.view.View
import android.webkit.WebView
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sovegetables.BaseActivity
import com.sovegetables.topnavbar.TopBar
import kotlinx.android.synthetic.main.activity_common_web.*
import java.util.*

open class CommonWebActivity : BaseActivity() {

    private var videoFullScreenHandler: DefaultVideoFullScreenHandler? = null
    private var longPressSavePictureHandler: LongPressSavePictureHandler? = null

    companion object{

        private const val KEY_URL = "key.CommonWebActivity.url"
        private var sModule: IWebModule = IWebModule.Default()

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

        fun setIWebModule(module: IWebModule){
            sModule = module
        }

        init {
            setLeftTopIcon(R.drawable.ic_agron_web_close_black)
        }

    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_web)
        val url = intent!!.extras!![KEY_URL] as String

        val downloadListener = sModule.downloadListenerModule()
        downloadListener?.attachWeb(web, this)
        longPressSavePictureHandler = sModule.longPressSavePictureModule() as LongPressSavePictureHandler?
        longPressSavePictureHandler?.attachWeb(web, this)

        videoFullScreenHandler = sModule.videoFullScreenModule() as DefaultVideoFullScreenHandler?
        web.addWebChromeClient(videoFullScreenHandler)
        web.addWebChromeClient(object : WebChromeClientAdapter(){
            override fun onReceivedTitle(view: WebView?, title: String?) {
                topBarAction.leftItemUpdater()
                    .icon(ContextCompat.getDrawable(view!!.context, R.drawable.ic_agron_web_close_black))
                    .text(title)
                    .textColorRes(android.R.color.black)
                    .update()
            }

            override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                super.onReceivedIcon(view, icon)
            }
        })
        val defaultWebProgressView = DefaultWebProgressView()
        defaultWebProgressView.onCreateProgressVIew(layoutInflater, fl_agron_web_header, agron_web_container)
        web.addWebChromeClient(WebChromeClientCompat(defaultWebProgressView))
        web.addWebViewClient(WebViewClientCompat(defaultWebProgressView))

        val listWebChromeClient = sModule.listWebChromeClient()
        listWebChromeClient?.forEach {
            web.addWebChromeClient(it)
        }
        val listWebViewClient = sModule.listWebViewClientAdapter()
        listWebViewClient?.forEach {
            web.addWebViewClient(it)
        }

        Collections.unmodifiableCollection(web.webChromeClientList.mWebChromeClients)
        Collections.unmodifiableCollection(web.webViewClientList.mWebViewClient)
        web.webChromeClientList.mWebChromeClients.forEach {
            it.attachWeb(web, this)
        }
        web.webViewClientList.mWebViewClient.forEach {
            it.attachWeb(web, this)
        }
        web.loadUrl(url)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        longPressSavePictureHandler?.onCreateContextMenu(menu, v, menuInfo)
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        web.onPause()
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        web.onResume()
    }

    @CallSuper
    override fun onDestroy() {
        web.webChromeClientList.mWebChromeClients.forEach {
            it.detachWeb(web, this)
        }
        web.webViewClientList.mWebViewClient.forEach {
            it.detachWeb(web, this)
        }

        super.onDestroy()
        web.destroy()
    }

    override fun getTopBar(): TopBar {
        return title("")
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
