package com.zeekrlife.connect.core.data.repository
/**
 *@author Yueming.Zhao
 *2023/5/16
 * mCarAdapter.sendKeyEvent()
 */
object HiCarRequestCode {

    /**
     * 手机app 列表
     */
    const val REQUEST_APP_LIST = 528
    /**
     * 打开手机app
     */
    const val REQUEST_APP_START = 529
    /**
     * 音频元数据
     */
    const val REQUEST_AUDIO_META_DATA = 514
    /**
     * 通话元数据
     */
    const val REQUEST_TEL_META_DATA = 516
    /**
     * 请求使用车机麦克风（在使用之前发起请求）
     */
    const val EVENT_DEVICE_MIC_REQUEST = 109
    /**
     * 接通hiCar通话
     */
    const val KEYCODE_CALL = 5

    /**
     * 释放hiCar呼叫
     */
    const val KEYCODE_ENDCALL = 6

    /**
     * 通话状态
     */
    const val DATA_TYPE_CALL_STATE_FOCUS = 504

    const val CALL_STATE_IDLE = 0 //空闲
    const val CALL_STATE_RINGING = 1//来电
    const val CALL_STATE_OFFHOOK = 2//接通

    /**
     * 挡位信息
     */
    const val DATA_TYPE_SENSOR = 506
    const val DATA_TYPE_META = 514
    const val DATA_TYPE_META_VIDEO = 515
}
