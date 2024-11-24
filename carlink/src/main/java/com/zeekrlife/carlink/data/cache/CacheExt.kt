package com.zeekrlife.carlink.data.cache

import com.zeekrlife.carlink.app.ext.mmkv
import com.zeekrlife.carlink.app.ext.mmkvSave
import com.zeekrlife.carlink.data.ValueKey
import com.zeekrlife.carlink.data.response.OpenApiInfo
import com.zeekrlife.carlink.data.response.ProtocolInfoBean
import com.zeekrlife.carlink.data.response.UserInfo

object CacheExt {

    /**
     * 是否已经登录
     */
    fun isLogin(): Boolean {
        return mmkv.decodeParcelable(
            ValueKey.USER_INFO,
            UserInfo::class.java
        ) != null
    }

    /**
     * 保存用户信息
     */
    fun setUserInfo(userInfo: UserInfo) {
        mmkv.encode(ValueKey.USER_INFO, userInfo)
    }

    /**
     * 退出登陆
     */
    fun loginOut() {
        mmkv.clear()
    }

    /**
     * 同意协议
     */
    fun setAgreementProtocol():Boolean {
        return mmkv.encode(ValueKey.USER_AGREEMENT_PROTOCOL + (getOpenApi()?.userInfo?.userId ?: ""),true)
    }

    /**
     * 是否同意过协议
     */
    fun isAgreementProtocol():Boolean {
        return mmkv.getBoolean(ValueKey.USER_AGREEMENT_PROTOCOL + (getOpenApi()?.userInfo?.userId ?: ""),false)
    }

    fun setOpenApi(openApiInfo: OpenApiInfo) {
        mmkv.encode(ValueKey.OPENAPI_INFO, openApiInfo)
    }

    /**
     * OpenApi
     */
    fun getOpenApi(): OpenApiInfo? {
        return mmkv.decodeParcelable(
            ValueKey.OPENAPI_INFO,
            OpenApiInfo::class.java
        )
    }

    fun setUserAgreement(protocolInfoBean: ProtocolInfoBean) {
        mmkv.encode(
            ValueKey.USER_AGREEMENT_INFO + (getOpenApi()?.userInfo?.userId ?: ""),
            protocolInfoBean
        )
    }

    fun setProtocol(protocolInfoBean: ProtocolInfoBean) {
        mmkv.encode(
            ValueKey.LAUNCHER_PROTOCOL_INFO + (getOpenApi()?.userInfo?.userId ?: ""),
            protocolInfoBean
        )
    }

    /**
     * 用户协议
     */
    fun getUserAgreement(): ProtocolInfoBean? = mmkv.decodeParcelable(
        ValueKey.USER_AGREEMENT_INFO + (getOpenApi()?.userInfo?.userId ?: ""),
        ProtocolInfoBean::class.java
    )

    /**
     * 隐私信息
     */
    fun getProtocol(): ProtocolInfoBean? = mmkv.decodeParcelable(
        ValueKey.LAUNCHER_PROTOCOL_INFO + (getOpenApi()?.userInfo?.userId ?: ""),
        ProtocolInfoBean::class.java
    )

    /**
     * 获取免责声明是否同意
     */
    fun getAgreeDisclaimer() : Boolean {
        return mmkvSave.decodeBool(ValueKey.DISCLAIMER)
    }
    /**
     * 同意免责声明
     */
    fun setAgreeDisclaimer(){
        mmkvSave.encode(ValueKey.DISCLAIMER, true)
    }
}