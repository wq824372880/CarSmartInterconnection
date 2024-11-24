package com.zeekrlife.connect.core.manager

import com.huawei.managementsdk.launcher.AppInfoChangeListener
import com.huawei.managementsdk.launcher.AppTransManager
import com.zeekrlife.connect.core.app.App

object HiCarAppListManager {

    fun registerAppInfoChangeListener(listener: AppInfoChangeListener){
        AppTransManager.getInstance().init(App.application)
        AppTransManager.getInstance().registerAppInfoChangeListener(listener)

    }

    fun unRegisterAppInfoChangeListener(listener: AppInfoChangeListener){
        AppTransManager.getInstance().unregisterAppInfoChangeListener(listener)
//        AppTransManager.release()
    }

    fun releaseAppTransManager(){
        AppTransManager.release()
    }

}