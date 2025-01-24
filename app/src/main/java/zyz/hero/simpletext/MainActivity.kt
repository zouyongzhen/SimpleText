package zyz.hero.simpletext

import android.icu.text.CaseMap.Fold
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import zyz.hero.simple_text.widget.fold.FoldText
import zyz.hero.simple_text.widget.fold.handler.HighLightHandler
import zyz.hero.simple_text.widget.fold.handler.QuoteHandler

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editText = findViewById<EditText>(R.id.et)
        val foldText = findViewById<FoldText>(R.id.foldText)
        foldText.setHandler(HighLightHandler(),QuoteHandler(){
            showLog(it)
        }).setOnButtonClickListener {
            showLog("onButtonClick")
        }.setOnLayoutClickListener {
            showLog("onLayoutClick")
        }
        editText.addTextChangedListener {
            foldText.setText(it?.toString())
        }

    }
    fun showLog(msg:String){
        Log.e("showLog", "$msg")
    }
}