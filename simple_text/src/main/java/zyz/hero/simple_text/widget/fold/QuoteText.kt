package zyz.hero.simple_text.widget.fold

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import zyz.hero.simple_text.R
import zyz.hero.simple_text.utils.TopAlignSpan
import java.util.regex.Matcher

open class QuoteText(context: Context, attrs: AttributeSet? = null) : FoldText(context, attrs) {
    companion object{
        //匹配<quote=[1]>，并将其转为中括号加数字添加span // 如："春种一粒粟<quote=[1]>秋收万颗子<quote=[2]>"  =>  "春种一粒粟[1]秋收万颗子[2]"
        const val REGEX_QUOTE = "<quote=(\\[\\d+\\])>"
    }
    //引用的点击事件
    private var onQuoteClick: ((String) -> Unit)? = null
    //引用文本颜色
    private var quoteColor = Color.parseColor("#666666")
    //引用文字大小占正文大小的百分比
    private var quoteSizeFraction = 0.8f
    init {
        regexList.add(REGEX_QUOTE)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.QuoteText)
        quoteColor = typedArray.getColor(R.styleable.QuoteText_quoteColor, Color.parseColor("#666666"))
        quoteSizeFraction = typedArray.getFloat(R.styleable.QuoteText_quoteSizeFraction, 0.8f)
        typedArray.recycle()
    }

    override fun handleMatcher(matcher: Matcher, spannableStringBuilder: SpannableStringBuilder) {
        super.handleMatcher(matcher, spannableStringBuilder)
        val index = regexList.indexOf(REGEX_QUOTE)
        if (index == -1) {
            return
        }
        val matchText = matcher.group(index + 1)
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

    fun setOnQuoteClickListener(onQuoteClick: (String) -> Unit) {
        this.onQuoteClick = onQuoteClick
    }
}