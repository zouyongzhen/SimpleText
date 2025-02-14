package zyz.hero.textlib

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri

/**
 *这个类用户获取context，这个context其实就是application，但是这里调用onCreate方法时，application还没有走onCreate方法
 *ContentProvider onCreate方法走在Application onCreate方法之前。
 */
class ContextContentProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        ContextHolder.init(context?.applicationContext)
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    object ContextHolder {
        private var appContext: Context? = null
        fun init(context: Context?) {
            appContext = context
        }

        fun getContext(): Context {
            return appContext ?: throw IllegalStateException("Context is not initialized!")
        }
    }
}