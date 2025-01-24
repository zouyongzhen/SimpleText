package zyz.hero.simple_text.widget.fold

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import zyz.hero.simple_text.R
import java.util.regex.Matcher

/**
 * 支持折叠、数字引用、高亮
 */

class FoldTextWithQuoteAndHighLight(context: Context, attrs: AttributeSet? = null) : QuoteText(context, attrs) {
    companion object {
        const val REGEX_HIGH_LIGHT = "<hl=(.*?)>"
    }

    var highLightColor = Color.parseColor("#357aff")

    init {
        regexList.add(REGEX_HIGH_LIGHT)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FoldTextWithQuoteAndHighLight)
        highLightColor = typedArray.getColor(R.styleable.FoldTextWithQuoteAndHighLight_highLightColor, Color.parseColor("#357aff"))
        typedArray.recycle()
    }

    override fun handleMatcher(matcher: Matcher, spannableStringBuilder: SpannableStringBuilder) {
        super.handleMatcher(matcher, spannableStringBuilder)
        val index = regexList.indexOf(REGEX_HIGH_LIGHT)
        if (index == -1) {
            return
        }
        val matchText = matcher.group(index + 1)
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
