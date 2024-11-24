package com.zeekrlife.connect.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.zeekrlife.connect.core.ConnectService
import com.zeekrlife.net.interception.logging.util.logE

class BootReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "InterConnectionBootReceiver"
    }
    private val ACTION_BOOT = "android.intent.action.BOOT_COMPLETED"
        override fun onReceive(context: Context, intent: Intent) {
            Log.e("zzzBootReceiver", "\"BootReceiver onReceive")
            val action = intent.action
//            if (action != null && action == ACTION_BOOT) {
//                    "ConnectService33 startService".logE("zzzApp")
//                    val serviceIntent = Intent(context, ConnectService::class.java)
//                    context.startService(serviceIntent)
//
//        }

    }

    }