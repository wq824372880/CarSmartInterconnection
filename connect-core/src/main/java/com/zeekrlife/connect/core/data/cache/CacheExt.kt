package com.zeekrlife.connect.core.data.cache
import com.tencent.mmkv.MMKV
import com.zeekrlife.common.util.GsonUtils
import com.zeekrlife.connect.core.app.App
import com.zeekrlife.connect.core.data.response.UserInfo
import com.zeekrlife.connect.core.app.ext.mmkv
import com.zeekrlife.connect.core.data.request.ManualReconnectParams
import com.zeekrlife.connect.core.data.response.LastTrustPhoneInfo
import com.zeekrlife.net.interception.logging.util.logE

object CacheExt {
    const val TAG = "CacheExt"
    private fun checkMMKVInitialize() {
        if (MMKV.getRootDir() == null) {
            MMKV.initialize(App.application)
        }
    }

    /**
     * 是否已经登录
     */
    fun isLogin(): Boolean {
        checkMMKVInitialize()
        return mmkv.decodeParcelable(
            ValueKey.USER_INFO,
            UserInfo::class.java
        ) != null
    }

    /**
     * 保存用户信息
     */
    fun setUserInfo(userInfo: UserInfo) {
        checkMMKVInitialize()
        mmkv.encode(ValueKey.USER_INFO, userInfo)
    }

    /**
     * 退出登陆
     */
    fun loginOut() {
        checkMMKVInitialize()
        mmkv.clear()
    }

    /**
     * 获取applist列表
     */
//    fun getHiCarAppInfoList() : HiCarAppInfoListBean? {
//        checkMMKVInitialize()
//        return mmkv.decodeParcelable(ValueKey.HICAR_APP_LIST, HiCarAppInfoListBean::class.java)
//    }
//
//    fun setHiCarAppInfoList(appInfoListBean: HiCarAppInfoListBean){
//        checkMMKVInitialize()
//        mmkv.encode(ValueKey.HICAR_APP_LIST, appInfoListBean)
//    }

    fun getHiCarManualReconnect():ManualReconnectParams?{
        checkMMKVInitialize()
        val hiCarManualReconnectParams = mmkv.decodeParcelable(ValueKey.HICAR_MANUAL_RECONNECT, ManualReconnectParams::class.java,ManualReconnectParams(0))
        "CacheExt.getHiCarManualReconnect()  ManualReconnectParams: ${GsonUtils.toJson(hiCarManualReconnectParams, ManualReconnectParams::class.java)}".logE(TAG)
        return hiCarManualReconnectParams

    }

    /**
     * 手动断开连接
     * 0: 允许发起自动回连
     * 1: 不在发起自动回连
     */

    fun setHiCarManualReconnect(isUserDisconnect:ManualReconnectParams){
        "CacheExt.setHiCarManualReconnect() isUserDisconnect:${isUserDisconnect.isUserDisconnect}".logE(TAG)
        checkMMKVInitialize()
        mmkv.encode(ValueKey.HICAR_MANUAL_RECONNECT,isUserDisconnect)
    }

    fun getHiCarLastTrustPhoneInfo(): LastTrustPhoneInfo? {
        checkMMKVInitialize()
        val info = mmkv.decodeParcelable(ValueKey.HICAR_LAST_TRUST_PHONE,LastTrustPhoneInfo::class.java)
        "CacheExt.getHiCarManualReconnect()  ManualReconnectParams: ${GsonUtils.toJson(info)}".logE(TAG)
        return info
    }

    /**
     * 最后一次连接的手机信息（自动连接使用）
     */
    fun setHiCarLastTrustPhoneInfo(info:LastTrustPhoneInfo){
        "CacheExt.setHiCarLastTrustPhoneInfo() info:${GsonUtils.toJson(info)}".logE(TAG)
        checkMMKVInitialize()
        mmkv.encode(ValueKey.HICAR_LAST_TRUST_PHONE,info)
    }



}