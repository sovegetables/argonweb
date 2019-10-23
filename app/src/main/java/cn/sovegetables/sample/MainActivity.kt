package cn.sovegetables.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.sovegetables.web.CommonWebActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        et_input.setText("https://www.baidu.com")
        btn_go.setOnClickListener {
            CommonWebActivity.start(this, et_input.text.toString())
        }
    }
}
