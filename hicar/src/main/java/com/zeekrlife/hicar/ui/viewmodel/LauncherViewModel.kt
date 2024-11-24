package com.zeekrlife.hicar.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zeekr.sdk.device.impl.DeviceAPI
import com.zeekr.sdk.user.impl.UserAPI
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.livedata.BooleanLiveData
import com.zeekrlife.hicar.data.cache.CacheExt
import com.zeekrlife.hicar.data.response.OpenApiDeviceInfo
import com.zeekrlife.hicar.data.response.OpenApiInfo
import com.zeekrlife.hicar.data.response.OpenApiUserInfo
import com.zeekrlife.hicar.data.response.ProtocolInfoBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LauncherViewModel : BaseViewModel() {

    var mIsLogin =  BooleanLiveData()
    var mOpenApiInfo = MutableLiveData<OpenApiInfo>()
    var mRemoteProtocolInfo = MutableLiveData<MutableList<ProtocolInfoBean>>()

    fun getOpenApiInfo(openApiUserInstance: UserAPI, openApiDeviceInstance: DeviceAPI) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                val userInfo = (OpenApiUserInfo(
                    openApiUserInstance.userInfo?.userId,
                    openApiUserInstance.userInfo?.username,
                    openApiUserInstance.userInfo?.avatar,
                    openApiUserInstance.userInfo?.mobile,
                    openApiUserInstance.userInfo?.sex,
                    openApiUserInstance.userInfo?.identity,
                    openApiUserInstance.userInfo?.expand,
                    openApiUserInstance.hasLogin(),
                    openApiUserInstance.token,
//                    openApiUserInstance.logout(),
                ))
                val deviceInfo = (OpenApiDeviceInfo(
                    openApiDeviceInstance.ihuid,
                    openApiDeviceInstance.vin,
                    "",
                    openApiDeviceInstance.xdsn,
                    openApiDeviceInstance.iccid,
                    openApiDeviceInstance.vehicleType,
                    openApiDeviceInstance.projectCode,
                    openApiDeviceInstance.supplierCode,
                    openApiDeviceInstance.operatorCode,
                    openApiDeviceInstance.operatorName,
                    openApiDeviceInstance.openIHUID,
                    openApiDeviceInstance.openVIN,
                    openApiDeviceInstance.ihuSerialNo,
                    openApiDeviceInstance.deviceServiceIDJson,
                    openApiDeviceInstance.vehicleTypeConfig,

                    ))
                CacheExt.setOpenApi(OpenApiInfo(userInfo, deviceInfo))
                mOpenApiInfo.postValue(OpenApiInfo(userInfo, deviceInfo))
            }
        }
    }

    /**
     * 首次启动获取协议详情（未有缓存）
     */
    fun getProtocolInfo() {
    }


}