package com.zeekrlife.connect.core.constants

object ConnectCallBackEvent {
    /**
     * onDeviceChange()回调
     */
    const val EVENT_DEVICE_CONNECT = 101  //连接成功
    const val EVENT_DEVICE_DISCONNECT = 102  //断开连接
    const val EVENT_DEVICE_CONNECT_FAILD = 103 //连接失败
    const val EVENT_DEVICE_PROJECT_CONNECT = 104 //投屏连接
    const val EVENT_DEVICE_PROJECT_DISCONNECT = 105 //投屏断开连接
    const val EVENT_DEVICE_ADV_START = 106  //蓝牙广播已开始
    const val EVENT_DEVICE_ADV_STOP = 107 //蓝牙广播停止
    const val EVENT_DEVICE_START_ADV_TIMEOUT = 124 //发现广播发送超时
    const val EVENT_DEVICE_MIC_REQUEST = 109 //请求使用车机MIC
    const val EVENT_DEVICE_MIC_RELEASE = 110 //车机MIC释放通知

    const val EVENT_DEVICE_RECONNECT_FAILED = 114 //自动连接失败

    const val EVENT_DEVICE_CODE_SUCCESS = 121 //链接码获取成功

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

    /**
     * errorCode 错误码
     */
    const val REASON_AUTO_CONNECT_SWITCH_OFF: Int = 2101 //手机侧HiCar App设置界面中“自动连接”未打开。
    const val REASON_CAR_DEVICE_NOT_EXIST = 2102 //手机侧HiCar App设置界面“我的设备”中将该车机连接信息删除。

}