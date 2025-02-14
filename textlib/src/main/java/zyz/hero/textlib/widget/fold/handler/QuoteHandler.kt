package zyz.hero.textlib.widget.fold.handler

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import zyz.hero.textlib.utils.TopAlignSpan
import zyz.hero.textlib.utils.toColor
import zyz.hero.textlib.widget.fold.REGEX_QUOTE

/**
 * quoteSizeFraction 引用文字大小占正文大小的百分比
 *
 */
class QuoteHandler(regex: String = REGEX_QUOTE, val quoteSizeFraction:Float = 0.8f, val quoteColor:Int = "#357aff".toColor(), val onQuoteClick: ((String) -> Unit)? = null): BaseHandler(regex) {
    override fun handle(textView: TextView, matchText: String?, spannableStringBuilder: SpannableStringBuilder) {
        if (matchText != null) {
            // 捕获目标字符串
            val spanStart = spannableStringBuilder.length
            spannableStringBuilder.append(matchText)
            val spanEnd = spannableStringBuilder.length
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    if (matchText.isNotEmpty()) {
                        onQuoteClick?.invoke(matchText)
                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = quoteColor
                }
            }
            spannableStringBuilder.setSpan(clickableSpan, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableStringBuilder.setSpan(
                TopAlignSpan(textView.textSize * quoteSizeFraction, 0.3f), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}