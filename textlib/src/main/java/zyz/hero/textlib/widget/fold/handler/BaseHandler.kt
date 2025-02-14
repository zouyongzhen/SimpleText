package zyz.hero.textlib.widget.fold.handler

import android.text.SpannableStringBuilder
import android.widget.TextView

abstract class BaseHandler(val regex:String) {
    abstract fun handle(textView: TextView, matchText: String?, spannableStringBuilder: SpannableStringBuilder)
}