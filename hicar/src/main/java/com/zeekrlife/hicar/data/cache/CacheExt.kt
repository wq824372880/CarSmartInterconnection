package com.zeekrlife.hicar.data.cache

import com.tencent.mmkv.MMKV
import com.zeekr.basic.appContext
import com.zeekrlife.hicar.app.App
import com.zeekrlife.hicar.app.ext.mmkv
import com.zeekrlife.hicar.app.ext.mmkvSave
import com.zeekrlife.hicar.data.response.OpenApiInfo
import com.zeekrlife.hicar.data.response.UserInfo

object CacheExt {

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
     * 同意协议
     */
    fun setAgreementProtocol():Boolean {
        checkMMKVInitialize()
        return mmkv.encode(ValueKey.USER_AGREEMENT_PROTOCOL + (getOpenApi()?.userInfo?.userId ?: ""),true)
    }

    /**
     * 是否同意过协议
     */
    fun isAgreementProtocol():Boolean {
        checkMMKVInitialize()
        return mmkv.getBoolean(ValueKey.USER_AGREEMENT_PROTOCOL + (getOpenApi()?.userInfo?.userId ?: ""),false)
    }

    fun setOpenApi(openApiInfo: OpenApiInfo) {
        checkMMKVInitialize()
        mmkv.encode(ValueKey.OPENAPI_INFO, openApiInfo)
    }

    /**
     * OpenApi
     */
    fun getOpenApi(): OpenApiInfo? {
        checkMMKVInitialize()
        return mmkv.decodeParcelable(
            ValueKey.OPENAPI_INFO,
            OpenApiInfo::class.java
        )
    }


    /**
     * 获取免责声明是否同意
     */
    fun getAgreeDisclaimer() : Boolean {
        checkMMKVInitialize()
        return mmkvSave.decodeBool(ValueKey.DISCLAIMER)
    }


    /**
     * 同意免责声明
     */
    fun setAgreeDisclaimer(){
        checkMMKVInitialize()
        mmkvSave.encode(ValueKey.DISCLAIMER, true)
    }

    /**
     * 免责声明倒计时
     */
    fun setShowCountDown(boolean: Boolean){
        checkMMKVInitialize()
        mmkvSave.encode(ValueKey.DISCLAIMER_COUNT_DOWN, boolean)
    }

    /**
     * 免责声明倒计时
     */
    fun getShowCountDown() : Boolean {
        checkMMKVInitialize()
        return mmkvSave.decodeBool(ValueKey.DISCLAIMER_COUNT_DOWN)
    }

}