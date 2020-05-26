package cn.sovegetables.sample

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import cn.sovegetables.web.CommonWebActivity
import cn.sovegetables.web.WebConfig
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        et_input.setText("https://www.baidu.com")
        btn_go.setOnClickListener {
            CommonWebActivity.start(this, WebConfig(url = et_input.text.toString(), statusColorInt = Color.BLUE,
                enableAutoTitle = true,
                withCloseIconAndClosePage = false) )
        }

//        for (i in 1..1000){
//            handler.postDelayed({
//                btn_go.performClick()
//            }, 8000)
//        }
    }
}
