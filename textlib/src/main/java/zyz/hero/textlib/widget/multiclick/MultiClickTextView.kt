package zyz.hero.textlib.widget.multiclick

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import zyz.hero.textlib.utils.TextViewTouchListener
import zyz.hero.textlib.utils.charSequencePrefixConcat

/**
 * 处理一个文本展示列表数据，列表项需要响应点击
 * 多文本点击：
 * 例1："主演刘亦菲/成龙出席发布会"，其中"刘亦菲"和"成龙"需要响应点击事件
 */
class MultiClickTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextView(context, attrs) {
    data class TextData(
        //前缀
        var prefix: CharSequence? = SpannableStringBuilder(),
        //中缀，多次使用 不能使用同一个对象。
        var infix: () -> CharSequence? = { SpannableStringBuilder() },
        //后缀
        var suffix: CharSequence? = SpannableStringBuilder(),
        //数据集
        var dataList: List<String?>?,
        var clickTextColor: Int = -1,
        var onTextClick: ((clickText: String?, index: Int) -> Unit)? = null
    )

    fun setTextData(textData: TextData?) {
        if (textData?.dataList.isNullOrEmpty()) {
            return
        }
        textData?.let {
            val spannable = getTextSpan(it).append(it.suffix ?: "").charSequencePrefixConcat(it.prefix)
            setOnTouchListener(TextViewTouchListener(spannable))
            text = spannable
        }
    }

    private fun getTextSpan(textData: TextData): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder()
        var startIndex: Int
        var endIndex: Int
        textData.dataList?.let { list ->
            for (index in list.indices) {
                val item = list[index]
                if (item.isNullOrEmpty()) {
                    continue
                }
                //这里拼间距
                if (index != 0) {
                    spannableStringBuilder.append(textData.infix())
                }
                //这里拼文本
                startIndex = spannableStringBuilder.length
                spannableStringBuilder.append(item)
                endIndex = spannableStringBuilder.length
                val clickableSpan = TitleClickableSpan(item, index, textData)
                spannableStringBuilder.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return spannableStringBuilder
    }

    private class TitleClickableSpan(
        private val clickText: String,
        private val index: Int,
        private val textData: TextData,
    ) : ClickableSpan() {
        override fun onClick(widget: View) {
            textData.onTextClick?.invoke(clickText, index)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false // 是否显示下划线
            if (textData.clickTextColor != -1) {
                ds.color = textData.clickTextColor
            }
        }
    }
}