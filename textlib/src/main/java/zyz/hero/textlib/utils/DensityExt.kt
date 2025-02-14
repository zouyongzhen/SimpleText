package zyz.hero.textlib.utils

import android.util.TypedValue
import zyz.hero.textlib.ContextContentProvider

/**
 * dp转px
 */
/**
 * dp转px
 */
val Number.dp: Int
    get() = (this.toFloat() * ContextContentProvider.ContextHolder.getContext().resources.displayMetrics.density + 0.5f).toInt()

/**
 * px转dp
 */
val Number.toDp: Float
    get() = this.toFloat() / (ContextContentProvider.ContextHolder.getContext().resources?.displayMetrics?.density
        ?: 1F) + 0.5f

/**
 * sp转px
 */
val Number.sp: Int
    get() = (TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        ContextContentProvider.ContextHolder.getContext().resources.displayMetrics
    )).toInt()
