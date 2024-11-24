package com.zeekrlife.carlink.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.carlink.R
import com.zeekrlife.carlink.utils.StringResourceProvider
import com.zeekrlife.carlink.data.PageType
import com.zeekrlife.net.interception.logging.util.LogUtils
import com.zeekrlife.net.interception.logging.util.logE

/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description:
 *@author: e-Yang.Dong1
 *@date: 2023/5/12 15:19:42
 *@version: V1.0
 */
class PinViewModel : BaseViewModel() {
    val pageType = MutableLiveData<PageType>()
    val pinCode = MutableLiveData<String>()

    fun updatePageType(type: PageType) {
        pageType.value = type
    }

    fun updatePinCode(type: String) {
        pinCode.value = type
    }

    /**
     * 重连点击
     */
    fun retryConnectClick() {
        pageType.value = PageType.HICAR_CONNECT
    }

    /**
     * 获取连接码重试点击
     */
    fun retryGetCodeClick() {
        pageType.value = PageType.CONNECTION_CODE_GET
    }

    fun retryClick(){
        if(pageType.value ==  PageType.HICAR_CONNECTION_FAILED){
            retryConnectClick()
        }

        if(pageType.value ==  PageType.CONNECTION_CODE_GET_FAILED){
            retryGetCodeClick()
        }
    }

    fun showPinRetryButton(): Boolean {
        return pageType.value == PageType.CONNECTION_CODE_GET_FAILED || pageType.value == PageType.HICAR_CONNECTION_FAILED
    }

    fun getLoadingImageArray(type: PageType) : IntArray{
        //动画图片ID数组
        when (type){
            PageType.CONNECTION_CODE_GET -> return intArrayOf(
                R.drawable.loading_0, R.drawable.loading_1,
                R.drawable.loading_2, R.drawable.loading_3, R.drawable.loading_4,
                R.drawable.loading_5, R.drawable.loading_6, R.drawable.loading_7,
                R.drawable.loading_8, R.drawable.loading_9)
            PageType.HICAR_CONNECTING -> return intArrayOf(
                R.drawable.loading_0, R.drawable.loading_1,
                R.drawable.loading_2, R.drawable.loading_3, R.drawable.loading_4,
                R.drawable.loading_5, R.drawable.loading_6, R.drawable.loading_7,
                R.drawable.loading_8, R.drawable.loading_9)
            else -> {
                LogUtils.logE("getLoadingImageArray type is error")
                return intArrayOf()
            }
        }
    }

    fun getLoadingContentString() : String{
        return when(pageType.value){
            PageType.BLUETOOTHl_TURN_ON -> StringResourceProvider.resourceProvider.getString(R.string.bluetooth_starting)
            PageType.CONNECTION_CODE_GET -> StringResourceProvider.resourceProvider.getString(R.string.code_getting)
            PageType.HICAR_CONNECTING -> StringResourceProvider.resourceProvider.getString(R.string.connecting, "dy的华为")
            else -> {
                LogUtils.logE("getLoadingContentString type is error")
                ""
            }
        }
    }
    fun getLoadingNoticeString() : String{
        return when(pageType.value){
            PageType.BLUETOOTHl_TURN_ON -> StringResourceProvider.resourceProvider.getString(R.string.wireless_bluetooth_notice)
            PageType.CONNECTION_CODE_GET -> StringResourceProvider.resourceProvider.getString(R.string.wireless_connect_code_notice)
            else -> {
                LogUtils.logE("getLoadingContentString type is error")
                ""
            }
        }
    }
    fun getErrorOrDisConnectContentString() : String{
        return when(pageType.value){
            PageType.CONNECTION_CODE_GET_FAILED -> StringResourceProvider.resourceProvider.getString(R.string.connect_retry_notice)
            PageType.HICAR_DISCONNECT -> StringResourceProvider.resourceProvider.getString(R.string.user_leave_disconnect)
            else -> {
                LogUtils.logE("getErrorOrDisConnectContentString type is error")
                ""
            }
        }
    }
    fun getErrorOrDisConnectNoticeString() : String{
        return when(pageType.value){
            PageType.CONNECTION_CODE_GET_FAILED -> StringResourceProvider.resourceProvider.getString(R.string.connect_restart_notice)
            PageType.HICAR_DISCONNECT -> StringResourceProvider.resourceProvider.getString(R.string.manually_disconnect)
            else -> {
                LogUtils.logE("getErrorOrDisConnectNoticeString type is error")
                ""
            }
        }
    }



    fun isPinLoadingPage(newValue : PageType) : Boolean{
        return newValue == PageType.BLUETOOTHl_TURN_ON || newValue == PageType.CONNECTION_CODE_GET || newValue == PageType.HICAR_CONNECTING
    }

    fun isPinCodePage(newValue : PageType) : Boolean{
        return newValue == PageType.HICAR_CONNECT ||
                newValue == PageType.CONNECTION_CODE_GET_FAILED ||
                newValue == PageType.HICAR_CONNECTION_FAILED ||
                newValue == PageType.HICAR_DISCONNECT
    }

    fun isSurfacePage(newValue : PageType) : Boolean{
        return newValue == PageType.APP_ENTER_LOADING ||
                newValue == PageType.APP_SHOWING ||
                newValue == PageType.APP_LOAD_FAILED
    }
}