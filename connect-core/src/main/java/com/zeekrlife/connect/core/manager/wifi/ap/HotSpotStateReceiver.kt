package com.zeekrlife.connect.core.manager.wifi.ap

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.huawei.managementsdk.common.LogUtils
import com.zeekrlife.connect.core.BuildConfig

/**
Date:2022/11/21
@author:e-Dongdong.Qiu
 */
class HotSpotStateReceiver(wifiApStateCallback: WifiApStateCallback) : BroadcastReceiver() {

    private var wifiApStateCallback: WifiApStateCallback? = null

    companion object {
        const val TAG = "zzzHotSpotStateReceiver"
    }

    init {
        this.wifiApStateCallback = wifiApStateCallback
    }

    //便携式热点的状态为：10---正在关闭；11---已关闭；12---正在开启；13---已开启
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == "android.net.wifi.WIFI_AP_STATE_CHANGED") {
            val apState = intent.getIntExtra("wifi_state",0)
//            Log.e(TAG, "onReceive:wifiApStateChange state = $apState")
            when (apState) {
                10 -> {
                    wifiApStateCallback?.wifiApClosing()
//                    LogUtils.e(TAG, "AP热点正在关闭")
                }//正在关闭
                11 -> {
                    wifiApStateCallback?.wifiApClosed()
                    LogUtils.e(TAG, "onReceive:wifiApStateChang AP热点已关闭")
                } //已关闭
                12 -> {
                    wifiApStateCallback?.wifiApOpening()
//                    LogUtils.e(TAG, "AP热点正在开启")
                }//正在开启
                13 -> {
                    wifiApStateCallback?.wifiApOpenEd()
                    LogUtils.e(TAG, "onReceive:wifiApStateChang AP热点已开启")
                } //已开启
                14 -> {
                    wifiApStateCallback?.wifiApOpenFailed()
                    LogUtils.e(TAG, "onReceive:wifiApStateChang AP热点开启失败")
                }
            }
        }
    }


}