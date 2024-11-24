package com.zeekrlife.connect.core

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.zeekr.sdk.device.impl.DeviceAPI
import com.zeekrlife.common.ComConstants
import com.zeekrlife.connect.core.app.App
import com.zeekrlife.connect.core.app.eventViewModel
import com.zeekrlife.connect.core.manager.HiCarPropertyManager
import com.zeekrlife.connect.core.utils.VehicleUtil
import com.zeekrlife.net.interception.logging.util.logE

class ConnectService : Service() {
    private val DEFAULT_NOTIFICATION_CHANNEL_ID = "ConnectionService_channel"
    private val DEFAULT_NOTIFICATION_CHANNEL_NAME = "ConnectionService"

    override fun onBind(intent: Intent): IBinder? {
        Log.e("ConnectService onBind", "onBind called")
        return ConnectServiceImpl.instance
    }

    override fun onCreate() {
        super.onCreate()
        HiCarPropertyManager.deleteShortcutList()
        HiCarPropertyManager.notifyHiCarAppListChange()
        Log.e("ConnectService onCreate", "onCreate called")
        DeviceAPI.get().init(App.application){ p0, _->
            "DeviceAPI init : $p0".logE("DeviceApiManager")
            if (p0) {
                val type =  VehicleUtil.getVehicleType()
                "----type : $type".logE("DeviceApiManager")
            }
            ConnectServiceImpl.instance.loadHiCarSdk()
        }

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e("ConnectService bind", "onStartCommand called")
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("ConnectService bind", "onDestroy called")
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }

    private fun serviceForeground() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationChannel = NotificationChannel(
                    DEFAULT_NOTIFICATION_CHANNEL_ID,
                    DEFAULT_NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
                )
                notificationManager.createNotificationChannel(notificationChannel)
                val notification: Notification = NotificationCompat.Builder(
                    this,
                    DEFAULT_NOTIFICATION_CHANNEL_ID
                ).setAutoCancel(true)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setOngoing(true)
                    .setPriority(NotificationManager.IMPORTANCE_LOW)
                    .build()
                startForeground(ComConstants.FOREGROUND_SERVICE_BOOT_RECEIVER, notification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.e("ConnectService onUnbind", "onUnbind called")
        return true
    }

}