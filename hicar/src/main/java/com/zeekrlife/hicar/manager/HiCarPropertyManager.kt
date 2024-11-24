package com.zeekrlife.hicar.manager

import com.zeekr.basic.appContext
import com.zeekrlife.hicar.data.database.AppShortcutBean
import com.zeekrlife.hicar.data.database.AppShortcutDB
import com.zeekrlife.hicar.data.database.AppShortcutDao
import com.zeekrlife.hicar.provider.HiCarAppListPropertiesProvider

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
        appShortcutDao.deleteAll()
    }

    fun notifyHiCarAppListChange() {
        HiCarAppListPropertiesProvider.HiCarAppListNotifyChange(appContext)
    }

}