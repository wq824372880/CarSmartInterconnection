package com.zeekrlife.connect.core.manager

import android.content.IntentFilter
import com.zeekrlife.connect.core.app.App
import com.zeekrlife.connect.core.manager.wifi.ap.HotSpotStateReceiver
import com.zeekrlife.connect.core.manager.wifi.ap.WifiApStateCallback

object HiCarServiceManager {
    fun registerHotSpotStateReceiver(callback: ((isOpen: Boolean) -> Unit) = {}){
        val receiver = HotSpotStateReceiver(object : WifiApStateCallback{
            override fun wifiApOpening() {
            }

            override fun wifiApOpenEd() {
                callback.invoke(true)
            }

            override fun wifiApClosing() {
            }

            override fun wifiApClosed() {
                callback.invoke(false)
            }

            override fun wifiApActive(isEmpty: Boolean) {
            }

            override fun wifiApError(isError: Boolean) {
            }

            override fun wifiApOpenFailed() {
            }
        })
        val intentFilter = IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED")
        App.application.registerReceiver(receiver, intentFilter)

    }
}