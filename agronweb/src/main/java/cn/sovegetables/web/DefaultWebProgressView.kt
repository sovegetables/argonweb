package cn.sovegetables.web

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout

class DefaultWebProgressView: IWebModule.WebProgressViewModule(){

    private lateinit var progressBar: ProgressBar

    fun onCreateProgressVIew(inflater: LayoutInflater, webHeaderContainer: FrameLayout , container: ConstraintLayout){
        val progressView = inflater.inflate(R.layout.agron_web_progress_view, webHeaderContainer, false)
        progressBar = progressView.findViewById(R.id.agron_progress_bar)
        container.addView(progressView)
        progressBar.visibility = View.GONE
    }

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        if(newProgress >= 0){
            progressBar.visibility = View.VISIBLE
            progressBar.progress = newProgress
        } else if(newProgress == 100){
            progressBar.progress = 0
            progressBar.visibility = View.GONE
        }
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        progressBar.visibility = View.VISIBLE
    }



    override fun onPageFinished(view: WebView?, url: String?) {
        progressBar.visibility = View.GONE
    }
}