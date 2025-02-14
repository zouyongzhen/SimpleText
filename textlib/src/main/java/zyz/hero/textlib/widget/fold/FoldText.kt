package zyz.hero.textlib.widget.fold

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.StaticLayout
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.TouchDelegate
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import zyz.hero.textlib.R
import zyz.hero.textlib.utils.TextViewTouchListener
import zyz.hero.textlib.utils.dp
import zyz.hero.textlib.utils.sp
import zyz.hero.textlib.widget.fold.handler.BaseHandler
import java.text.BreakIterator
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.min

/**
 * 支持文本展开/收起、省略的控件
 */

open class FoldText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RelativeLayout(context, attrs) {
    companion object {
        const val ZERO_WIDTH_CHAR = '\u200B'

        //不可以展开/收起 超过允许最大行数省略，右下角展示一个图标
        const val SHOW_MODE_UNFOLD_WITH_BUTTON_ICON = 1

        //不可以展开/收起 超过允许最大行数省略，右下角展示一个按钮文字
        const val SHOW_MODE_UNFOLD_WITH_BUTTON_TEXT = 2

        //可以展开/收起 超过允许最大行数省略，右下角展示展开收起按钮图标
        const val SHOW_MODE_FOLD_WITH_BUTTON_ICON = 3

        //可以展开/收起 超过允许最大行数省略，右下角展示展开收起按钮文字
        const val SHOW_MODE_FOLD_WITH_BUTTON_TEXT = 4
    }


    protected open var regexList = mutableListOf<BaseHandler>()

    //整体布局的点击事件（不包含引用和按钮图标或文字）
    private var onLayoutClick: ((FoldText) -> Unit)? = null

    //右下角按钮图标或文字的点击事件
    private var onButtonClick: ((FoldText) -> Unit)? = null
    protected var textView: TextView
    private var button: TextView
    private var spannableContentString: SpannableStringBuilder = SpannableStringBuilder()
    var originalContentString: CharSequence? = ""
    private var animator: ValueAnimator? = null

    //默认超过限制行数折叠展示
    private var isFold = true

    //文本是否被省略
    private var isEllipsis = false

    //小于等于这个行数不显示展开收起按钮
    private var limitLineCount = 3
    private var buttonMarginLeft = 10.dp
    private var buttonMarginRight = 5.dp
    private var textLineSpacingMultiplier = 1.2f
    private var textLineSpacing = 0f

    //省略箭头 >
    private var buttonEllipsizeIcon = 0

    //折叠状态下的文本
    private var buttonFoldText: CharSequence = "展开"

    //展开状态下的文本
    private var buttonExpandText: CharSequence = "收起"

    //省略状态下的文本
    private var buttonEllipsizeText: CharSequence = "详情"

    //折叠状态箭头
    private var buttonFoldIcon = 0

    //展开状态箭头
    private var buttonExpandIcon = 0

    //显示模式
    private var showMode = SHOW_MODE_FOLD_WITH_BUTTON_TEXT


    //文本颜色
    private var textColor = Color.parseColor("#333333")

    //展开收起文本颜色
    private var buttonTextColor = Color.parseColor("#666666")

    //是否支持emoji，可以设置不支持用以提高性能
    private var supportEmoji = true

    private var contentTextSize = 13.sp.toFloat()
    private var buttonTextSize = 13.sp.toFloat()

    init {
        isClickable = true
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FoldText)
        buttonEllipsizeIcon = typedArray.getResourceId(R.styleable.FoldText_buttonEllipsizeIcon, 0)
        limitLineCount = typedArray.getInt(R.styleable.FoldText_limitLineCount, 3)
        buttonFoldText = typedArray.getString(R.styleable.FoldText_buttonFoldText) ?: "展开"
        buttonExpandText = typedArray.getString(R.styleable.FoldText_buttonExpandText) ?: "收起"
        buttonEllipsizeText = typedArray.getString(R.styleable.FoldText_buttonEllipsizeText) ?: "详情"
        buttonFoldIcon = typedArray.getResourceId(R.styleable.FoldText_buttonFoldIcon, 0)
        buttonExpandIcon = typedArray.getResourceId(R.styleable.FoldText_buttonExpandIcon, 0)
        showMode = typedArray.getInt(R.styleable.FoldText_showMode, SHOW_MODE_FOLD_WITH_BUTTON_TEXT)
        buttonMarginLeft = typedArray.getDimensionPixelSize(R.styleable.FoldText_buttonMarginLeft, 10.dp)
        buttonMarginRight = typedArray.getDimensionPixelSize(R.styleable.FoldText_buttonMarginRight, 5.dp)
        textColor = typedArray.getColor(R.styleable.FoldText_contentTextColor, Color.parseColor("#333333"))
        textLineSpacingMultiplier = typedArray.getFloat(R.styleable.FoldText_contentTextLineSpacingMultiplier, 1.2f)
        textLineSpacing = typedArray.getFloat(R.styleable.FoldText_contentTextLineSpacing, 0f)
        buttonTextColor = typedArray.getColor(R.styleable.FoldText_buttonTextColor, Color.parseColor("#666666"))

        contentTextSize = typedArray.getDimension(R.styleable.FoldText_contentTextSize, 13.sp.toFloat())
        buttonTextSize = typedArray.getDimension(R.styleable.FoldText_buttonTextSize, 13.sp.toFloat())
        supportEmoji = typedArray.getBoolean(R.styleable.FoldText_supportEmoji, true)
        typedArray.recycle()
        LayoutInflater.from(context).inflate(R.layout.simple_text_layout_quote_fold_text, this)
        textView = findViewById(R.id.text)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTextSize)
        textView.setTextColor(textColor)
        textView.setLineSpacing(textLineSpacing, textLineSpacingMultiplier)
        button = findViewById(R.id.button)
        //这里是因为textview 多行文本时右侧大概率无法对齐，这里把展开按钮往左偏移一部分减少视觉上的错位效果
        (button.layoutParams as? MarginLayoutParams)?.let {
            it.rightMargin = buttonMarginRight.toInt()
            button.requestLayout()
        }
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonTextSize)
        button.setTextColor(buttonTextColor)
        button.setOnClickListener {
            when (showMode) {
                SHOW_MODE_FOLD_WITH_BUTTON_TEXT, SHOW_MODE_FOLD_WITH_BUTTON_ICON -> {
                    toggle()
                }

                else -> {
                    onButtonClick?.invoke(this)
                }
            }
        }
        setOnClickListener {
            onLayoutClick?.invoke(this)
        }
    }

    private fun toggle() {
        this.isFold = !isFold
        setText(originalContentString)
    }

    fun isFold(): Boolean {
        return isFold;
    }

    fun isEllipsis(): Boolean {
        return isEllipsis;
    }

    fun setText(contentStr: CharSequence?) {
        originalContentString = contentStr
        if (contentStr.isNullOrEmpty()) {
            textView.text = null
            return
        }
        this.spannableContentString = handleSpan(contentStr)
        post {
            val textViewWidth: Int = width - getPaddingLeft() - getPaddingRight()
            if (textViewWidth <= 0) {
                return@post
            }
            val lineCount = getStaticLayout(textViewWidth).lineCount
            when {
                //<=limitLineCount
                lineCount <= limitLineCount -> {
                    isEllipsis = false
                    button.visibility = View.GONE
                    textView.text = spannableContentString
                }

                else -> {
                    button.visibility = View.VISIBLE
                    when (showMode) {
                        SHOW_MODE_FOLD_WITH_BUTTON_TEXT, SHOW_MODE_UNFOLD_WITH_BUTTON_TEXT -> {
                            showTextAndHandleContent()
                        }

                        SHOW_MODE_FOLD_WITH_BUTTON_ICON, SHOW_MODE_UNFOLD_WITH_BUTTON_ICON -> {
                            showIconAndHandleContent()
                        }
                    }
                }
            }
            //扩大button 点击的面积
            expandFoldClickArea()
        }
    }

    protected open fun handleMatcher(matcher: Matcher, spannableStringBuilder: SpannableStringBuilder) {

    }

    protected open fun handleSpan(contentString: CharSequence): SpannableStringBuilder {
        if (regexList.isEmpty()) {
            return SpannableStringBuilder(contentString)
        }
        val pattern: Pattern = Pattern.compile(regexList.joinToString("|") { it.regex })
        val matcher: Matcher = pattern.matcher(contentString)
        var lastEnd = 0
        // 创建SpannableStringBuilder来构建新的文本
        val spannableStringBuilder = SpannableStringBuilder()
        // 循环查找所有匹配项
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            // 添加之前未匹配的文本部分
            val beforeTextTemp = contentString.subSequence(lastEnd, start)
            val beforeText = insertZeroWidthSpace(beforeTextTemp)
            spannableStringBuilder.append(beforeText)
            regexList.firstOrNull {
                val matchText = matcher.group(regexList.indexOf(it) + 1)
                (matchText != null).apply {
                    it.handle(textView, matchText, spannableStringBuilder)
                }
            }
            // 更新lastEnd
            lastEnd = end
        }
        // 添加最后一个匹配项后面的文本
        val lastSubstringTemp = contentString.substring(lastEnd)
        val lastSubstring = insertZeroWidthSpace(lastSubstringTemp)
        spannableStringBuilder.append(lastSubstring);
        return spannableStringBuilder
    }

    /**
     * 插入零宽字符，防止展示优化导致的自动换行等问题
     */
    private fun insertZeroWidthSpace(text: CharSequence?): String {
        if (text.isNullOrEmpty()) return ""
        if (supportEmoji) {
            // 使用 BreakIterator 支持完整字符（包括 Emoji）
            val breakIterator = BreakIterator.getCharacterInstance()
            breakIterator.setText(text.toString())
            // 预估最大长度：每个字符后添加一个零宽字符
            val charArray = CharArray(text.length * 2) { ZERO_WIDTH_CHAR }
            var arrayIndex = 0

            var start = breakIterator.first()
            var end = breakIterator.next()

            while (end != BreakIterator.DONE) {
                val charSequence = text.subSequence(start, end)
                // 将完整字符写入数组
                for (char in charSequence) {
                    charArray[arrayIndex++] = char
                }
                // 添加零宽字符
                charArray[arrayIndex++] = ZERO_WIDTH_CHAR
                start = end
                end = breakIterator.next()
            }

            return String(charArray, 0, arrayIndex) // 根据实际长度创建字符串
        } else {
            //这里实现每个字符后面都加上零宽字符
            val charArray = CharArray(text.length * 2) { ZERO_WIDTH_CHAR }
            for (i in text.indices) {
                charArray[i * 2] = text[i] // 放入原字符
            }
            return String(charArray)
        }
    }

    private fun getStaticLayout(width: Int): StaticLayout {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(this.spannableContentString, 0, this.spannableContentString.length, textView.paint, width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(textView.lineSpacingExtra, textView.lineSpacingMultiplier)
                .setIncludePad(textView.includeFontPadding)
                .build()
        } else {
            StaticLayout(
                this.spannableContentString,
                textView.paint,
                width,
                Layout.Alignment.ALIGN_NORMAL,
                textView.lineSpacingMultiplier,
                textView.lineSpacingExtra,
                textView.includeFontPadding
            )
        }
    }

    private fun showText() {
        //可用的文本宽度width
        val availableSpacing = width - getPaddingLeft() - getPaddingRight()
        if (availableSpacing <= 0) {
            return
        }
        val staticLayout = getStaticLayout(availableSpacing)
        val lastLineNum = when (showMode) {
            SHOW_MODE_FOLD_WITH_BUTTON_TEXT, SHOW_MODE_FOLD_WITH_BUTTON_ICON -> {
                if (isFold) min(staticLayout.lineCount - 1, limitLineCount - 1) else staticLayout.lineCount - 1
            }

            else -> {
                min(staticLayout.lineCount - 1, limitLineCount - 1)
            }
        }

        val lastLineStart = staticLayout.getLineStart(lastLineNum)
        //折叠或省略状态下，这里使用从最后一行开始的所有文本进行省略计算，避免出现最后一行不够展示一个字符，但是能够展示一个省略箭头从而导致省略号不显示的问题
        //因为这里使用从最后一行开始的所有文本进行省略计算，计算结果中会出现最后行包含换行符的情况，这里把从最后一行开始的所有文本的换行符去掉
        val remainFromLastLine = kotlin.runCatching {
            spannableContentString.subSequence(lastLineStart, spannableContentString.length)
        }.getOrNull()
        if (remainFromLastLine !is SpannableStringBuilder) return
        val regex = "\\r?\\n".toRegex() // 匹配所有换行符
        val matches = regex.findAll(remainFromLastLine) // 找到所有换行符的位置
        // 倒序删除
        matches.toList().asReversed().forEach { matchResult ->
            remainFromLastLine.replace(matchResult.range.first, matchResult.range.last + 1, "")
        }
        //最后一行剩余的宽度
        val lastLineAvailableSpacing = width - (button.width + buttonMarginLeft + buttonMarginRight + paddingLeft + paddingRight)
        //Question: 考虑一种情况：当文本展开时最后一行本来是可以展示下的，但是加上图标或者展开/收起文本后放不下了？
        //当模式是可以折叠展开的模式 行数大于限制行数 且是展开状态 剩余空间不够展示最后一行文本加按钮 会出现这种情况
        val resultLastLineText = when {
            (showMode == SHOW_MODE_FOLD_WITH_BUTTON_TEXT || showMode == SHOW_MODE_FOLD_WITH_BUTTON_ICON)
                    && staticLayout.lineCount > limitLineCount
                    && !isFold
                    && staticLayout.getLineWidth(lastLineNum) > lastLineAvailableSpacing -> {
                remainFromLastLine.append("\n")
            }

            else -> {
                TextUtils.ellipsize(remainFromLastLine, textView.paint, lastLineAvailableSpacing.toFloat(), TextUtils.TruncateAt.END).also {
                    isEllipsis = remainFromLastLine.toString() != it.toString()
                }
            }
        }
        val resultText = spannableContentString.subSequence(0, lastLineStart).apply {
            (this as? SpannableStringBuilder)?.append(resultLastLineText)
        }
        textView.text = resultText
        textView.setOnTouchListener(TextViewTouchListener(resultText as? SpannableStringBuilder))
    }

    /**
     * 扩大展开收起按钮的点击区域
     */
    private fun expandFoldClickArea() {
        if (button.visibility == VISIBLE) {
            button.post {
                val bounds = Rect()
                button.getHitRect(bounds)
                bounds.left -= 20.dp
                bounds.top -= 20.dp
                bounds.right += 20.dp
                bounds.bottom += 20.dp
                touchDelegate = TouchDelegate(bounds, button)
            }
        } else {
            touchDelegate = null
        }
    }

    /**
     * 显示按钮文本，隐藏按钮图标并做正文的处理和展示
     */
    private fun showTextAndHandleContent() {
        val buttonText: CharSequence =
            if (showMode == SHOW_MODE_FOLD_WITH_BUTTON_TEXT) (if (isFold) buttonFoldText else buttonExpandText) else buttonEllipsizeText
        button.text = buttonText
        button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        button.post { showText() }
    }

    /**
     * 显示按钮图标，隐藏按钮文本并做正文的处理和展示
     */
    private fun showIconAndHandleContent() {
        button.text = null
        val buttonIcon = if (showMode == SHOW_MODE_FOLD_WITH_BUTTON_ICON) (if (isFold) buttonFoldIcon else buttonExpandIcon) else buttonEllipsizeIcon
        button.setCompoundDrawablesWithIntrinsicBounds(0, 0, buttonIcon, 0)
        button.post { showText() }
    }

    private fun animateViewHeight(view: View, startHeight: Int, endHeight: Int, duration: Long) {
        animator?.run { cancel() }
        animator = ValueAnimator.ofInt(startHeight, endHeight)
        animator?.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = value
            view.layoutParams = layoutParams
        }
        animator?.duration = duration
        animator?.start()
    }

    override fun onDetachedFromWindow() {
        animator?.run { cancel() }
        super.onDetachedFromWindow()
    }

    //    override fun onConfigurationChanged(newConfig: Configuration?) {
    //        super.onConfigurationChanged(newConfig)
    //        setText(originalContentString)
    //    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (!originalContentString.isNullOrEmpty()) {
            setText(originalContentString)
        }
    }


    fun setOnLayoutClickListener(onTextViewClick: (FoldText) -> Unit): FoldText {
        return this.apply {
            this.onLayoutClick = onTextViewClick
        }
    }

    fun setOnButtonClickListener(onButtonClick: (FoldText) -> Unit): FoldText {
        return this.apply {
            this.onButtonClick = onButtonClick
        }
    }

    fun setButtonTextSize(buttonTextSizePx: Float): FoldText {
        return this.apply {
            this.buttonTextSize = buttonTextSizePx
        }
    }

    fun setQuoteSizeFraction(contentTextSizePx: Float): FoldText {
        return this.apply {
            this.contentTextSize = contentTextSizePx
        }
    }

    fun setButtonTextColor(buttonTextColor: Int): FoldText {
        return this.apply {
            this.buttonTextColor = buttonTextColor
        }
    }

    fun setTextColor(textColor: Int): FoldText {
        return this.apply {
            this.textColor = textColor
        }
    }

    fun setShowMode(showMode: Int): FoldText {
        return this.apply {
            this.showMode = showMode
        }
    }

    fun setButtonExpandIcon(buttonExpandIcon: Int): FoldText {
        return this.apply {
            this.buttonExpandIcon = buttonExpandIcon
        }
    }

    fun setButtonFoldIcon(buttonFoldIcon: Int): FoldText {
        return this.apply {
            this.buttonFoldIcon = buttonFoldIcon
        }
    }

    fun setButtonEllipsizeText(buttonEllipsizeText: CharSequence): FoldText {
        return this.apply {
            this.buttonEllipsizeText = buttonEllipsizeText
        }
    }

    fun setButtonExpandText(buttonExpandText: CharSequence): FoldText {
        return this.apply {
            this.buttonExpandText = buttonExpandText
        }
    }

    fun setButtonFoldText(buttonFoldText: CharSequence): FoldText {
        return this.apply {
            this.buttonFoldText = buttonFoldText
        }
    }

    fun setButtonMarginRight(buttonMarginRightPx: Int): FoldText {
        return this.apply {
            this.buttonMarginRight = buttonMarginRightPx
        }
    }

    fun setButtonMarginLeft(buttonMarginLeftPx: Int): FoldText {

        return this.apply {
            this.buttonMarginLeft = buttonMarginLeftPx
        }
    }

    fun setButtonEllipsizeIcon(buttonEllipsizeIcon: Int): FoldText {
        return this.apply {
            this.buttonEllipsizeIcon = buttonEllipsizeIcon
        }
    }

    fun setLimitLineCount(limitLineCount: Int): FoldText {
        return this.apply {
            this.limitLineCount = limitLineCount
        }
    }

    fun setTextLineSpacingMultiplier(textLineSpacingMultiplier: Float): FoldText {
        return this.apply {
            this.textLineSpacingMultiplier = textLineSpacingMultiplier
        }
    }

    fun setTextLineSpacing(textLineSpacing: Float): FoldText {
        return this.apply {
            this.textLineSpacing = textLineSpacing
        }
    }

    fun setFold(isFold: Boolean): FoldText {
        return this.apply {
            this.isFold = isFold
        }
    }

    fun setHandler(vararg handlers: BaseHandler): FoldText {
        return this.apply {
            regexList.addAll(handlers)
        }
    }

}
