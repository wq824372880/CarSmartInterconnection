/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2022. All rights reserved.
 */
package com.zeekrlife.connect.core.adapter

import android.app.UiModeManager
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.text.TextUtils
import android.util.Log
import com.huawei.hicarsdk.CarConfig
import com.huawei.hicarsdk.HardwareInfo
import com.zeekr.basic.appContext
import com.zeekr.car.tsp.PropertiesUtil
import com.zeekrlife.connect.core.app.App.Companion.application
import com.zeekrlife.connect.core.utils.VehicleUtil
import com.zeekrlife.net.interception.logging.util.logE
import java.io.IOException
import java.io.InputStream
import java.util.Properties
import java.util.regex.Pattern


/**
 * adapter for generating CarConfig
 */
class ConnectConfigHelper private constructor() {
    private var mPhysicsWidth = 0
    private var mPhysicsHeight = 0
    private var mScreenWidth = 0
    private var mScreenHeight = 0
    private var mScreenSize = 0f
    private var mDefaultBitrate = 5 * 1024 * 1024
    private var mMaxBitrate = 20 * 1000 * 1000
    private var mMinBitrate = 500 * 1000
    private var mCodecType = 0
    private var mDpi = 0
    private var mFps = 0
    private var mGop = 0
    private var mCodecConfigureFlag = 0
    private var mDefaultAudioFormat = 0
    private var mAudiodelaybuffer = 5
//    private var mAdvPower = -1000
    private var mSupport5gChannels: IntArray? = null
    private var mIgnoreAndroidCamera = false
    private var mBluetoothMac: String? = null
    private var mModuleId: String? = null

//    private val mBluetoothManager: BluetoothManager by lazy {  appContext.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager }
    companion object {

        private const val TAG = "zzzConnectConfigHelper"
//        @Volatile
//        private var mConfigHelper: ConnectConfigHelper? = null
        private const val PROPERTIES_PATH = "/system/etc/hicar.properties"
        private const val DEFAULT_BLUETOOTH_ADDRESS = "01:01:01:01:01:01"
        private const val MODEL_ID = "0x00049200"  // TODO:
        private const val PHYSICS_WIDTH = "PhysicsWidth"
        private const val PHYSICS_HEIGHT = "PhysicsHeight"
        private const val VIDEO_WIDTH = "VideoWidth"
        private const val VIDEO_HEIGHT = "VideoHeight"
        private const val SCREEN_WIDTH = "ScreenWidth"
        private const val SCREEN_HEIGHT = "ScreenHeight"
        private const val SCREEN_SIZE = "ScreenSize"
        private const val DEFAULT_BITRATE = "DefaultBitrate"
        private const val MAX_BITRATE = "MaxBitrate"
        private const val MIN_BITRATE = "MinBitrate"
        private const val DEFAULT_AUDIO_FORMAT = "DefaultAudioFormat"
        private const val AUDIODELAYBUFFER = "AudioDelayBuffer"
        private const val ADV_POWER = "AdvPower"
        private const val DPI = "DPI"
        private const val FPS = "FPS"
        private const val GOP = "GOP"
        private const val CODEC_TYPE = "CodecType"
        private const val CODEC_CONFIGURE_GLAG = "CodecConfigureFlag"
        private const val SUPPORT_5G_CHANNEL = "Support5GChannel"
        private const val IGNORE_ANDROID_CAMERA = "IgnoreAndroidCamera"
        private const val CONNECT_TYPE_WIRELESS_AND_USB = 3


        /**
         * Singleton mode, get instance
         *
         * @return result HiCarDemoCarAdapter
         */
        @JvmStatic
        @get:Synchronized
        val configHelperInstance by lazy {
            ConnectConfigHelper()
        }
//            get() = mConfigHelper
    }

    init {
        blueToothMac
        readConfigFile()
    }

    private val blueToothMac: String?
        get() {
            if (mBluetoothMac != null) {
                return mBluetoothMac
            }
            val mBluetoothManager =  application.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            mBluetoothMac = mBluetoothManager.adapter?.address
            if (mBluetoothMac == null) {
                Log.e(TAG, "getBluetoothMac fail,set default bluetooth address")
                mBluetoothMac = DEFAULT_BLUETOOTH_ADDRESS
                return DEFAULT_BLUETOOTH_ADDRESS
            }
            Log.e(TAG, "getBlueToothMac success")
            return mBluetoothMac
        }

    /**
     * create CarConfig with Bluetooth Mac and ModelId
     *
     * @return object CarConfig
     */
    fun createBasicCarConfig(): CarConfig {
        PropertiesUtil.setProperty("persist.sys.hicar.apsta.coexist","1")
        PropertiesUtil.setProperty("persist.sys.hicar.reuse.ap","true")
        PropertiesUtil.setProperty("persist.sys.hicar.ap.interface","wlan2")

        val builder = CarConfig.Builder()
        if (mBluetoothMac != null && mBluetoothMac!!.isNotEmpty()) {
            builder.withBrMac(mBluetoothMac)
        }

//        if (mPhysicsWidth != 0) {
//            builder.withPhysicsWidth(mPhysicsWidth)
//        }
//        if (mPhysicsHeight != 0) {
//            builder.withPhysicsHeight(mPhysicsHeight)
//        }
//        if (mScreenWidth != 0) {
//            builder.withScreenWidth(mScreenWidth)
//        }
//        if (mScreenHeight != 0) {
//            builder.withScreenHeight(mScreenHeight)
//        }
//        if (mScreenSize > 0.0f) {
//            builder.withScreenSize(mScreenSize)
//        }
//        if (mDefaultBitrate != 0) {
//            builder.withDefaultBitrate(mDefaultBitrate)
//        }
//        if (mMaxBitrate != 0) {
//            builder.withMaxBitrate(mMaxBitrate)
//        }
//        if (mMinBitrate != 0) {
//            builder.withMinBitrate(mMinBitrate)
//        }

//        if (mAdvPower != -1000) {
//            builder.withAdvPower(mAdvPower)
//        }
        builder.withAudioDelayBuffer(mAudiodelaybuffer)
        builder.withDefaultAudioFormat(mDefaultAudioFormat)

        builder.withSupportWireless(true)
        builder.withSupportUsb(false)
        builder.withSupportReconnect(true)
        builder.withSupport5gChannels(intArrayOf(149, 153, 157, 161, 165))
        builder.withAdvPower(-95)
        builder.withDpi(255)
        builder.withFps(60)
        builder.withGop(60)
        builder.withScreenWidth(2560)
        builder.withScreenHeight(1600)
        builder.withPhysicsWidth(2560)
        builder.withPhysicsHeight(1600)
        builder.withVideoWidth(2560)
        builder.withVideoHeight(1358)
        builder.withScreenSize(14.375F)
        builder.withCodecType(1)
        builder.withMinBitrate(4 * 1000 * 1000)
        builder.withMaxBitrate(8 * 1000 * 1000)
        builder.withDefaultBitrate(5 * 1000 * 1000)

        val hashMap = HashMap<String,String>().apply {
            put("isSupportRoundCorner","1")
            put("DAY_NIGHT_MODE",if(getUINightMode()) "night" else "day")//白天黑夜模式
            put("isOnlyParkAllow","0")//挡位
            put("DEVICE_TYPE","4")//车机类型
        }
        builder.withInitialConfig(hashMap)

        val modeId = VehicleUtil.getModeId()

        Log.e(TAG, "----modeId----$modeId")
        if (modeId != null && !TextUtils.isEmpty(modeId)) {
            builder.withModeId(modeId)
        }
//        builder.withAudioSourceConfig()
        builder.withCodecConfigureFlag(0)
        builder.withIgnoreAndroidCamera(mIgnoreAndroidCamera)
        val hardwareInfo = HardwareInfo("512068_DHU7A_CS1E", "QCA6696", "QCA6696")
        builder.withHardwareInfo(hardwareInfo)
        return builder.build()
    }

    private fun readConfigFile() {
        var `is`:InputStream?= null
        try {
             `is` = application.assets.open("hicar.properties")
            val prop = Properties()
            prop.load(`is`)
            mModuleId = prop.getProperty(MODEL_ID)
            isPropertyNull(mModuleId, MODEL_ID)
            val screenSize = prop.getProperty(SCREEN_SIZE)
            if (!isPropertyNull(screenSize, SCREEN_SIZE)) {
                mScreenSize = java.lang.Float.valueOf(screenSize)
            }
            readVideoPorperties(prop)
            val defaultAudioFormat = prop.getProperty(DEFAULT_AUDIO_FORMAT)
            if (!isPropertyNull(defaultAudioFormat, DEFAULT_AUDIO_FORMAT)) {
                mDefaultAudioFormat = Integer.valueOf(defaultAudioFormat)
            }

            val audiodelaybuffer = prop.getProperty(AUDIODELAYBUFFER)
            if (!isPropertyNull(audiodelaybuffer, AUDIODELAYBUFFER)) {
                mDefaultAudioFormat = Integer.valueOf(audiodelaybuffer)
            }
//            val advPower = prop.getProperty(ADV_POWER)
//            if (!isPropertyNull(advPower, ADV_POWER)) {
//                mAdvPower = Integer.valueOf(advPower)
//            }
//            val support5gChannel = prop.getProperty(SUPPORT_5G_CHANNEL)
//            if (!isPropertyNull(support5gChannel, SUPPORT_5G_CHANNEL)) {
//                val channels = getSupport5gChannel(support5gChannel)
//                mSupport5gChannels = Arrays.copyOf(channels, channels.size)
//            }
            val ignoreAndroidCamera = prop.getProperty(IGNORE_ANDROID_CAMERA)
            if (!isPropertyNull(ignoreAndroidCamera, IGNORE_ANDROID_CAMERA)) {
                mIgnoreAndroidCamera = java.lang.Boolean.parseBoolean(ignoreAndroidCamera)
            }
        } catch (e: IOException) {
             "readParam  IOException:" + e.localizedMessage.logE(TAG)
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    "readParam file close IOException:" + e.localizedMessage.logE(TAG)
                }
            }
        }
    }

    private fun isPropertyNull(property: String?, tag: String): Boolean {
        return if (property.isNullOrEmpty()) {
//            "$tag is invalid".logE(TAG)
            true
        } else {
//            "get $tag success property:$property".logE(TAG)
            false
        }
    }

    private fun getSupport5gChannel(support5gChannel: String): IntArray {
        return if (Pattern.matches("\\[\\d+(,\\d+)*\\]", support5gChannel)) {
            var temp = support5gChannel.replace("[", "")
            temp = temp.replace("]", "")
            val channelStrs =
                temp.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val support5gChannels = IntArray(channelStrs.size)
            try {
                for (i in channelStrs.indices) {
                    support5gChannels[i] = channelStrs[i].toInt()
                }
            } catch (e: NumberFormatException) {
                 "support 5g channel number is invalid".logE(TAG)
                return IntArray(0)
            }
            support5gChannels
        } else {
            "wrong format of support 5g channel".logE(TAG)
            IntArray(0)
        }
    }

    private fun readVideoPorperties(prop: Properties) {
        val physicsWidth = prop.getProperty(PHYSICS_WIDTH)
        if (!isPropertyNull(physicsWidth, PHYSICS_WIDTH)) {
            mPhysicsWidth = Integer.valueOf(physicsWidth)
        }
        val physicsHeight = prop.getProperty(PHYSICS_HEIGHT)
        if (!isPropertyNull(physicsHeight, PHYSICS_HEIGHT)) {
            mPhysicsHeight = Integer.valueOf(physicsHeight)
        }
        val screenWidth = prop.getProperty(SCREEN_WIDTH)
        if (!isPropertyNull(screenWidth, SCREEN_WIDTH)) {
            mScreenWidth = Integer.valueOf(screenWidth)
        }
        val screenHeight = prop.getProperty(SCREEN_HEIGHT)
        if (!isPropertyNull(screenHeight, SCREEN_HEIGHT)) {
            mScreenHeight = Integer.valueOf(screenHeight)
        }
        val defaultBitrate = prop.getProperty(DEFAULT_BITRATE)
        if (!isPropertyNull(defaultBitrate, DEFAULT_BITRATE)) {
            mDefaultBitrate = Integer.valueOf(defaultBitrate)
        }
        val maxBitrate = prop.getProperty(MAX_BITRATE)
        if (!isPropertyNull(maxBitrate, MAX_BITRATE)) {
            mMaxBitrate = Integer.valueOf(maxBitrate)
        }
        val minBitrate = prop.getProperty(MIN_BITRATE)
        if (!isPropertyNull(minBitrate, MIN_BITRATE)) {
            mMinBitrate = Integer.valueOf(minBitrate)
        }
        val dpi = prop.getProperty(DPI)
        if (!isPropertyNull(dpi, DPI)) {
            mDpi = Integer.valueOf(dpi)
        }
        val fps = prop.getProperty(FPS)
        if (!isPropertyNull(fps, FPS)) {
            mFps = Integer.valueOf(fps)
        }
        val gop = prop.getProperty(GOP)
        if (!isPropertyNull(gop, GOP)) {
            mGop = Integer.valueOf(gop)
        }
        val codecType = prop.getProperty(CODEC_TYPE)
        if (!isPropertyNull(codecType, CODEC_TYPE)) {
            mCodecType = Integer.valueOf(codecType)
        }
        val codecConfigureFlag = prop.getProperty(CODEC_CONFIGURE_GLAG)
        if (!isPropertyNull(codecConfigureFlag, CODEC_CONFIGURE_GLAG)) {
            mCodecConfigureFlag = Integer.valueOf(codecConfigureFlag)
        }
    }

    fun getModuleId(): String? {
        return mModuleId
    }

    fun getUINightMode(): Boolean {
        val uiMode = appContext.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        return uiMode.nightMode == UiModeManager.MODE_NIGHT_YES
    }



}