package com.zeekrlife.hicar.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.zeekrlife.common.ext.toStartActivity
import com.zeekrlife.connect.base.manager.ConnectServiceManager
import com.zeekrlife.hicar.data.database.AppShortcutBean
import com.zeekrlife.hicar.data.database.AppShortcutDB
import com.zeekrlife.hicar.data.database.AppShortcutDao
import com.zeekrlife.hicar.ui.activity.MainActivity
import com.zeekrlife.net.interception.logging.util.logE


class HiCarAppListPropertiesProvider : ContentProvider() {

    companion object {
        const val TAG = "zzzHiCarAppListPropertiesProvider"
        const val AUTHORITY = "com.zeekrlife.hicar.HiCarAppListPropertiesProvider"
        const val URI_APPLET_TABLE_NAME = "hicar_shortcut"

        fun HiCarAppListNotifyChange(context: Context) {
            "hicarapplistShortcutNotifyChange".logE(TAG)
            val uri = Uri.parse("content://${AUTHORITY}")
            context.contentResolver.notifyChange(uri, null)
        }
    }

    private val matcher = UriMatcher(UriMatcher.NO_MATCH)

    private val CODE_QUERY_PROPERTY_ALL = 1
    private val CODE_QUERY_PROPERTY_ID = 2


    override fun onCreate(): Boolean {
        //查询所有列表
        matcher.addURI(AUTHORITY, "query/all", CODE_QUERY_PROPERTY_ALL)
        //查询指定包名：content://${authority}/query/${packageName}
        matcher.addURI(AUTHORITY, "query/*", CODE_QUERY_PROPERTY_ID)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val code: Int = matcher.match(uri)
        if (code == CODE_QUERY_PROPERTY_ALL || code == CODE_QUERY_PROPERTY_ID) {
            val context = context ?: return null
            val modelDao: AppShortcutDao = AppShortcutDB.getInstance(context).getAppShortcutDao()
            var cursor: Cursor? = null
            cursor = if (code == CODE_QUERY_PROPERTY_ALL) {
                modelDao.queryAppShortcutList()
            } else {
                modelDao.queryAppShortcut(ContentUris.parseId(uri))
            }
            return cursor
        } else {
            return null
        }
    }

    override fun getType(uri: Uri): String {
        return when (matcher.match(uri)) {
            CODE_QUERY_PROPERTY_ALL -> "vnd.android.cursor.dir/$AUTHORITY.$URI_APPLET_TABLE_NAME"
            CODE_QUERY_PROPERTY_ID -> "vnd.android.cursor.item/$AUTHORITY.$URI_APPLET_TABLE_NAME"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return when (matcher.match(uri)) {
            CODE_QUERY_PROPERTY_ALL -> {
                val context = context ?: return null
                val id: Long = AppShortcutDB.getInstance(context)
                    .getAppShortcutDao()
                    .insertAppShortcut(AppShortcutDB.fromContentValues(values))
                if (id <= 0) {
                    return null
                }
                HiCarAppListNotifyChange(context)
                ContentUris.withAppendedId(uri, id)
                null
            }
            CODE_QUERY_PROPERTY_ID -> throw java.lang.IllegalArgumentException("Invalid URI, cannot insert with ID: $uri")
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return when (matcher.match(uri)) {
            CODE_QUERY_PROPERTY_ALL -> {
                0
            }
            CODE_QUERY_PROPERTY_ID -> {
                val context = context ?: return 0
                val count: Int = AppShortcutDB.getInstance(context).getAppShortcutDao()
                    .deleteById(ContentUris.parseId(uri))
                HiCarAppListNotifyChange(context)
                count
                0
            }
            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return when (matcher.match(uri)) {
            CODE_QUERY_PROPERTY_ALL -> {
                throw IllegalArgumentException("Invalid URI, cannot update without ID$uri")
            }
            CODE_QUERY_PROPERTY_ID -> {
                val context = context ?: return 0
                val aromeShortcutBean: AppShortcutBean = AppShortcutDB.fromContentValues(values)
                aromeShortcutBean.id = ContentUris.parseId(uri)
                val count: Int = AppShortcutDB.getInstance(context).getAppShortcutDao()
                    .updateAppShortcut(aromeShortcutBean)
                HiCarAppListNotifyChange(context)
                count
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        var result = Bundle()
        try {
            when (method) {
                "notifyHiCarAppList" -> {
//                    var cacheAppList = CacheExt.getHiCarAppInfoList()?:""
//                    "notifyHiCarAppList cacheAppList = ${cacheAppList},dataSize = ${cacheAppList.length}".logE(TAG)
//                   result = setCallResult(cacheAppList)
                }
                "startHiCarApp" -> {
                    arg?.let {
                        handleRequestStartApp(arg)
                    }
                }
                "disConnectHiCarDevice" -> {
                    arg?.let {
                        handleDisConnectHiCarDevice()
                    }
                }
            }
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return super.call(method, arg, extras)
    }

    // 设置回调结果的方法
    private fun setCallResult(jsonString: String): Bundle{
        val result = Bundle().apply {
            putString("hicar_app_list", jsonString)
        }
        return result
    }


    private fun handleRequestStartApp(packageName: String,callBack: ((jsonString: String) -> Unit)? = null) {
        val bundle = Bundle().apply {
            putString("packageName",packageName)
            putBoolean("RequestStartApp",true)
        }
        toStartActivity(MainActivity::class.java,bundle)
    }

    private fun handleDisConnectHiCarDevice(callBack: ((jsonString: String) -> Unit)? = null) {
        ConnectServiceManager.getInstance().disconnectDevice("")
    }



    /**
     *  launcher use example HiCarAppList更新
     */
//    private fun notifyHiCarAppList(callBack: ((jsonString: String) -> Unit)? = null) {
//
//        val uri = Uri.parse("content://com.zeekrlife.hicar.HiCarAppListPropertiesProvider/query/all")
//        context?.contentResolver?.registerContentObserver(uri,true,object : ContentObserver(
//            Handler(Looper.getMainLooper())) {
//            override fun onChange(selfChange: Boolean) {
//                super.onChange(selfChange)
//                context!!.contentResolver.call(uri, "notifyHiCarAppList", null, null)
//            }
//        })
//    }

    /**
     *  launcher引入示例
     *  查询所有hicar列表
     */
    fun notifyHiCarAppList2() {
        val uri = Uri.parse("content://com.zeekrlife.hicar.HiCarAppListPropertiesProvider/query/all")
        context?.contentResolver?.registerContentObserver(uri,true,object : ContentObserver(
            Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
//                context!!.contentResolver.call(uri, "notifyHiCarAppList", null, null)
                var appletList : ArrayList<AppShortcutBean> = arrayListOf()
                var cursor: Cursor? = null
                try {
                    cursor = context?.contentResolver?.query(uri, null, null, null, null)
                    while (cursor?.moveToNext() == true) {
                        val appShortcutBean = AppShortcutBean()
                        cursor.columnNames.forEach {
                            val columnIndex = cursor.getColumnIndex(it)
                            when (it) {
                                "id" -> appShortcutBean.id = cursor.getLong(columnIndex)
                                "mPackageName" -> appShortcutBean.mPackageName = cursor.getString(columnIndex)
                                "mName" ->  appShortcutBean.mName = cursor.getString(columnIndex)
                                "mType" -> appShortcutBean.mType = cursor.getInt(columnIndex)
                                "mIcon"->appShortcutBean.mIcon = cursor.getBlob(columnIndex)
                            }
                        }
                        appletList.add(appShortcutBean)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    cursor?.close()
                }
            }
        })
    }
    /**
     *  start HiCarApp  action
     */
    private fun requestStartApp(packageName:String) {
        val uri = Uri.parse("content://com.zeekrlife.hicar.HiCarAppListPropertiesProvider/query/all")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context?.contentResolver?.call(uri, "startHiCarApp", packageName, null)
        }
    }

    /**
     *  断开HiCar的连接
     */
    private fun disConnectHiCarDevice(packageName:String) {
        val uri = Uri.parse("content://com.zeekrlife.hicar.HiCarAppListPropertiesProvider/query/all")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context?.contentResolver?.call(uri, "disConnectHiCarDevice", packageName, null)
        }
    }

}