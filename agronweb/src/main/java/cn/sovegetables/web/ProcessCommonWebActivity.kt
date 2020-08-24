package cn.sovegetables.web

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment

open class ProcessCommonWebActivity : CommonWebActivity() {

    companion object{

        fun start(activity: Activity, url: String){
            start(activity, WebConfig(url = url, enableAutoTitle = true))
        }

        fun start(activity: Activity, webConfig: WebConfig){
            val intent = Intent(activity, ProcessCommonWebActivity::class.java)
            putWebConfig(intent,  webConfig)
            activity.startActivity(intent)
        }

        fun start(fragment: Fragment, url: String){
            start(fragment, WebConfig(url = url, enableAutoTitle = true))
        }

        fun start(fragment: Fragment, webConfig: WebConfig){
            val intent = Intent(fragment.requireContext(), ProcessCommonWebActivity::class.java)
            putWebConfig(intent,  webConfig)
            fragment.startActivity(intent)
        }

        fun setIWebModule(module: IWebModule){
            CommonWebActivity.setIWebModule(module)
        }
    }
}
