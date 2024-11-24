package com.zeekrlife.connect.core.data.response

import com.huawei.managementsdk.launcher.AppInfoBean
import com.zeekrlife.aidl.IHiCarAppInfo

class HiCarAppInfo : IHiCarAppInfo(){

    fun setData(appInfoBean: AppInfoBean){
        mPackageName = appInfoBean.pkgName
        mName = appInfoBean.name
        mIcon = appInfoBean.icon
        mType =appInfoBean.type
    }
}