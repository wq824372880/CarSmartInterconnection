package com.zeekrlife.connect.core.manager

import com.zeekr.basic.appContext
import com.zeekrlife.connect.core.data.database.AppShortcutBean
import com.zeekrlife.connect.core.data.database.AppShortcutDB
import com.zeekrlife.connect.core.data.database.AppShortcutDao
import com.zeekrlife.connect.core.provider.HiCarAppListPropertiesProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object HiCarPropertyManager {

    private val appShortcutDao: AppShortcutDao by lazy {
        AppShortcutDB.getInstance(appContext).getAppShortcutDao()
    }

    /**
     * 添加hicar applist到桌面
     */
    fun addShortcutList(list: List<AppShortcutBean>) {
            appShortcutDao.insertAppShortcutList(list)
    }
    fun deleteShortcutList() {
        val cursor = appShortcutDao.queryAppShortcutList()
        cursor?.let {
            if (it.moveToNext()) appShortcutDao.deleteAll()
            it.close()
        }
    }

    fun notifyHiCarAppListChange() {
        MainScope().launch(Dispatchers.Default) {
            delay(800)
            HiCarAppListPropertiesProvider.HiCarAppListNotifyChange(appContext)
        }

    }

}