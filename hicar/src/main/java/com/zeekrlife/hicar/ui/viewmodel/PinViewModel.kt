package com.zeekrlife.hicar.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.livedata.BooleanLiveData
import com.zeekrlife.common.livedata.StringLiveData
import com.zeekrlife.connect.base.manager.ConnectServiceManager
import com.zeekrlife.hicar.R
import com.zeekrlife.hicar.utils.StringResourceProvider
import com.zeekrlife.hicar.data.PageType
import com.zeekrlife.hicar.service.HiCarCoreServiceListener
import com.zeekrlife.hicar.ui.activity.MainActivity
import com.zeekrlife.hicar.ui.fragment.PinFragment
import com.zeekrlife.hicar.utils.HiCarServiceManager
import com.zeekrlife.net.interception.logging.util.LogUtils
import com.zeekrlife.net.interception.logging.util.logE
import com.zeekrlife.net.load.LoadStatusEntity
import com.zeekrlife.net.load.LoadingType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description:
 *@author: e-Yang.Dong1
 *@date: 2023/5/12 15:19:42
 *@version: V1.0
 */
class PinViewModel : BaseViewModel() {
    val pageType = MutableLiveData<PageType>()
    val pinCode = StringLiveData()
    val background = BooleanLiveData() //false：否，将HiCar切换到前台显示；true：是，将HiCar切换到后台显示。
    var releasePageTypeObserve = false
    var reConnectMac = StringLiveData()


    fun updatePageType(type: PageType) {
        if(releasePageTypeObserve){
            return
        }
        pageType.postValue(type)
    }

    fun updateBackground(b: Boolean) {
        background.postValue(b)
    }

    fun updateReConnectMac(mac:String){
        reConnectMac.postValue(mac)
    }
    /**
     * 1首次启动，配置页面类型 ：GET
     * 2异常启动，配置页面类型 ：根据当前状态来显示，如果当前是投屏相关状态：显示断开连接。
     */
    fun getPageTypeFromEvent(event:Int) : PageType{
        return when(event){
            -1,0 -> {
                PageType.CONNECTION_CODE_GET
            }
            HiCarCoreServiceListener.EVENT_DEVICE_CONNECT -> {
                PageType.HICAR_DISCONNECT
            }
            HiCarCoreServiceListener.EVENT_DEVICE_DISCONNECT -> {
                PageType.HICAR_CONNECTION_FAILED
            }
            HiCarCoreServiceListener.EVENT_DEVICE_CONNECT_FAILD -> {
                PageType.HICAR_CONNECTION_FAILED
            }
//            HiCarCoreServiceListener.EVENT_DEVICE_PROJECT_CONNECT -> {
//                PageType.APP_SHOWING
//            }
//            HiCarCoreServiceListener.EVENT_DEVICE_PROJECT_DISCONNECT -> {
//                PageType.HICAR_CONNECTION_FAILED
//            }
            HiCarCoreServiceListener.EVENT_DEVICE_CODE_SUCCESS -> {
                PageType.HICAR_CONNECT
            }

//            HiCarCoreServiceListener.EVENT_DEVICE_SERVICE_PAUSE -> {
//                PageType.HICAR_DISCONNECT
//            }
//            HiCarCoreServiceListener.EVENT_DEVICE_SERVICE_RESUME -> {
//                PageType.HICAR_DISCONNECT
//            }
//            HiCarCoreServiceListener.EVENT_DEVICE_SERVICE_START -> {
//                PageType.HICAR_DISCONNECT
//            }
//            HiCarCoreServiceListener.EVENT_DEVICE_SERVICE_STOP -> {
//                PageType.HICAR_CONNECTION_FAILED
//            }
//            HiCarCoreServiceListener.EVENT_DEVICE_DISPLAY_SERVICE_PLAYING -> {
//                PageType.HICAR_DISCONNECT
//            }
//            HiCarCoreServiceListener.EVENT_DEVICE_DISPLAY_SERVICE_PLAY_FAILED -> {
//                PageType.HICAR_CONNECTION_FAILED
//            }
            else -> {
                PageType.HICAR_DISCONNECT
            }
        }
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
    fun retryGetCodeClick(activity:MainActivity) {
        ConnectServiceManager.getInstance().openWifi()
        pageType.value = PageType.CONNECTION_CODE_GET
        viewModelScope.launch(Dispatchers.IO) {
//            HiCarServiceManager.handleStoptAdv()
            delay(500)
            "PinCodeFragment handleStartAdv".logE("PinCodeFragment")
            HiCarServiceManager.handleStartAdv(activity)
        }
    }

    fun retryClick(activity: MainActivity){
        retryGetCodeClick(activity)
    }

    fun showPinRetryButton(): Boolean {
        return pageType.value == PageType.CONNECTION_CODE_GET_FAILED || pageType.value == PageType.HICAR_CONNECTION_FAILED
    }

    fun getLoadingContentString(bluetoothName:String?) : String{
        return when(pageType.value){
            PageType.BLUETOOTHl_TURN_ON -> StringResourceProvider.resourceProvider.getString(R.string.bluetooth_starting)
            PageType.CONNECTION_CODE_GET -> StringResourceProvider.resourceProvider.getString(R.string.code_getting)
            PageType.HICAR_CONNECTING -> StringResourceProvider.resourceProvider.getString(R.string.connecting,
                bluetoothName?:""
            )
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
                "getLoadingContentString type is error".logE("PinViewModel")
                ""
            }
        }
    }
    fun getErrorOrDisConnectContentString() : String{
        return when(pageType.value){
            PageType.CONNECTION_CODE_GET_FAILED -> StringResourceProvider.resourceProvider.getString(R.string.connect_retry_notice)
            PageType.HICAR_DISCONNECT -> StringResourceProvider.resourceProvider.getString(R.string.user_leave_disconnect)
            else -> {
                "getErrorOrDisConnectContentString type is error".logE("PinViewModel")
                ""
            }
        }
    }
    fun getErrorOrDisConnectNoticeString() : String{
        return when(pageType.value){
            PageType.CONNECTION_CODE_GET_FAILED -> StringResourceProvider.resourceProvider.getString(R.string.connect_restart_notice)
            PageType.HICAR_DISCONNECT -> StringResourceProvider.resourceProvider.getString(R.string.manually_disconnect)
            else -> {
                "getErrorOrDisConnectNoticeString type is error".logE("PinViewModel")
                ""
            }
        }
    }



    fun isPinLoadingPage(newValue : PageType) : Boolean{
        return newValue == PageType.BLUETOOTHl_TURN_ON || newValue == PageType.CONNECTION_CODE_GET || newValue == PageType.HICAR_CONNECTING
    }

    fun isPinCodePage(newValue : PageType) : Boolean{
        return newValue == PageType.HICAR_CONNECT
    }

    fun isPinFailPage(newValue : PageType) : Boolean{
        return newValue == PageType.CONNECTION_CODE_GET_FAILED || newValue == PageType.HICAR_CONNECTION_FAILED
    }

    fun isSurfacePage(newValue : PageType) : Boolean{
        return newValue == PageType.APP_ENTER_LOADING ||
                newValue == PageType.APP_SHOWING ||
                newValue == PageType.APP_LOAD_FAILED
    }

    fun isDisConnectPage(newValue : PageType) : Boolean{
        return newValue == PageType.HICAR_DISCONNECT
    }

//    fun projectionError(){
//        Log.i("zzzPinFragment", "projectionError")
//        updatePageType(PageType.APP_LOAD_FAILED )
//        loadingChange.showError.value = LoadStatusEntity(
//            requestCode = "",
//            throwable = Exception(),
//            errorCode = 0,
//            errorMessage = "",
//            loadingType = LoadingType.LOADING_XML,
//        )
//    }
//    fun projectionLoading(){
//        Log.e("zzzPinFragment", "projectionLoading")
//        loadingChange.loading.value = LoadingDialogEntity(
//            loadingType = LoadingType.LOADING_XML,
//            isShow = true
//        )
//        viewModelScope.launch {
//            delay(1000)
//            updatePageType(PageType.APP_SHOWING)
//            Log.e("zzzPinFragment", "delay ")
//            delay(2000)
//            projectionSuccess()
//
//        }
//    }

//    fun disconnectDevice(){
//        HiCarServiceManager.disconnectDevice(PinFragment.mDeviceId)
//        updatePageType(PageType.CONNECTION_CODE_GET)
//        viewModelScope.launch {
//            delay(1000)
//            HiCarServiceManager.handleStartAdv()
//            delay(3000)
//            Log.e("zzzPinFragment", "disconnectDevice ")
//            updatePageType(PageType.HICAR_CONNECT)
//        }
//    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}