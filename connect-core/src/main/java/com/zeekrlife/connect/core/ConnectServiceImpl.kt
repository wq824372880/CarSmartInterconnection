package com.zeekrlife.connect.core

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.os.Bundle
import android.os.RemoteCallbackList
import android.os.RemoteException
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.Surface
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.alibaba.fastjson.JSONObject
import com.huawei.dmsdpsdk.CustomizedAudioAttributes
import com.huawei.dmsdpsdk.DMSDPAdapter
import com.huawei.hicarsdk.*
import com.huawei.managementsdk.launcher.AppInfoBean
import com.huawei.managementsdk.launcher.AppInfoChangeListener
import com.zeekr.sdk.vr.callback.ITtsCallback
import com.zeekr.sdk.vr.constant.VrConstant
import com.zeekr.sdk.vr.impl.VrAPI
import com.zeekrlife.aidl.IConnectService
import com.zeekrlife.aidl.IHiCarListener
import com.zeekrlife.common.ext.toJsonStr
import com.zeekrlife.common.ext.toStartActivity
import com.zeekrlife.common.ext.toStartActivityForClearTask
import com.zeekrlife.common.util.GsonUtils
import com.zeekrlife.common.util.SPUtils
import com.zeekrlife.common.util.ToastUtils
import com.zeekrlife.connect.core.adapter.ConnectConfigHelper.Companion.configHelperInstance
import com.zeekrlife.connect.core.app.App.Companion.application
import com.zeekrlife.connect.core.app.InitUtils
import com.zeekrlife.connect.core.app.eventViewModel
import com.zeekrlife.connect.core.constants.ConnectCallBackEvent
import com.zeekrlife.connect.core.constants.SPConstants
import com.zeekrlife.connect.core.data.cache.CacheExt
import com.zeekrlife.connect.core.data.database.AppShortcutBean
import com.zeekrlife.connect.core.data.entity.HiCarMapper
import com.zeekrlife.connect.core.data.repository.HiCarRequestCode
import com.zeekrlife.connect.core.data.repository.UserRepository
import com.zeekrlife.connect.core.data.repository.UserRepository.DATA_TYPE_AA_APP_OPERATION
import com.zeekrlife.connect.core.data.repository.UserRepository.DATA_TYPE_CALL_STATE_FOCUS
import com.zeekrlife.connect.core.data.repository.UserRepository.DATA_TYPE_NAV_FOCUS
import com.zeekrlife.connect.core.data.repository.UserRepository.DATA_TYPE_SCREEN_CAST_AUTHORIZED
import com.zeekrlife.connect.core.data.repository.UserRepository.DATA_TYPE_USERACTIVEDISCONNECT
import com.zeekrlife.connect.core.data.repository.UserRepository.DATA_TYPE_VOICE_STATE
import com.zeekrlife.connect.core.data.request.AppBackgroundParams
import com.zeekrlife.connect.core.data.request.ManualReconnectParams
import com.zeekrlife.connect.core.data.response.AppRespInfo
import com.zeekrlife.connect.core.data.response.LastTrustPhoneInfo
import com.zeekrlife.connect.core.manager.HiCarAppListManager
import com.zeekrlife.connect.core.manager.HiCarPropertyManager
import com.zeekrlife.connect.core.manager.HiCarServiceManager
import com.zeekrlife.connect.core.manager.bluetooth.MyBluetoothManager
import com.zeekrlife.connect.core.manager.bluetooth.constant.UnifyBluetoothDevice
import com.zeekrlife.connect.core.manager.globalsettings.GlobalSetting
import com.zeekrlife.connect.core.manager.wifi.ap.WifiApConnector
import com.zeekrlife.connect.core.ui.activity.ProtocolActivity
import com.zeekrlife.connect.core.ui.activity.SurfaceActivity
import com.zeekrlife.connect.core.utils.BluetoothUtil
import com.zeekrlife.net.interception.logging.util.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList


class ConnectServiceImpl : IConnectService.Stub(), AppInfoChangeListener {

    private val mListeners = RemoteCallbackList<IHiCarListener>()
    private var mHiCarAdapter: HiCarAdapter? = null

    private var mCarConfig: CarConfig? = null
    private val mHiCarListener = HiCarListener()
    private val mHiCarAudioListener = HiCarAudioListener()
    private val myBluetoothManager: MyBluetoothManager by lazy { MyBluetoothManager.getManager() }
    private var mRetryTimes = 0
    private var isReconnect = false  //是否正在发起、进行自动连接
    private var errorCodeForReconnect = 0
    private var mIsProject = false
    private val mutex = Mutex()
    var mHiCarAppList = CopyOnWriteArrayList<AppInfoBean>()
    val appShortcutBeanList = CopyOnWriteArrayList<AppShortcutBean>()
    @Volatile
    var onLoadAllAppList = mutableListOf<AppInfoBean>()
    val startAppResponseMap: ConcurrentHashMap<String, Boolean> by lazy { ConcurrentHashMap<String, Boolean>() }

    @Volatile
    var requestAppTime = 0
    var currentStartApp: String? = null
    val excludedPackageList = listOf(
        "com.huawei.hicar",
        "com.autonavi.minimap",
        "com.huawei.contacts",
        "com.android.contacts",
        "com.huawei.meetime",
        "com.ximalaya.ting.android",
        "com.tencent.qqmusic",
        "cmccwm.mobilemusic",
        "com.shinyv.cnr",
        "com.netease.cloudmusic",
        "com.tencent.mm",
        "com.qiyi.video",
        "tv.danmaku.bili",
        "com.tencent.wework",
        "com.baidu.BaiduMap"
    )

    @Volatile
    private var currentEventType = -1 //记录当前事件

    companion object {
        private const val TAG = "zzzConnectServiceImpl"
        private const val HEADSET_ACTION_CONNECTION_STATE_CHANGED =
            "android.bluetooth.headsetclient.profile.action.CONNECTION_STATE_CHANGED"
        const val A2DP_ACTION_CONNECTION_STATE_CHANGED =
            "android.bluetooth.a2dp-sink.profile.action.CONNECTION_STATE_CHANGED"
        private const val SUCCESS = 0
        private const val FAILED = -1
        private const val TIME_FOR_DELAY_BOOT = 0
        private const val TIME_FOR_ADV_RETRY = 500
        private const val ADV_MAX_RETRY_TIMES = 5

        @Volatile
        var connectedDevice = ""

        @Volatile
        var connectedDeviceAddress = ""

        @JvmStatic
        @get:Synchronized
        val instance by lazy {
            ConnectServiceImpl()
        }
    }

    @Throws(RemoteException::class)
    override fun loadHiCarSdk() {

        if (mHiCarAdapter != null) {
            return
        }
        mCarConfig = configHelperInstance.createBasicCarConfig()

        if (mCarConfig == null) {
            return
        }

        HiCarAdapter.init(application, mCarConfig, object : HiCarInitCallback {
            @SuppressLint("MissingPermission")
            override fun onInitSuccess(hiCarAdapter: HiCarAdapter) {
                "HiCar onInitSuccess".logE(TAG)
                HiCarPropertyManager.deleteShortcutList()
                HiCarPropertyManager.notifyHiCarAppListChange()
                mHiCarAdapter = hiCarAdapter
                isReconnect = false
                errorCodeForReconnect = 0
                currentEventType = -1

                //调用接口使能音频分流策略
                mHiCarAdapter?.enableMultiAudioBusPolicy()
                //setHiCarCallEnable() //通知hicar通话状态可用


//                val audioCustomCode = mHiCarAdapter?.registerCarAudioCustomListener(mHiCarAudioCustomListener)

//                Log.e(TAG,"--audioCustomCode--$audioCustomCode")


                val retCode = mHiCarAdapter?.registerCarListener(mHiCarListener)
                if (retCode != HiCarConst.SUCCESS) {
                    "HiCar reg listener failed".logE(TAG)
                    return
                } else {
                    "HiCar reg listener success".logE(TAG)
                }

                satisfyBluetoothConnectCondition()
            }

            override fun onInitFail(errCode: Int) {
                "HiCar init failed".logE(TAG)
            }

            override fun onBinderDied() {
                "HiCar service bind died".logE(TAG)
                resetStatus()
            }
        })

        openBluetooh()
        openWifi()

        displayConflict()

    }

    @Throws(RemoteException::class)
    override fun unLoadHiCarSdk() {
        if (mHiCarAdapter != null) {
            val carAudioListenerRetCode =
                mHiCarAdapter!!.unRegisterCarAudioListener(mHiCarAudioListener)
            if (carAudioListenerRetCode != HiCarConst.SUCCESS) {
                "HiCar unreg audio listener failed".logE(TAG)
            }
            val carListenerRetCode = mHiCarAdapter!!.unRegisterCarListener(mHiCarListener)
            if (carListenerRetCode != HiCarConst.SUCCESS) {
                "HiCar unreg listener failed".logE(TAG)
            }
            HiCarAppListManager.unRegisterAppInfoChangeListener(this)
            mHiCarAdapter!!.deInit()
            mHiCarAdapter = null
        }
    }


    @Throws(RemoteException::class)
    override fun handleStartAdv() {
        if (isReconnect) return
        if (mHiCarAdapter != null) {
            isReconnect = false
            errorCodeForReconnect = 0
            val retCode = mHiCarAdapter?.startAdv()
            if (retCode == HiCarConst.SUCCESS) {
                "handleStartAdv: start adv success。getAdvPower:${mCarConfig?.advPower}".logE(TAG)
                mRetryTimes = 0
                return
            }
        }
        if (mRetryTimes < ADV_MAX_RETRY_TIMES) {
            mRetryTimes++
            handleStartAdv()
        } else {
            mRetryTimes = 0
            "startAdv failed".logE(TAG)
        }
    }

    @Throws(RemoteException::class)
    override fun handleStoptAdv() {
        mHiCarAdapter?.stopAdv()
    }

    @SuppressLint("MissingPermission")
    @Throws(RemoteException::class)
    override fun startBluetoothRecommend() {
        BluetoothUtil.startBluetoothRecommend(mHiCarAdapter)
    }

    @Throws(RemoteException::class)
    override fun startProjection() {
        if (connectedDevice.isNotEmpty() && !mIsProject) {
            "start Projection".logE(TAG)
            val retCode = mHiCarAdapter?.startProjection()
            if (retCode == HiCarConst.SUCCESS) {
                "start Projection success".logE(TAG)
                mIsProject = true
            }
        }
    }

    @Throws(RemoteException::class)
    override fun pauseProjection() {
        if (connectedDevice.isNotEmpty() && mIsProject) {
            "pause Projection".logE(TAG)
            val retCode = mHiCarAdapter?.pauseProjection()
            if (retCode == HiCarConst.SUCCESS) {
                mIsProject = false
            }
        }
    }

    @Throws(RemoteException::class)
    override fun stopProjection() {
        if (connectedDevice.isNotEmpty() && mIsProject) {
            "pause Projection".logE(TAG)
            val retCode = mHiCarAdapter?.stopProjection()
            if (retCode == HiCarConst.SUCCESS) {
                mIsProject = false
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?) {
        DMSDPAdapter.sendHiSightMotionEvent(event)
    }

    @Throws(RemoteException::class)
    override fun disconnectDevice(deviceId: String) {
        if (currentEventType == 101) {
            mHiCarAdapter?.disconnectDevice(connectedDevice)
            mHiCarAdapter?.stopAdv()
            manualDisconnect()
        }
    }

    override fun updateCarConfig(surface: Surface?, width: Int, height: Int): Boolean {
        "updateCarInfo: width = $width, height = $height".logE(TAG)
        if (mCarConfig == null) {
            "updateCarInfo: mCarConfig is null".logE(TAG)
            return false
        }
        mCarConfig?.updateSurface(surface)
        mCarConfig?.updateVideoWidth(width)
        mCarConfig?.updateVideoHeight(height)
        //更新是否支持自动连接功能
        mCarConfig?.updateSupportReconnect(true)
        if (mHiCarAdapter == null) {
            "updateCarInfo: mHiCarAdapter is null".logE(TAG)
            return false
        }
        return mHiCarAdapter?.updateCarConfig(mCarConfig,false) == HiCarConst.SUCCESS
    }

    fun getCarConfig(): CarConfig? {
        return mCarConfig
    }


    @Throws(RemoteException::class)
    override fun registerHiCarListener(callback: IHiCarListener?): Boolean {
        if (callback == null) {
            return false
        }
        return mListeners.register(callback)
    }

    @Throws(RemoteException::class)
    override fun unregisterHiCarListener(callback: IHiCarListener?): Boolean {
        if (callback == null) {
            return false
        }
        return mListeners.unregister(callback)
    }

    //发送报文到hicar 做相关数据通信
    override fun sendHiCarData(byteData: ByteArray?, requestCode: Int) {
        val resultCode = mHiCarAdapter?.sendCarData(requestCode, byteData)
        Log.e(TAG, "---sendHiCarData_resultCode: $resultCode , from requestCode: $requestCode")
    }


    inner class HiCarListener : CarListener {
        override fun onDeviceChange(deviceId: String?, event: Int, errorcode: Int) {
            "onDeviceChange: key:$deviceId,event:$event.errorCode:$errorcode".logE(TAG)

            MainScope().launch(Dispatchers.Main) {
                when (event) {
                    ConnectCallBackEvent.EVENT_DEVICE_CONNECT -> {
                        "buildTime:  ---->${BuildConfig.BuildTime}".logE(TAG)
                        currentEventType = event
                        requestAppTime = 0
                        mIsProject = false
                        connectedDevice = deviceId ?: ""
                        mHiCarAppList.clear()
                        onLoadAllAppList.clear()
                        currentStartApp = null

                        HiCarAppListManager.unRegisterAppInfoChangeListener(this@ConnectServiceImpl)
                        UserRepository.requestHiCarAppList(mHiCarAdapter)

                        MainScope().launch(Dispatchers.Default) {
                            globalConnected()
                            initVrAndPolicy()
                            mHiCarAdapter?.enableMultiAudioBusPolicy()
                            mHiCarAdapter?.registerCarAudioListener(mHiCarAudioListener)
                            delay(2000)
                            if (requestAppTime == 0) {
                                toStartActivityForClearTask(SurfaceActivity::class.java, Bundle())
                                requestAppTime = 2
                                requestAppList()
                            }
                        }

                    }


                    ConnectCallBackEvent.EVENT_DEVICE_DISCONNECT -> {

                        if (currentEventType == ConnectCallBackEvent.EVENT_DEVICE_CONNECT) {
                            ToastUtils.show(application, "HUAWEI HiCar 连接已断开")
                        }
                        currentEventType = event
                        isReconnect = false
                        mIsProject = false
                        if (connectedDevice == deviceId) {
                            connectedDevice = ""
                        } else {
                            "onDeviceChange: disconnect diff device".logE(TAG)
                        }
                        mHiCarAppList.clear()
                        onLoadAllAppList.clear()
                        currentStartApp = null
                        eventViewModel.finishActivityEvent.postValue(true)

                        HiCarPropertyManager.deleteShortcutList()
                        HiCarPropertyManager.notifyHiCarAppListChange()
                        HiCarAppListManager.unRegisterAppInfoChangeListener(this@ConnectServiceImpl)
                        HiCarAppListManager.releaseAppTransManager()


                        SPUtils.getInstance().put(SPConstants.DEVICE_NAME, "")
                        closeECNR()

                        globalDisconnected()

                    }

                    ConnectCallBackEvent.EVENT_DEVICE_CONNECT_FAILD -> {
                        currentEventType = event
                        isReconnect = false
                        mIsProject = false
                        connectedDevice = ""
                        mHiCarAppList.clear()
                        onLoadAllAppList.clear()
                        currentStartApp = null
                        eventViewModel.finishActivityEvent.postValue(true)
                        ToastUtils.show(
                            application,
                            "HUAWEI HiCar连接失败，请重新进行连接",
                            Toast.LENGTH_LONG
                        )
                        showToastForConnectFail()

                        globalDisconnected()
                    }

                    ConnectCallBackEvent.EVENT_DEVICE_RECONNECT_FAILED -> {
                        if (errorCodeForReconnect == ConnectCallBackEvent.REASON_AUTO_CONNECT_SWITCH_OFF)
                            return@launch
                        if (errorcode == ConnectCallBackEvent.REASON_AUTO_CONNECT_SWITCH_OFF) {
                            errorCodeForReconnect =
                                ConnectCallBackEvent.REASON_AUTO_CONNECT_SWITCH_OFF
                        } else if (errorcode == ConnectCallBackEvent.REASON_CAR_DEVICE_NOT_EXIST) {
                            manualDisconnect()
                        }
                    }


                    ConnectCallBackEvent.EVENT_DEVICE_PROJECT_CONNECT -> {

                    }

                    ConnectCallBackEvent.EVENT_DEVICE_PROJECT_DISCONNECT -> {

                    }

                    ConnectCallBackEvent.EVENT_DEVICE_ADV_START -> {

                    }

                    ConnectCallBackEvent.EVENT_DEVICE_ADV_STOP -> {

                    }

                    ConnectCallBackEvent.EVENT_DEVICE_START_ADV_TIMEOUT -> {
//                            mRetryTimes = 0
//                            handleStartAdv()
                    }


                    ConnectCallBackEvent.EVENT_DEVICE_MIC_REQUEST -> {

                    }

                    ConnectCallBackEvent.EVENT_DEVICE_MIC_RELEASE -> {

                    }

                }
            }

            MainScope().launch(Dispatchers.Default) {
                mutex.withLock {
                    try {
                        val len: Int = mListeners.beginBroadcast()
                        if (len > 0) {
                            for (i in 0 until len) {
                                val callback: IHiCarListener = mListeners.getBroadcastItem(i)
                                try {
                                    callback.onDeviceChange(deviceId, event, errorcode)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    } finally {
                        try {
                            mListeners.finishBroadcast()
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }
                    }
                }
            }

        }

        override fun onDeviceServiceChange(serviceId: String, event: Int) {
            "onDeviceServiceChange event:$event ,serviceId = $serviceId".logE(TAG)
            MainScope().launch(Dispatchers.Main) {
                when (event) {
                    ConnectCallBackEvent.EVENT_DEVICE_SERVICE_RESUME -> {
//                        val launchIntent: Intent? =
//                            application.packageManager.getLaunchIntentForPackage(application.packageName)
//                        if (launchIntent != null) {
//                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            application.startActivity(launchIntent)
//                        }

                    }

                    ConnectCallBackEvent.EVENT_DEVICE_SERVICE_START -> {

                    }

                    ConnectCallBackEvent.EVENT_DEVICE_SERVICE_PAUSE -> {

                    }

                    ConnectCallBackEvent.EVENT_DEVICE_SERVICE_STOP -> {
                        "投屏停止 $serviceId".logE(TAG)
//                        mHiCarListener.onDeviceChange(
//                            serviceId,
//                            ConnectCallBackEvent.EVENT_DEVICE_DISCONNECT,
//                            0
//                        )
                        mHiCarAdapter?.disconnectDevice(serviceId)

                    }

                    ConnectCallBackEvent.EVENT_DEVICE_DISPLAY_SERVICE_PLAYING -> {
                        "投屏成功 $serviceId".logE(TAG)

                    }

                    ConnectCallBackEvent.EVENT_DEVICE_DISPLAY_SERVICE_PLAY_FAILED -> {
                        "投屏失败 $serviceId".logE(TAG)
//                        mHiCarListener.onDeviceChange(
//                            serviceId,
//                            ConnectCallBackEvent.EVENT_DEVICE_DISCONNECT,
//                            0
//                        )
                        mHiCarAdapter?.disconnectDevice(serviceId)
                    }

                }
            }
            MainScope().launch(Dispatchers.Default) {
                mutex.withLock {
                    try {
                        val len: Int = mListeners.beginBroadcast()
                        if (len > 0) {
                            for (i in 0 until len) {
                                val callback: IHiCarListener = mListeners.getBroadcastItem(i)
                                try {
                                    callback.onDeviceServiceChange(serviceId, event)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    } finally {
                        try {
                            mListeners.finishBroadcast()
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }
                    }

                }
            }

        }

        override fun onDataReceive(key: String, dataType: Int, data: ByteArray) {
            if (dataType == 528 || dataType == 531) {
                "onDataReceive dataType = $dataType".logE(TAG)
            } else {
                "onDataReceive dataType = $dataType,data:${GsonUtils.toJson(String(data))}".logE(
                    TAG
                )
            }

            when (dataType) {
                DATA_TYPE_VOICE_STATE -> {
                    //根据语音状态的空闲和打开 同步speech的状态  语音开->speech关  语音关->speech开
//                    syncAISpeechState(data)
                    closeHiCarVoice(data)
                }

                DATA_TYPE_CALL_STATE_FOCUS -> {

                    saveCallState(data)
                }

                DATA_TYPE_NAV_FOCUS -> {
                    hicarNavStatus(data)
                }

                DATA_TYPE_USERACTIVEDISCONNECT -> {//手动断开连接，后续不发起自动回连
                    manualDisconnect(data)
                }

                DATA_TYPE_AA_APP_OPERATION -> {
                    //监听回调给surfaceactivity
                    startAppResponse(data)
                }

                DATA_TYPE_SCREEN_CAST_AUTHORIZED -> {
                    updateBackground(data)
                }


            }

            MainScope().launch(Dispatchers.Default) {

                mutex.withLock {
                    try {
                        val len: Int = mListeners.beginBroadcast()

                        if (len > 0) {
                            for (i in 0 until len) {
                                val callback: IHiCarListener = mListeners.getBroadcastItem(i)
                                try {
                                    callback.onDataReceive(key, dataType, data)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    } finally {
                        try {
                            mListeners.finishBroadcast()
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        override fun onPinCode(code: String) {
            MainScope().launch(Dispatchers.Default) {
                mutex.withLock {
                    try {
                        val len: Int = mListeners.beginBroadcast()
                        "onPinCode:: len = $len".logE(TAG)
                        if (len > 0) {
                            for (i in 0 until len) {
                                val callback: IHiCarListener = mListeners.getBroadcastItem(i)
                                try {
                                    currentEventType =
                                        ConnectCallBackEvent.EVENT_DEVICE_CODE_SUCCESS
                                    callback.onPinCode(code)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    } finally {
                        try {
                            mListeners.finishBroadcast()
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        override fun getSecureFileSize(fileName: String): Long {
            try {
                application.openFileInput(fileName).use { fis ->
                    return fis.available()
                        .toLong()
                }
            } catch (e: IOException) {
//                "read file IOException".logE(TAG)
            }
            return FAILED.toLong()
        }

        override fun readSecureFile(fileName: String): ByteArray? {
//                "readSecureFile: fileName = $fileName".logE(TAG)
            var readFileData: ByteArray? = null
            var fis: FileInputStream? = null
            var result = false
            try {
                fis = application.openFileInput(fileName)
                val temp = ByteArray(1024)
                var len = fis.read(temp)
                // read file
                while (len != FAILED) {
                    if (readFileData != null && readFileData.isNotEmpty()) {
                        readFileData = byteMerger(readFileData, temp, len)
                    } else {
                        readFileData = ByteArray(0)
                        readFileData = byteMerger(readFileData, temp, len)
                    }
                    len = fis.read(temp)
                }
                result = true
//                "read secure file".logE(TAG)
            } catch (e: IOException) {
//                "read file IOException".logE(TAG)
            } finally {
                try {
                    fis?.close()
                } catch (e: IOException) {
//                    "close file IOException".logE(TAG)
                }
            }
            if (!result) {
                readFileData = null
            }
            return readFileData
        }

        override fun writeSecureFile(fileName: String, data: ByteArray): Boolean {
//                "write secure file:$fileName".logE(TAG)
            var fos: FileOutputStream? = null
            var retCode = false
            try {
                fos = application.openFileOutput(fileName, Context.MODE_PRIVATE)
                fos.write(data)
                fos.flush()
                retCode = true
            } catch (e: FileNotFoundException) {
//                "write file FileNotFoundException".logE(TAG)
            } catch (e: IOException) {
//                "write file IOException".logE(TAG)
            } finally {
                try {
                    fos?.close()
                } catch (e: IOException) {
//                    "close file IOException".logE(TAG)
                }
            }
            return retCode
        }

        override fun removeSecureFile(fileName: String): Boolean {
            return false
        }

        override fun onShowStartPage() {
            MainScope().launch(Dispatchers.Default) {
                mutex.withLock {
                    try {
                        val len: Int = mListeners.beginBroadcast()
                        "onPinCode:: len = $len".logE(TAG)
                        if (len > 0) {
                            for (i in 0 until len) {
                                val callback: IHiCarListener = mListeners.getBroadcastItem(i)
                                try {
                                    callback.onShowStartPage()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    } finally {
                        try {
                            mListeners.finishBroadcast()
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            "onShowStartPage".logE(TAG)
            MainScope().launch(Dispatchers.Main) {
                delay(500)
                val intent = Intent()
                intent.setClassName(
                    "com.zeekrlife.hicar",
                    "com.zeekrlife.hicar.ui.activity.MainActivity"
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                application.startActivity(intent)
            }
        }

        override fun syncPhoneTime(p0: Long) {

        }

        private fun byteMerger(byteA: ByteArray, byteB: ByteArray, byteLength: Int): ByteArray {
            val result = ByteArray(byteA.size + byteLength)
            System.arraycopy(byteA, 0, result, 0, byteA.size)
            System.arraycopy(byteB, 0, result, byteA.size, byteLength)
            return result
        }
    }

    private var isCalling = false
    private fun saveCallState(data: ByteArray) {
        val str = GsonUtils.toJson(String(data))
        val indexOf = str.indexOf(":")
        if (indexOf < 0) {
            return
        }
        val value = str.substring(indexOf + 1, indexOf + 2)
        isCalling = value == "1"
        if (isCalling) {
            eventViewModel.finishActivityEvent.postValue(true)
        }
    }

    private fun closeHiCarVoice(data: ByteArray) {
        val str = GsonUtils.toJson(String(data))
        val indexOf = str.indexOf(":")
        if (indexOf < 0) {
            return
        }
        val value = str.substring(indexOf + 1, indexOf + 2)
        Log.e(TAG, "-----close Value: $value")
        if ("1" == value) {

            if (!isCalling) {
                VrAPI.get().voiceApi.playTTS(
                    "暂不支持Hicar的手机语音功能",
                    "TtsID_Hicar",
                    VrConstant.TTS_PRIORITY.IMPORTANT_REALTIME,
                    object : ITtsCallback.Stub() {
                        override fun onEnd(p0: String?) {

                        }

                        override fun onError(p0: String?, p1: String?) {
                        }
                    })
            }

            val closeStr = "{\n" +
                    "    \"frontEndInfo\": {\n" +
                    "        \"car\": {\n" +
                    "            \"micNumber\": 1,\n" +
                    "            \"speakerNumber\": 1\n" +
                    "        },\n" +
                    "        \"centralControl\": {\n" +
                    "            \"vendorName\": \"XX\",\n" +
                    "            \"version\": \"10.0.0.12\"\n" +
                    "        },\n" +
                    "        \"intent\": \"stopVoiceRecognize\",\n" +
                    "        \"voice\": {\n" +
                    "            \"infoToVendorCloud\": \"HicarManager\",\n" +
                    "            \"vendorName\": \"10.0.0.12\",\n" +
                    "            \"version\": \"12\"\n" +
                    "        },\n" +
                    "        \"wakeType\": \"buttonPress\"\n" +
                    "    }\n" +
                    "}"
            val closeResult = mHiCarAdapter?.sendHotWord(closeStr)
            Log.e(TAG, "---closeResult:  $closeResult")

        }
    }

    private fun closeECNR() {
        //关闭hal层 ecnr
//            val audioManager: AudioManager = application.getSystemService<AudioManager>(
//                AudioManager::class.java
//            )
//            audioManager.setParameters("ecnr_swicth=off")

    }

    private fun openECNR() {
        //开启hal层 ecnr
//            val audioManager: AudioManager = application.getSystemService<AudioManager>(
//                AudioManager::class.java
//            )
//            audioManager.setParameters("ecnr_swicth=on")
    }

    /**
     * 请求应用列表
     */
    override fun requestAppList() {
        HiCarAppListManager.unRegisterAppInfoChangeListener(this@ConnectServiceImpl)
        HiCarAppListManager.registerAppInfoChangeListener(this@ConnectServiceImpl)
        UserRepository.requestHiCarAppList(mHiCarAdapter)
        DayNightManager.init(application)

        MainScope().launch(Dispatchers.IO) {
            mHiCarAdapter?.let {
                delay(2000)
                if (requestAppTime == 2 && onLoadAllAppList.isEmpty() && currentEventType == ConnectCallBackEvent.EVENT_DEVICE_CONNECT) {//2s后仍无applist,说明应该是首次连接
                    "requestAppList22,$requestAppTime".logE(TAG)
                    HiCarAppListManager.unRegisterAppInfoChangeListener(this@ConnectServiceImpl)
                    HiCarAppListManager.registerAppInfoChangeListener(this@ConnectServiceImpl)
                    UserRepository.requestHiCarAppList(mHiCarAdapter)
                    delay(2000)
                    if(requestAppTime == 2 && onLoadAllAppList.isEmpty() && currentEventType == ConnectCallBackEvent.EVENT_DEVICE_CONNECT){ //2s后仍无applist,说明华为bug触发 首次连接但532并无响应
                        toStartActivityForClearTask(ProtocolActivity::class.java, Bundle())
                    }
                }
            }
        }

    }

    /**
     * 发送通话状态可用到hiCar
     */
    private fun setHiCarCallDisable() {
        val jsonObject = JSONObject()
        jsonObject["service"] = "virtualModem"
        jsonObject["subService"] = "capability"
        jsonObject["command"] = "1"
        jsonObject["errorCode"] = "100003"
        val data = jsonObject.toJSONString()
        Log.e("setHiCarCallDisable", "---test disable modem data--$data")
        sendHiCarData(
            data.toByteArray(StandardCharsets.UTF_8),
            UserRepository.DATA_TYPE_SUPPORT_VIRTUAL_MODEM
        )
    }

    override fun sendKeyEvent(keyEvent: Int, action: Int) {
        mHiCarAdapter?.sendKeyEvent(keyEvent, action)
    }

    override fun updateDimInfo() {
        HiCarDimManager.getInstance(application).updateDimInfo()
    }

    override fun openBluetooh() {
        if (ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            "openBluetooh disable".logE(TAG)
            return
        }
        "openBluetooh enable".logE(TAG)
        BluetoothUtil.openBluetooth()

        HiCarServiceManager.registerHotSpotStateReceiver()
    }

    override fun getBluetoothState(): Boolean {

        return myBluetoothManager.isBtOpened
    }

    override fun getWifiState(): Boolean {
        return (WifiApConnector.getInstance()?.isWifiApOn()) ?: false
    }


    override fun openWifi() {
        if (ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        "openWifi enable".logE(TAG)
        WifiApConnector.getInstance()?.mPassphraseAppend = ""
        WifiApConnector.getInstance()?.startTethering()
//        WifiServiceManager.getInstance().WLANMode(
//            WifiServiceManager.WIFI_ON
//        )
//        WifiServiceManager.getInstance().WLANMode(
//            WifiServiceManager.AP_ON
//        )
    }

    override fun closeWifi() {
        if (ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        "closeWifi enable".logE(TAG)
        WifiApConnector.getInstance()?.stopTethering()
//        WifiServiceManager.getInstance().WLANMode(
//            WifiServiceManager.NET_OFF
//        )
    }

    override fun modifyAPPassWord(password: String?) {
        WifiApConnector.getInstance()?.mPassphraseAppend =
            if ((WifiApConnector.getInstance()?.mPassphrase?.length
                    ?: 0) < 1
            ) {
                "123456780"
            } else {
                WifiApConnector.getInstance()?.mPassphrase + "0"
            }
        WifiApConnector.getInstance()?.startWifiAp(
            WifiApConnector.getInstance()?.mSsid ?: "ZEEKR-0001",
            (WifiApConnector.getInstance()?.mPassphraseAppend) ?: "123456780"
        )
    }

    /**
     * 打开hicar app
     */
    override fun requestStartApp(packageName: String) {
        mHiCarAdapter?.let {
            DayNightManager.init(application)
            UserRepository.requestStartApp(mHiCarAdapter, packageName)
        }
    }

    @Synchronized
    override fun onLoadAllAppInfo(deviceId: String?, list: MutableList<AppInfoBean>?) {
        "onLoadAllAppInfo list222size:${list?.size}".logE(TAG)
//        onLoadAllAppList = list ?: mutableListOf()
        if (requestAppTime < 2) {
            return
        } else {
            MainScope().launch(Dispatchers.Default) {

                mutex.withLock {
                    try {
                        val len: Int = mListeners.beginBroadcast()
                        if (len > 0) {
                            for (i in 0 until len) {
                                val callback: IHiCarListener = mListeners.getBroadcastItem(i)
                                try {
                                    "zzzConnectServiceImpl onHiCarApplistChange:${list?.size}".logE(
                                        TAG
                                    )
                                    callback.onHiCarApplistChange(
                                        HiCarMapper.aidlToEntityList(
                                            mHiCarAppList
                                        )
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    } finally {
                        try {
                            mListeners.finishBroadcast()
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            MainScope().launch(Dispatchers.IO) {
                "onLoadAllAppInfo list2233,onLoadAllAppList:${onLoadAllAppList.size}".logE(TAG)
                if (onLoadAllAppList.isNotEmpty()) {
                    return@launch
                } else {
                    onLoadAllAppList = list ?: mutableListOf()
                    mHiCarAppList.clear()

                    val listFilter = list?.filter { appInfo ->
                        !excludedPackageList.any { excludedPackage ->
                            excludedPackage.contentEquals(appInfo.pkgName)

                        }
                    }

                    listFilter?.let { filters ->
                        mHiCarAppList.addAll(filters)
                        callBackHICarAppListChange(true)
                    }
                }

                CacheExt.setHiCarManualReconnect(ManualReconnectParams(0))
                val trustPhoneInfo = mHiCarAdapter?.trustDeviceList?.find { it.phoneId == deviceId }
                trustPhoneInfo?.let {
                    CacheExt.setHiCarLastTrustPhoneInfo(
                        LastTrustPhoneInfo(
                            mPhoneId = it.phoneId,
                            mPhoneName = it.phoneName,
                            mPhoneBrMac = it.phoneBrMac,
                            mLastConnectTime = it.lastConnectTime
                        )
                    )
                }

            }

            initBasicProperty()
        }


    }

    @Synchronized
    override fun onAppInfoAdd(deviceId: String?, list: MutableList<AppInfoBean>?) {
        MainScope().launch(Dispatchers.IO) {
            delay(500)
            "onAppInfoAdd list2222:${list?.size} ".logE(TAG)

            val listFilter = list?.filter { appInfo ->
                !excludedPackageList.any { excludedPackage ->
                    excludedPackage.contentEquals(appInfo.pkgName)
                }
            }
            val addPackageNameList = listFilter?.asSequence()?.map { it.pkgName }?.toSet()
            val allPackageNameList = onLoadAllAppList.asSequence().map { it.pkgName }.toSet()
            val intersect = addPackageNameList?.intersect(allPackageNameList)
            "onAppInfoAdd intersect:${intersect}".logE(TAG)
            if (!intersect.isNullOrEmpty()) {
                return@launch
            }

            listFilter?.let {
                mHiCarAppList.addAll(it)
                callBackHICarAppListChange(false)
            }
        }

    }

    override fun onAppInfoRemove(deviceId: String?, list: MutableList<AppInfoBean>?) {
        "onAppInfoRemove list222:${list.toJsonStr()} ".logE(TAG)
        MainScope().launch(Dispatchers.IO) {
            val listFilter = list?.filter { appInfo ->
                !excludedPackageList.any { excludedPackage ->
                    excludedPackage.contentEquals(appInfo.pkgName)
                }
            }
            listFilter?.let {
                it.takeIf {
                    it.isNotEmpty()
                }?.forEach {
                    mHiCarAppList.removeIf { hicarList ->
                        it.pkgName!!.contentEquals(hicarList.pkgName)
                    }
                }

                callBackHICarAppListChange(false)
            }
        }
    }

    override fun onAppInfoUpdate(deviceId: String?, list: MutableList<AppInfoBean>?) {
//        MainScope().launch(Dispatchers.IO) {
//            "onAppInfoUpdate list2222:${list?.size} ".logE(TAG)
//            delay(2000)
//            val listFilter = list?.filter { appInfo ->
//                !excludedPackageList.any { excludedPackage ->
//                    excludedPackage.contentEquals(appInfo.pkgName)
//                }
//            }
//
//            val allPackageNameList = mHiCarAppList.asSequence().map { it.pkgName }.toSet()
//            listFilter?.forEach {
//                if(!allPackageNameList.contains(it.pkgName)){
//                    mHiCarAppList.add(it)
//                }
//            }
//            callBackHICarAppListChange(false)
//            "onAppInfoUpdate union:${mHiCarAppList.size}".logE(TAG)
//        }

    }

    /**
     * 获取手机型号
     */
    override fun getPhoneName(): String {
        val list = mHiCarAdapter?.trustDeviceList
        "getPhoneName: ${list?.toJsonStr()}".logE(TAG)
        val phoneId = list?.find { connectedDevice.contentEquals(it.phoneId) }
        return phoneId?.phoneName ?: (list?.firstOrNull()?.phoneName ?: "")
    }

    override fun requestBackground(isBackground: Boolean) {
        mHiCarAdapter?.let {
            UserRepository.requestBackground(it, isBackground)
        }
    }

    override fun startReconnect(mac: String?) {
        isReconnect = true
        mHiCarAdapter!!.startReconnect(mac ?: "")

    }

    override fun finishActivity(isBack: Boolean) {
        eventViewModel.finishActivityEvent.postValue(isBack)
    }

    override fun moveTaskToBack(isBack: Boolean) {
        eventViewModel.moveTaskToBack.postValue(isBack)
    }

    override fun deleteApplist() {
        HiCarPropertyManager.deleteShortcutList()
        HiCarPropertyManager.notifyHiCarAppListChange()
    }

    override fun manualReconnect() {
        manualDisconnect()
    }

    override fun currentCastType(): Int {
        return InitUtils.globalSettingCurrentCastType
    }

    override fun setAdvPower(power: Int) {
        mCarConfig?.updateAdvPower(power)
    }

    override fun disconnectCarlink() {
        // hicar的连接冲突关联这里  后续执行 断开carLink的操作
        Log.e(TAG, "----disconnectCarlink: ")
    }

    override fun getCurrentEventType(): Int {
        "getCurrentEventType: getCurrentEventType:$currentEventType".logE(TAG)
        return currentEventType
    }

    override fun setCurrentEventType(type: Int) {
        currentEventType = type
        if (currentEventType == ConnectCallBackEvent.EVENT_DEVICE_DISCONNECT) {
            mHiCarAdapter?.disconnectDevice(connectedDevice)
            mHiCarAdapter?.stopAdv()
        }

    }

    fun getConnectedDeviceAddress(): String {
        return connectedDeviceAddress.ifEmpty {
            mHiCarAdapter?.trustDeviceList?.find { it.phoneId == connectedDevice }?.phoneBrMac ?: ""
        }

    }


    /**
     *  car audio listener
     */
    inner class HiCarAudioListener : CarAudioListener() {
        override fun getCustomizedAudioAttributes(
            usages: IntArray?, contentType: IntArray?,
            focusGain: Int, info: Map<String, String>?
        ): CustomizedAudioAttributes? {


            var customizedAudioAttributes: CustomizedAudioAttributes? = null

            if (focusGain == 0) {
                return null
            }

            paramPrint(usages, contentType, focusGain, info)


            // 如果usages有多个值，则根据多个usage判断，根据判断之后的usage转换streamType
            usages?.let {
                var usage = if (it.isNotEmpty()) it[0] else AudioAttributes.USAGE_UNKNOWN

                if (usage == 17) {
                    usage = AudioAttributes.USAGE_ASSISTANT;
                }

                if (usage == AudioAttributes.USAGE_UNKNOWN
                    ||usage == AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY
                    || usage == AudioAttributes.USAGE_ASSISTANCE_SONIFICATION
                ) {
                    return null
                }

                if (usage == AudioAttributes.USAGE_ALARM
                    || usage == AudioAttributes.USAGE_NOTIFICATION_EVENT
                    || usage == AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_DELAYED) {
                    usage = AudioAttributes.USAGE_NOTIFICATION
                }

                val convertContentType = usageToContentType(usage)

                customizedAudioAttributes = AudioAttributes.Builder().setUsage(usage)
                    .setContentType(convertContentType).build().run {
                        CustomizedAudioAttributes(
                            this,
                            focusGain, info
                        )
                    }
            }
            return customizedAudioAttributes
        }

    }

    private fun paramPrint(
        usages: IntArray?,
        contentType: IntArray?,
        focusGain: Int,
        info: Map<String, String>?
    ) {

        var content = "usage :"
        if (usages == null) {
            content += "null;"
        } else {
            for (usage in usages) {
                content += "$usage "
            }
            content += ";"
        }

        content += "contentType: "
        if (contentType == null) {
            content += " null;"
        } else {
            for (i in contentType) {
                content += "$i "
            }
            content += ";"
        }
        content += "focusGain: $focusGain"

        "getCustomizedAudioAttributes: $content".logE(TAG)

    }


    private fun usageToContentType(usage: Int): Int {

        return when (usage) {
            //多媒体 1
            AudioAttributes.USAGE_MEDIA,
            AudioAttributes.USAGE_GAME,
            AudioAttributes.USAGE_ASSISTANT -> AudioAttributes.CONTENT_TYPE_MUSIC


            //speech 2
            AudioAttributes.USAGE_VOICE_COMMUNICATION,
            AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY -> AudioAttributes.CONTENT_TYPE_SPEECH

            //铃声
            AudioAttributes.USAGE_NOTIFICATION_RINGTONE,
            AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_REQUEST,
            AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT,
            AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_DELAYED,
            AudioAttributes.USAGE_NOTIFICATION_EVENT,
            AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE,
            AudioAttributes.USAGE_ASSISTANCE_SONIFICATION,
            AudioAttributes.USAGE_NOTIFICATION,
            AudioAttributes.USAGE_ALARM,
            AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING -> AudioAttributes.CONTENT_TYPE_SONIFICATION


            else -> AudioAttributes.CONTENT_TYPE_MUSIC
        }
    }

    private suspend fun callBackHICarAppListChange(showToast: Boolean) {
        HiCarPropertyManager.deleteShortcutList()
        appShortcutBeanList.clear()
        appShortcutBeanList.addAll(mHiCarAppList.map {
            AppShortcutBean(
                mPackageName = it.pkgName,
                mName = it.name.replace(" HUAWEI HiCar", ""),
                mIcon = it.icon,
                mType = it.type
            )
        })
        HiCarPropertyManager.addShortcutList(appShortcutBeanList)
        "onHiCarApplistChange list444:${GsonUtils.toJson(appShortcutBeanList.size)}".logE(TAG)
        HiCarPropertyManager.notifyHiCarAppListChange()
        if (showToast) {
            MainScope().launch(Dispatchers.Main) {
                delay(1000)
                ToastUtils.show(
                    application, "HUAWEI HiCar连接成功",
                    R.drawable.ic_toast_success, Toast.LENGTH_LONG
                )
                eventViewModel.finishActivityEvent.postValue(true)
                if (!isReconnect) {
                    delay(1000)
                    val intent = Intent()
                    intent.setClassName("ecarx.launcher3", "ecarx.launcher3.AppCenterActivity")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    application.startActivity(intent)
                }
            }
        }


    }

    /**
     * 手动断开连接 后续不发起自动连接
     */
    fun manualDisconnect(data: ByteArray? = null) {
//        var manualReconnect = GsonUtils.fromJson(GsonUtils.toJson(String(data)),ManualReconnectParams::class.java)
//        if(manualReconnect.isUserDisconnect == 1)
        CacheExt.setHiCarManualReconnect(ManualReconnectParams(1))
        UserRepository.requestManualReconnect(mHiCarAdapter)

    }

    /**
     * 打开app回调
     */
    fun startAppResponse(data: ByteArray) {
        val startAppResponse = String(data)
        startAppResponse.let { it ->
            val result = GsonUtils.fromJson(it, AppRespInfo::class.java)
            result?.RequestAppResp?.AppPackage?.let {
                startAppResponseMap.put(it, true)
            }
//            if("success".contentEquals(result.RequestAppResp?.Description)){
            eventViewModel.startAppSuccess.postValue(true)
//            }
        }
    }

    /**
     * dateType = 532 数据处理
     */
    fun updateBackground(data: ByteArray) {
        var authorized = GsonUtils.toJson(String(data))
        authorized = if (authorized.startsWith("\"") && authorized.endsWith("\"")) {
            authorized.substring(1, authorized.length - 1)
        } else {
            authorized
        }
        "authorized:$authorized".logE(TAG)
        authorized?.let {
            val authBean = GsonUtils.fromJson(
                if (it.contains("\\")) (it.replace(
                    "\\",
                    ""
                )) else it, AppBackgroundParams::class.java
            )
            authBean?.let { bean ->
                "background:${GsonUtils.toJson(bean.ProjectAction?.Background)},isFirstProjection: ${ProtocolActivity.isFirstProjection}".logE(
                    TAG
                )

                if ((bean.ProjectAction?.Background) != true) {
                    requestAppTime = 1
                    toStartActivity(ProtocolActivity::class.java, Bundle())
                    eventViewModel.background.postValue((bean.ProjectAction?.Background) ?: false)
                } else if (bean.ProjectAction.Background && onLoadAllAppList.isEmpty()) {
                    requestAppTime = 2
                    toStartActivityForClearTask(SurfaceActivity::class.java, Bundle())
                    requestAppList()
                    eventViewModel.background.postValue((bean.ProjectAction?.Background) ?: false)
                }


            } ?: it.run {
                toStartActivity(ProtocolActivity::class.java, Bundle())
                eventViewModel.background.postValue(false)
            }
        }

    }

    private fun displayConflict() {
        MainScope().launch(Dispatchers.Main) {
            eventViewModel.castTypeEvent.observeForever {
                if (it != GlobalSetting.FROM_HICAR && currentEventType == ConnectCallBackEvent.EVENT_DEVICE_CONNECT) {
                    mHiCarAdapter?.disconnectDevice(connectedDevice)
                }
            }
        }
    }

    private fun globalConnected() {
        val lastTrustPhoneInfo =
            mHiCarAdapter?.trustDeviceList?.find { it.phoneId == connectedDevice }
        connectedDeviceAddress = (lastTrustPhoneInfo?.phoneBrMac ?: "").toString()
        val bondedDevices = myBluetoothManager.bondedDevices
        if (bondedDevices.isNotEmpty()) {
            val bluetoothDevice: UnifyBluetoothDevice? =
                bondedDevices.find {
                    connectedDeviceAddress == it.mainDevice?.address.toString()
                }
            "it.mainDevice.address.toString():${connectedDeviceAddress}".logE(TAG)
            if (myBluetoothManager.isConnectedA2dp(bluetoothDevice?.mainDevice)) {
                myBluetoothManager.disconnectA2ap(bluetoothDevice?.mainDevice)
            }

        }

        GlobalSetting.hicarConnected(GlobalSetting.FROM_HICAR, connectedDeviceAddress)

        (mHiCarAdapter?.trustDeviceList?.map {
            "phoneName:${it.phoneName},phoneBrMac:${it.phoneBrMac}"
        }.toJsonStr()).logE(TAG)
    }

    private fun globalDisconnected() {
        connectedDeviceAddress = ""
        GlobalSetting.hicarDisconnect()
    }

    private fun resetStatus() {
        MainScope().launch(Dispatchers.Main) {
        "resetStatus:: mHiCarAdapter isNull:${mHiCarAdapter== null},connectedDevice:$connectedDevice".logE(TAG)
        mHiCarAdapter?.disconnectDevice(connectedDevice)
            delay(300)
            manualDisconnect()
            Settings.System.putInt(application.contentResolver, GlobalSetting.CASTTYPE, 0)
            if (currentEventType == ConnectCallBackEvent.EVENT_DEVICE_CONNECT) {
                ToastUtils.show(application, "HUAWEI HiCar 连接已断开")
            }
            currentEventType = ConnectCallBackEvent.EVENT_DEVICE_DISCONNECT
            isReconnect = false
            mIsProject = false
            mHiCarAppList.clear()
            onLoadAllAppList.clear()
            connectedDevice = ""
            eventViewModel.finishActivityEvent.postValue(true)

            HiCarPropertyManager.deleteShortcutList()
            HiCarPropertyManager.notifyHiCarAppListChange()

            SPUtils.getInstance().put(SPConstants.DEVICE_NAME, "")
            closeECNR()
            unLoadHiCarSdk()
            //为了实现杀死依赖的4个进程后重连功能正常
            delay(1000)
            val am = application.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            am.clearApplicationUserData()
            loadHiCarSdk()
        }

    }


    private fun initBasicProperty() {
        setHiCarCallDisable()  // to be test

        SensorManager.init(application) //初始化挡位信息

        DayNightManager.init(application)
    }

    private fun initVrAndPolicy() {
        MainScope().launch(Dispatchers.Main) {
            VrAPI.get().init(
                application
            ) { isReady, message -> Log.e(TAG, "onVrAPIReady=$isReady  message=$message") }
        }
    }

    private fun sendMetaRequest() {
        Log.e(TAG, "----sensorValue:Meta")
        val jsonObject = JSONObject()
        jsonObject["RequestAAData"] = 5
        val data = jsonObject.toJSONString()
        Log.e("zzzConnectServiceImpl", "---data--$data")
        sendHiCarData(
            data.toByteArray(StandardCharsets.UTF_8),
            HiCarRequestCode.DATA_TYPE_META
        )
    }

    fun stopHicarNav() {
        Log.e(TAG, "---start stop nav!")
        val jsonObject = JSONObject()
        jsonObject["navFocus"] = "NATIVE"

        val data = jsonObject.toJSONString()
        sendHiCarData(
            data.toByteArray(StandardCharsets.UTF_8),
            DATA_TYPE_NAV_FOCUS
        )

    }

    private fun hicarNavStatus(data: ByteArray) {
        val toJson = GsonUtils.toJson(String(data))
        "--hicarNavStatus--$toJson".logE(TAG)
        if (toJson.contains("HICAR_STOP")) {
            //hicar导航停止
            Log.e(TAG, "---HICAR_STOP--")
        } else if (toJson.contains("HICAR")) {
            //hicar导航启动
            GlobalSetting.updateNavFocus(GlobalSetting.NAV_HICAR)
        }
    }

    /**
     * 连接失败Toast
     */
    private fun showToastForConnectFail() {
        CacheExt.setHiCarManualReconnect(ManualReconnectParams(1))
        if (errorCodeForReconnect == ConnectCallBackEvent.REASON_AUTO_CONNECT_SWITCH_OFF) {
            ToastUtils.show(
                application,
                "HUAWEI HiCar连接失败，请在手机侧HUAWEI HiCar允许自动连接",
                R.drawable.ic_toast_error,
                Toast.LENGTH_LONG
            )
        } else {
            ToastUtils.show(
                application,
                "HUAWEI HiCar连接失败，请重新进行连接",
                R.drawable.ic_toast_error,
                Toast.LENGTH_LONG
            )
        }
    }

    /**
     * 蓝牙推荐弹框
     */
    fun satisfyBluetoothRecommend(mainDevice: BluetoothDevice? = null) {

                    "satisfyBluetoothRecommend: startBluetoothRecommend".logE(TAG)
        if (currentEventType != ConnectCallBackEvent.EVENT_DEVICE_CONNECT) {
            setCurrentEventType(-1)
        }
        startBluetoothRecommend()

    }

    /**
     * 过滤满足回连条件开始回连
     */
    fun satisfyBluetoothConnectCondition(mainDevice: BluetoothDevice? = null) {
        val hiCarManualReconnectParams: ManualReconnectParams? = CacheExt.getHiCarManualReconnect()
        val isUserDisconnect = hiCarManualReconnectParams?.isUserDisconnect
        hiCarManualReconnectParams?.takeIf {
            isUserDisconnect != 1 && mHiCarAdapter != null
        }?.let {
            var hiCarTrustDeviceMac = ""
            val trustInfo: LastTrustPhoneInfo? = CacheExt.getHiCarLastTrustPhoneInfo()
            val info =
                mHiCarAdapter?.trustDeviceList?.find { it.phoneBrMac == trustInfo?.mPhoneBrMac }
            hiCarTrustDeviceMac = info?.phoneBrMac ?: ""
            val bondedDevices = myBluetoothManager.bondedDevices
            if (hiCarTrustDeviceMac.isNotEmpty() && !isReconnect) {
                "信任设备reconnect11,mac:$hiCarTrustDeviceMac".logE(TAG)

                if (mainDevice != null) { //蓝牙切换触发回连
                    if (hiCarTrustDeviceMac.contentEquals(mainDevice.address)) {
                        if ((currentEventType == -1 || currentEventType == ConnectCallBackEvent.EVENT_DEVICE_DISCONNECT || currentEventType == ConnectCallBackEvent.EVENT_DEVICE_CONNECT_FAILD)) {
                            "start reconnect22,mac:${hiCarTrustDeviceMac},currentEventType:$currentEventType}".logE(
                                TAG
                            )
//                            openWifi()
                            MainScope().launch(Dispatchers.Default) {
                                closeWifi()
                                delay(3000)
                                openWifi()
                                delay(3000)
                                "start reconnect33,mac:${hiCarTrustDeviceMac}".logE(TAG)
                                startReconnect(hiCarTrustDeviceMac)
                                MainScope().launch(Dispatchers.Main) {
                                    ToastUtils.show(
                                        application,
                                        message = "${info?.phoneName} 正在连接，请稍等",
                                        R.drawable.ic_toast_logo,
                                        duration = Toast.LENGTH_LONG
                                    )
                                }
                            }
                        }
                    }

                } else if (bondedDevices.isNotEmpty()) {//初始化触发
                    val bluetoothDevice: BluetoothDevice? =
                        bondedDevices.map { it.mainDevice }.find {
                            hiCarTrustDeviceMac.contentEquals(it?.address)
                        }
                    bluetoothDevice?.let {
                        if (!isReconnect && (currentEventType == -1 || currentEventType == ConnectCallBackEvent.EVENT_DEVICE_DISCONNECT || currentEventType == ConnectCallBackEvent.EVENT_DEVICE_CONNECT_FAILD)) {
                            "start reconnect44,mac:${hiCarTrustDeviceMac}".logE(TAG)
                            openWifi()
                            startReconnect(hiCarTrustDeviceMac)
                            MainScope().launch(Dispatchers.Main) {
                                ToastUtils.show(
                                    application,
                                    message = "${info?.phoneName} 正在连接，请稍等",
                                    R.drawable.ic_toast_logo,
                                    duration = Toast.LENGTH_LONG
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}