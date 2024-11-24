/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2022. All rights reserved.
 */
package com.zeekrlife.carlink.service


/**
 * Interface for HiCarDemoService
 */
interface HiCarCoreServiceListener {
    /**
     * onDeviceChange
     *
     * @param key key
     * @param event evnet
     * @param errorcode errorcode
     */
    fun onDeviceChange(key: String?, event: Int, errorcode: Int)

    /**
     * onDeviceServiceChange
     *
     * @param serviceId serviceId
     * @param event event
     */
    fun onDeviceServiceChange(serviceId: String?, event: Int)

    /**
     * onDataReceive
     *
     * @param key key
     * @param dataType data type
     * @param data data
     */
    fun onDataReceive(key: String?, dataType: Int, data: ByteArray?)

    /**
     * onPinCode
     *
     * @param code code
     */
    fun onPinCode(code: String?)

    /**
     * onBinderDied
     */
    fun onBinderDied()

    /**
     * getCustomizedAudioAttributes
     *
     * @param usages usages
     * @param contentTypes contentTypes
     * @param focusGain focusGain
     * @param map map
     * @return CustomizedAudioAttributes
     */

    companion object {
        /**
         * onDeviceChange()回调
         */
        const val EVENT_DEVICE_CONNECT = 101  //连接中...
        const val EVENT_DEVICE_DISCONNECT = 102  //断开连接
        const val EVENT_DEVICE_CONNECT_FAILD = 103 //连接失败
        const val EVENT_DEVICE_PROJECT_CONNECT = 104 //投屏连接
        const val EVENT_DEVICE_PROJECT_DISCONNECT = 105 //投屏断开连接
        const val EVENT_DEVICE_ADV_START = 106  //蓝牙广播已开始
        const val EVENT_DEVICE_ADV_STOP = 107 //蓝牙广播停止
        const val EVENT_DEVICE_START_ADV_TIMEOUT = 124 //发现广播发送超时
        const val EVENT_DEVICE_MIC_REQUEST = 109 //请求使用车机MIC
        const val EVENT_DEVICE_MIC_RELEASE = 110 //车机MIC释放通知


        /**
         * onDeviceServiceChange()回调
         */
        const val EVENT_DEVICE_SERVICE_PAUSE = 202 //投屏服务暂停
        const val EVENT_DEVICE_SERVICE_RESUME = 203 //投屏服务恢复
        const val EVENT_DEVICE_SERVICE_START = 204 //投屏服务启动
        const val EVENT_DEVICE_SERVICE_STOP = 205 //投屏服务停止
        const val EVENT_DEVICE_DISPLAY_SERVICE_PLAYING = 207 //投屏成功
        const val EVENT_DEVICE_DISPLAY_SERVICE_PLAY_FAILED = 208 //投屏失败
        const val  EVENT_DEVICE_SERVICE_VIRMODEM_CALLING = 209 // Modem通话接听中
        const val EVENT_DEVICE_SERVICE_VIRMODEM_HANG_UP = 210 //  Modem通话挂断
        const val EVENT_DEVICE_SERVICE_VOIP_CALLING = 214 //VoIP通话接听后发送该事件
        const val EVENT_DEVICE_SERVICE_VOIP_HANG_UP = 215 //VoIP通话挂断后发送该事件
    }
}