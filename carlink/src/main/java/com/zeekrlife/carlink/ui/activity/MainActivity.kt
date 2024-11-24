/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */
package com.zeekrlife.carlink.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.graphics.Color
import android.media.AudioAttributes
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.view.SurfaceView
import com.huawei.dmsdpsdk.CustomizedAudioAttributes
import com.zeekrlife.carlink.app.App
import com.zeekrlife.carlink.app.base.BaseActivity
import com.zeekrlife.carlink.data.Const
import com.zeekrlife.carlink.data.PageType
import com.zeekrlife.carlink.data.response.OrderReCharged
import com.zeekrlife.carlink.databinding.ActivityHomeBinding
import com.zeekrlife.carlink.service.HiCarCoreServiceListener
import com.zeekrlife.carlink.ui.viewmodel.HomeViewModel
import com.zeekrlife.net.interception.logging.util.logE

class MainActivity :  BaseActivity<HomeViewModel, ActivityHomeBinding>(){
    private var mSurfaceView: SurfaceView? = null
    private val mConnection = serviceConnection
    private val timer = AdvTimer(FRESH_PIN_INTERVAL, FRESH_DISPLAY_INTERVAL)

    private val mServiceListener: HiCarCoreServiceListener = object : HiCarCoreServiceListener {
        override fun onDeviceChange(key: String?, event: Int, errorcode: Int) {
            Log.i(TAG, "onDeviceChange:$key event:$event errorcode:$errorcode")
            when (event) {
                HiCarCoreServiceListener.EVENT_DEVICE_CONNECT -> onDeviceConnect()
                HiCarCoreServiceListener.EVENT_DEVICE_DISCONNECT -> onDeviceDisconnect()
                HiCarCoreServiceListener.EVENT_DEVICE_PROJECT_CONNECT -> onDeviceProjectConnect()
                HiCarCoreServiceListener.EVENT_DEVICE_PROJECT_DISCONNECT -> onDeviceProjectDisconnect(
                    key
                )

                else -> {}
            }
        }

        override fun onDeviceServiceChange(serviceId: String?, event: Int) {
            when (event) {
                HiCarCoreServiceListener.EVENT_DEVICE_SERVICE_PAUSE -> onDeviceServicePause()
                HiCarCoreServiceListener.EVENT_DEVICE_SERVICE_RESUME -> onDeviceServiceResume()
                HiCarCoreServiceListener.EVENT_DEVICE_SERVICE_START -> onDeviceServiceStart()
                HiCarCoreServiceListener.EVENT_DEVICE_SERVICE_STOP -> onDeviceServiceStop()
                HiCarCoreServiceListener.EVENT_DEVICE_CONNECT_FAILD ->App.eventViewModelInstance.orderReChargedEvent.postValue(OrderReCharged(orderNo= PageType.HICAR_CONNECTION_FAILED.name))
                else -> {}
            }
        }

        override fun onDataReceive(key: String?, dataType: Int, data: ByteArray?) {

        }
        override fun onPinCode(code: String?) {
        }

        override fun onBinderDied() {
            Log.i(TAG, "onBinderDied")
            unbindService(mConnection)
            finish()
        }


    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        Log.i(TAG, "initView")


    }

    override fun initObserver() {
    }
    private fun onCustomizedAudioAttributes(
        usages: IntArray?, contentTypes: IntArray?, focusGain: Int,
        map: Map<String, String>?
    ): CustomizedAudioAttributes {
        val customizedAudioAttributes: CustomizedAudioAttributes
        // 如果usages有多个值，则根据多个usage判断，根据判断之后的usage转换streamType
        val usage = if(usages != null && usages.isNotEmpty())   usages[0] else  AudioAttributes.USAGE_UNKNOWN
        val contentType = usageToContentType(usage)
        customizedAudioAttributes = AudioAttributes.Builder().setUsage(usage)
            .setContentType(contentType).build().run {
                CustomizedAudioAttributes(
                    this,
                    focusGain, map
                )
            }
        Log.i(
            TAG, "getCustomizedAudioAttributes, mCustomizedAudioAttributes:"
                    + customizedAudioAttributes.toString()
        )
        return customizedAudioAttributes
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")
    }

    override fun onResume() {
        Log.i(TAG, "onResume")
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
        unbindService(mConnection)
    }

    override fun finish() {
        super.finish()
        Log.i(TAG, "finish")
    }

    private fun onDeviceConnect() {
        Log.i(TAG, "onDeviceConnect")
        App.eventViewModelInstance.orderReChargedEvent.postValue(OrderReCharged(orderNo= PageType.HICAR_CONNECTING.name))
    }

    private fun onDeviceDisconnect() {
        Log.i(TAG, "onDeviceDisconnect")
        finish()
    }

    private fun onDeviceProjectConnect() {
        Log.i(TAG, "onDeviceProjectConnect")
    }

    private fun onDeviceProjectDisconnect(deviceId: String?) {
        Log.i(TAG, "onDeviceProjectDisconnect")
//        finish()
    }

    private fun onDeviceServicePause() {
        Log.i(TAG, "onDeviceServicePause")
    }

    private fun onDeviceServiceResume() {
        Log.i(TAG, "onDeviceServiceResume")
    }

    private fun onDeviceServiceStart() {
        Log.i(TAG, "onDeviceServiceStart")
    }

    private fun onDeviceServiceStop() {
        Log.i(TAG, "onDeviceServiceStop")
//        finish()
    }

    private val serviceConnection: ServiceConnection
        private get() = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, iBinder: IBinder) {
                Log.i(TAG, "onServiceConnected")
            }

            override fun onServiceDisconnected(name: ComponentName) {
                Log.i(TAG, "onServiceDisconnected")
            }
        }

    internal inner class AdvTimer(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(millisUntilFinished: Long) {

            mBind.connectBtn.setBackgroundColor(Color.parseColor("#B6B6D8"))
            mBind.connectBtn.isClickable = false
            mBind.connectBtn.text = "${millisUntilFinished / 1000}秒后可刷新PIN码"
        }

        override fun onFinish() {
            mBind.connectBtn.text = "刷新PIN码"
            mBind.connectBtn.isClickable = true
            mBind.connectBtn.setBackgroundColor(Color.parseColor("#4EB84A"))
        }
    }


    private fun dp2px(context: Context, dp: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }

    private fun streamType2ContentType(streamType: Int): Int {
        "streamType2ContentType:____$streamType".logE("MainActivity")
        var contentType = Const.CONTENT_TYPE_MUSIC
        when (streamType) {
            Const.STREAM_VOICE_CALL -> contentType = Const.CONTENT_TYPE_SPEECH
            Const.STREAM_SYSTEM_ENFORCED, Const.STREAM_SYSTEM -> contentType =
                Const.CONTENT_TYPE_SONIFICATION

            Const.STREAM_RING -> contentType = Const.CONTENT_TYPE_SONIFICATION
            Const.STREAM_MUSIC -> contentType = Const.CONTENT_TYPE_MUSIC
            Const.STREAM_ALARM -> contentType = Const.CONTENT_TYPE_SONIFICATION
            Const.STREAM_NOTIFICATION -> contentType = Const.CONTENT_TYPE_SONIFICATION
            Const.STREAM_BLUETOOTH_SCO -> contentType = Const.CONTENT_TYPE_SPEECH
            Const.STREAM_DTMF -> contentType = Const.CONTENT_TYPE_SONIFICATION
            Const.STREAM_ACCESSIBILITY -> contentType = Const.CONTENT_TYPE_SPEECH
            else -> Log.e(TAG, "Invalid stream type $streamType")
        }
        return contentType
    }

    companion object {
        private const val TAG = "HiCarDemoActivity"
        private const val FRESH_PIN_INTERVAL = (60 * 1000).toLong()
        private const val FRESH_DISPLAY_INTERVAL: Long = 1000
        private fun usageToStreamType(usage: Int): Int {
            // usage to stream type mapping
            return when (usage) {
                AudioAttributes.USAGE_MEDIA, AudioAttributes.USAGE_GAME, AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE, AudioAttributes.USAGE_ASSISTANT -> Const.STREAM_MUSIC
                AudioAttributes.USAGE_ASSISTANCE_SONIFICATION -> Const.STREAM_SYSTEM
                AudioAttributes.USAGE_VOICE_COMMUNICATION -> Const.STREAM_VOICE_CALL
                AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING -> Const.STREAM_DTMF
                AudioAttributes.USAGE_ALARM -> Const.STREAM_ALARM
                AudioAttributes.USAGE_NOTIFICATION_RINGTONE -> Const.STREAM_RING
                AudioAttributes.USAGE_NOTIFICATION, AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_REQUEST, AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT, AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_DELAYED, AudioAttributes.USAGE_NOTIFICATION_EVENT -> Const.STREAM_NOTIFICATION
                AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY -> Const.STREAM_ACCESSIBILITY
                AudioAttributes.USAGE_UNKNOWN -> Const.STREAM_MUSIC
                else -> Const.STREAM_MUSIC
            }
        }

        private fun usageToContentType(usage: Int):Int {
            return when (usage) {
                AudioAttributes.USAGE_MEDIA,
                AudioAttributes.USAGE_GAME,
                AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE,
                AudioAttributes.USAGE_ASSISTANT -> AudioAttributes.CONTENT_TYPE_MUSIC
                AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE,
                AudioAttributes.USAGE_VOICE_COMMUNICATION,
                AudioAttributes.USAGE_ASSISTANCE_SONIFICATION,
                AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY -> AudioAttributes.CONTENT_TYPE_SPEECH
                AudioAttributes.USAGE_NOTIFICATION_RINGTONE -> AudioAttributes.CONTENT_TYPE_SONIFICATION
                AudioAttributes.USAGE_NOTIFICATION -> AudioAttributes.CONTENT_TYPE_SONIFICATION

                AudioAttributes.USAGE_ASSISTANCE_SONIFICATION -> Const.STREAM_SYSTEM
                AudioAttributes.USAGE_VOICE_COMMUNICATION -> Const.STREAM_VOICE_CALL
                AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING -> Const.STREAM_DTMF
                AudioAttributes.USAGE_ALARM -> Const.STREAM_ALARM
                AudioAttributes.USAGE_NOTIFICATION_RINGTONE -> Const.STREAM_RING
                AudioAttributes.USAGE_NOTIFICATION, AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_REQUEST, AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT, AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_DELAYED, AudioAttributes.USAGE_NOTIFICATION_EVENT -> Const.STREAM_NOTIFICATION
                AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY -> Const.STREAM_ACCESSIBILITY
                AudioAttributes.USAGE_UNKNOWN -> Const.STREAM_MUSIC
                else -> AudioAttributes.CONTENT_TYPE_MUSIC
            }
        }
    }
}