package com.zeekrlife.connect.core.data.entity

import com.huawei.managementsdk.launcher.AppInfoBean
import com.zeekrlife.connect.core.data.response.HiCarAppInfo

object HiCarMapper {
    fun aidlToEntity(appInfoBean: AppInfoBean): HiCarAppInfo {
        val hiCarAppInfo = HiCarAppInfo()
        hiCarAppInfo.setData(appInfoBean)
        return hiCarAppInfo
    }

    fun aidlToEntityList(appInfoBeanList:List<AppInfoBean>): List<HiCarAppInfo> {
        val hiCarAppInfoList = mutableListOf<HiCarAppInfo>()
        if(appInfoBeanList.isNotEmpty()){
            appInfoBeanList.forEach {
               aidlToEntity(it).let {hiCarAppInfo->
                   hiCarAppInfoList.add(hiCarAppInfo)
               }
            }
        }
        return hiCarAppInfoList
    }
}