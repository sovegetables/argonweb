package cn.sovegetables.web

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.view.ContextMenu
import android.view.View
import android.webkit.WebView
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.sovegetables.BaseActivity
import com.sovegetables.SystemBarConfig
import com.sovegetables.titleBuilder
import com.sovegetables.topnavbar.TopBar
import com.sovegetables.topnavbar.TopBarItem
import com.sovegetables.topnavbar.TopBarItemUpdater
import kotlinx.android.synthetic.main.activity_common_web.*
import java.util.*

open class CommonWebActivity : BaseActivity() {

    private var videoFullScreenHandler: DefaultVideoFullScreenHandler? = null
    private var longPressSavePictureHandler: LongPressSavePictureHandler? = null
    private var openFileChooserHandler: OpenFileChooserHandler? = null

    companion object{

        private const val KEY_WEB_CONFIG = "key.CommonWebActivity.config"
        private var sModule: IWebModule = IWebModule.Default()

        const val SHARE_TOP_ITEM_ID = 22;

        fun start(activity: Activity, url: String){
            start(activity, WebConfig(url = url, enableAutoTitle = true))
        }

        fun start(activity: Activity, webConfig: WebConfig){
            val intent = Intent(activity, CommonWebActivity::class.java)
            putWebConfig(intent, webConfig)
            activity.startActivity(intent)
        }

        fun start(fragment: Fragment, url: String){
            start(fragment, WebConfig(url = url, enableAutoTitle = true))
        }

        fun start(fragment: Fragment, webConfig: WebConfig){
            val intent = Intent(fragment.requireContext(), CommonWebActivity::class.java)
            putWebConfig(intent, webConfig)
            fragment.startActivity(intent)
        }

        fun setIWebModule(module: IWebModule){
            sModule = module
        }

        fun getIntent(webConfig: WebConfig) : Intent{
            val intent = Intent()
            putWebConfig(intent, webConfig)
            return intent
        }

        fun putWebConfig(intent: Intent, webConfig: WebConfig) {
            intent.putExtra(KEY_WEB_CONFIG, webConfig)
        }

        fun getWebConfig(activity: CommonWebActivity) : WebConfig{
            return activity.intent?.getParcelableExtra(KEY_WEB_CONFIG) as  WebConfig
        }
    }

    private lateinit var webConfig: WebConfig

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        webConfig = getWebConfig(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_web)

        val updater : TopBarItemUpdater = topBarAction.leftItemUpdater()
        if(webConfig.withCloseIconAndClosePage){
            updater.iconRes(R.drawable.ic_agron_web_close_black)
        }else{
            updater.iconRes(R.drawable.ic_delegate_arrow_back)
        }
        if(!webConfig.enableAutoTitle && !TextUtils.isEmpty(webConfig.title)){
            if(webConfig.isCenterTitle){
                topBarAction.topBarUpdater.title(webConfig.title)
                    .update()
            }else{
                updater.text(webConfig.title)
            }
        }
        updater.update()

        val url = webConfig.url
        val downloadListener = sModule.downloadListenerModule()
        downloadListener?.attachWeb(web, this)
        longPressSavePictureHandler = sModule.longPressSavePictureModule() as LongPressSavePictureHandler?
        longPressSavePictureHandler?.attachWeb(web, this)

        openFileChooserHandler = sModule.openFIleChooserModule()
        openFileChooserHandler?.attachWeb(web, this)

        videoFullScreenHandler = sModule.videoFullScreenModule() as DefaultVideoFullScreenHandler?
        web.addWebChromeClient(videoFullScreenHandler)
        web.addWebChromeClient(object : WebChromeClientAdapter(){
            override fun onReceivedTitle(view: WebView?, title: String?) {
                if(webConfig.enableAutoTitle){
                    if(webConfig.isCenterTitle){
                        topBarAction.topBarUpdater.title(title)
                            .update()
                    }else{
                        topBarAction.leftItemUpdater()
                            .text(title)
                            .update()
                    }
                }
            }
        })
        val defaultWebProgressView = sModule.webProgressView()
        defaultWebProgressView.onCreateProgressVIew(layoutInflater, fl_agron_web_header, agron_web_container)
        web.addWebChromeClient(WebChromeClientCompat(defaultWebProgressView))
        web.addWebViewClient(WebViewClientCompat(defaultWebProgressView))

        val pageListener = object : IWebModule.WebProgressViewModuleAdapter(){
            override fun onPageFinished(view: WebView?, url: String?) {
                topBarAction.findRightItemUpdaterById(SHARE_TOP_ITEM_ID)
                    .visibility(View.VISIBLE)
                    .update()
            }
        }
        web.addWebChromeClient(WebChromeClientCompat(pageListener))
        web.addWebViewClient(WebViewClientCompat(pageListener))

        val listWebChromeClient = sModule.listWebChromeClient()
        listWebChromeClient?.forEach {
            web.addWebChromeClient(it)
        }
        val listWebViewClient = sModule.listWebViewClientAdapter()
        listWebViewClient?.forEach {
            web.addWebViewClient(it)
        }

        Collections.unmodifiableCollection(web.webChromeClientList.webChromeClients)
        Collections.unmodifiableCollection(web.webViewClientList.webViewClient)

        onPrepareWeb(web, webConfig)

        web.webChromeClientList.webChromeClients.forEach {
            it.attachWeb(web, this)
        }
        web.webViewClientList.webViewClient.forEach {
            it.attachWeb(web, this)
        }
        webConfig.realUrl = url
        web.loadUrl(url)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putParcelable(KEY_WEB_CONFIG, webConfig)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webConfig = getWebConfig(this)
    }

    protected open fun onPrepareWeb(web: ArgonWebView?, webConfig: WebConfig) {
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
        web.webChromeClientList.webChromeClients.forEach {
            it.onActivityResult(this, requestCode, resultCode, data)
        }
        web.webViewClientList.webViewClient.forEach {
            it.onActivityResult(this, requestCode, resultCode, data)
        }
        openFileChooserHandler?.onActivityResult(this, requestCode, resultCode, data)
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
        web.webChromeClientList.webChromeClients.forEach {
            it.detachWeb(web, this)
        }
        web.webViewClientList.webViewClient.forEach {
            it.detachWeb(web, this)
        }

        super.onDestroy()
        web.destroy()
    }

    override fun createSystemBarConfig(): SystemBarConfig? {
        val config = webConfig
        val builder = SystemBarConfig.Builder()
        if(config.statusColorInt != null){
            builder.setStatusBarColor(config.statusColorInt!!)
        }
        return builder.build()
    }

    override fun getTopBar(): TopBar {
        val items = arrayListOf<TopBarItem>()
        items.add(TopBarItem.Builder()
            .icon(R.drawable.ic_agron_web_share)
            .listener {
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, webConfig.realUrl?:"")
                    type = "text/*"
                }
                startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.argon_send_to)))
            }
            .visibility(TopBarItem.Visibility.GONE)
            .build(this, SHARE_TOP_ITEM_ID))
        val topBar = titleBuilder("")
            .rights(items)
            .build(this)
        return topBar
    }

    override fun onBackPressed() {
        if(webConfig.withCloseIconAndClosePage){
            super.onBackPressed()
            return
        }

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
