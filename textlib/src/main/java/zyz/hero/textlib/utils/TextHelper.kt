package zyz.hero.textlib.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ReplacementSpan
import android.text.style.SuperscriptSpan
import android.view.MotionEvent
import android.view.View
import android.widget.TextView

fun String.toColor() = Color.parseColor(this)
fun Spannable?.charSequencePrefixConcat(prefix: CharSequence?): Spannable {
    return if (this.isNullOrBlank()) {
        SpannableStringBuilder()
    } else {
        SpannableStringBuilder(prefix ?: "").append(this)
    }
}

class SpaceSpan(private val width: Int) : ReplacementSpan() {
    override fun getSize(
        paint: Paint, text: CharSequence, start: Int, end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        return width
    }

    override fun draw(
        canvas: Canvas, text: CharSequence, start: Int, end: Int,
        x: Float, top: Int, y: Int, bottom: Int, paint: Paint
    ) {
    }
}

class TextViewTouchListener(val spannable: Spannable?) : View.OnTouchListener {
    override fun onTouch(widget: View?, event: MotionEvent): Boolean {
        if (widget is TextView) {
            val action = event.action
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                var x = event.x.toInt()
                var y = event.y.toInt()
                x -= widget.totalPaddingLeft
                y -= widget.totalPaddingTop
                x += widget.scrollX
                y += widget.scrollY
                val layout = widget.layout
                val line = layout.getLineForVertical(y)
                val off = layout.getOffsetForHorizontal(line, x.toFloat())
                val link = spannable?.getSpans(off, off, ClickableSpan::class.java)
                if (!link.isNullOrEmpty()) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget)
                    }
                    return true
                }
            }
            return false
        } else {
            return false
        }
    }
}

class TopAlignSpan @JvmOverloads constructor(fontSizePx: Float, shiftPercentage: Float = 0f) : SuperscriptSpan() {
    /** 字体大小  */
    private val fontSizePx: Float

    //shift value, 0 to 1.0
    private var shiftPercentage: Float = 0f

    //sets the shift percentage
    //doesn't shift
    init {
        if (shiftPercentage > 0.0 && shiftPercentage < 1.0) {
            this.shiftPercentage = shiftPercentage
        }
        this.fontSizePx = fontSizePx
    }

    override fun updateDrawState(tp: TextPaint) {
        //original ascent
        val ascent = tp.ascent()

        //scale down the font
        tp.textSize = fontSizePx

        //get the new font ascent
        val newAscent = tp.fontMetrics.ascent

        //计算基线偏移量
        tp.baselineShift = (tp.baselineShift + (ascent - newAscent) * (1 - shiftPercentage)).toInt()
    }

    override fun updateMeasureState(tp: TextPaint) {
        updateDrawState(tp)
    }
}

