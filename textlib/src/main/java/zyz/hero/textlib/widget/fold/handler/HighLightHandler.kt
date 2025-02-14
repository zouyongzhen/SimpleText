package zyz.hero.textlib.widget.fold.handler

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import zyz.hero.textlib.utils.toColor
import zyz.hero.textlib.widget.fold.REGEX_HIGH_LIGHT

class HighLightHandler(regex:String = REGEX_HIGH_LIGHT, val highLightColor:Int = "#357aff".toColor(), val onHighLightClick: ((String) -> Unit)? = null): BaseHandler(regex) {
    override fun handle(textView: TextView, matchText: String?, spannableStringBuilder: SpannableStringBuilder) {
        if (matchText != null) {
            val spanStart = spannableStringBuilder.length
            spannableStringBuilder.append(matchText)
            val spanEnd = spannableStringBuilder.length
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    if (matchText.isNotEmpty()) {
                        onHighLightClick?.invoke(matchText)
                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = highLightColor
                }
            }
            spannableStringBuilder.setSpan(clickableSpan, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}