package zyz.hero.simple_text.widget.fold.handler

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import zyz.hero.simple_text.utils.toColor
import zyz.hero.simple_text.widget.fold.REGEX_HIGH_LIGHT

class HighLightHandler(regex:String = REGEX_HIGH_LIGHT,val highLightColor:Int = "#357aff".toColor()): BaseHandler(regex) {
    override fun handle(textView: TextView, matchText: String?, spannableStringBuilder: SpannableStringBuilder) {
        if (matchText != null) {
            val spanStart = spannableStringBuilder.length
            spannableStringBuilder.append(matchText)
            spannableStringBuilder.setSpan(
                ForegroundColorSpan(highLightColor),
                spanStart,
                spanStart + matchText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}