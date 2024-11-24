package com.zeekrlife.connect.core.manager.wifi.ap

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.SoftApConfiguration
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import com.android.dx.stock.ProxyBuilder
import com.huawei.managementsdk.common.LogUtils
import com.zeekrlife.connect.core.app.App.Companion.application
import java.io.File
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class WifiApConnector {
    var wifiManager: WifiManager? = null
    private var mConnectivityManager: ConnectivityManager? = null
    private var startTetheringCallback: MyOnStartTetheringCallback? = null
    var isHotSpotApOpen: Boolean = false
    var mSsid =  ""
    var mPassphrase = ""
    var mPassphraseAppend:String = ""

    private val BAND_5GHZ = 1 shl 1


    fun startWifiApBySoftApCfg(name: String?, pwd: String?) {
        val wifiManager = application.let {
            it.getSystemService(Context.WIFI_SERVICE) as WifiManager
        }
        try {
            val softApCfgBuilderClz = Class.forName("android.net.wifi.SoftApConfiguration\$Builder")
            val softApCfgBuilder = softApCfgBuilderClz.newInstance()
            val setSsidMethod = softApCfgBuilderClz.getMethod(
                "setSsid",
                String::class.java
            )
            setSsidMethod.invoke(softApCfgBuilder, name)
            val setPassphraseMethod = softApCfgBuilderClz.getMethod(
                "setPassphrase",
                String::class.java,
                Int::class.javaPrimitiveType
            )
            setPassphraseMethod.invoke(
                softApCfgBuilder,
                pwd,
                SoftApConfiguration.SECURITY_TYPE_WPA2_PSK
            )
            val setBandMethod =
                softApCfgBuilderClz.getMethod("setBand", Int::class.javaPrimitiveType)
            setBandMethod.invoke(softApCfgBuilder, BAND_5GHZ)
            val setAutoShutdownEnabledMethod =
                softApCfgBuilderClz.getMethod("setAutoShutdownEnabled", Boolean::class.javaPrimitiveType)
            setAutoShutdownEnabledMethod.invoke(softApCfgBuilder, false)
//            val setChannelMethod =
//                softApCfgBuilderClz.getMethod("setChannel", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
//            setChannelMethod.invoke(softApCfgBuilder, 130, BAND_5GHZ)
            val buildMethod = softApCfgBuilderClz.getMethod("build")
            val softApCfg = buildMethod.invoke(softApCfgBuilder)
            val softApCfgClass = Class.forName("android.net.wifi.SoftApConfiguration")
            val setSoftApConfiguration =
                wifiManager?.javaClass?.getMethod("setSoftApConfiguration", softApCfgClass)
            setSoftApConfiguration?.invoke(wifiManager, softApCfg)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }

    fun startWifiAp(name: String, password: String) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            startWifiApBySoftApCfg(name, password)
        } else {
            startWifiApByWifiApCfg(name, password)
        }
    }


    fun startWifiApByWifiApCfg(name: String, password: String) {
        try {
            val wifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiConfiguration = WifiConfiguration()
            if (!TextUtils.isEmpty(name)) {
                wifiConfiguration.SSID = name
            }
            wifiConfiguration.allowedKeyManagement.set(4)
            wifiConfiguration.allowedAuthAlgorithms.set(0)
            if (!TextUtils.isEmpty(password)) {
                wifiConfiguration.preSharedKey = password
            }
            val wifiManagerClass: Class<out WifiManager> = wifiManager.javaClass
            val setWifiApConfigurationMethod = wifiManagerClass.getMethod(
                "setWifiApConfiguration",
                WifiConfiguration::class.java
            )
            val res = setWifiApConfigurationMethod.invoke(wifiManager, wifiConfiguration)
            Log.d(TAG, "startWifiAp: res = $res")
        } catch (e: java.lang.Exception) {
            Log.d(TAG, "EnableAp: Exception")
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "zzzWifiApConnector"
        private var wifiApConnector: WifiApConnector? = null
        fun getInstance(): WifiApConnector? {
            if (wifiApConnector == null) {
                wifiApConnector = WifiApConnector()
                wifiApConnector!!.wifiManager =
                    application
                        .getSystemService(Context.WIFI_SERVICE) as WifiManager
                wifiApConnector!!.mConnectivityManager =
                    application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            }
            return wifiApConnector
        }
    }

    fun isTetherActive(): Boolean {
        try {
            val method: Method? =
                mConnectivityManager?.javaClass?.getDeclaredMethod("getTetheredIfaces")
            if (method == null) {
                LogUtils.e(TAG, "getTetheredIfaces is null")
            } else {
                val res = method.invoke(mConnectivityManager) as Array<String>
                LogUtils.d(TAG, "getTetheredIfaces invoked")
                LogUtils.d(TAG, res.contentToString())
                if (res.isNotEmpty()) {
                    return true
                }
            }
        } catch (e: java.lang.Exception) {
            LogUtils.e(TAG, "Error in getTetheredIfaces")
            e.printStackTrace()
        }
        return false
    }


    fun startTethering(callback: MyOnStartTetheringCallback?= null): Boolean {

        val outputDir: File = application.codeCacheDir
        val proxy: Any = try {
            ProxyBuilder.forClass(onStartTetheringCallbackClass())
                .dexCache(outputDir).handler(InvocationHandler { proxy, method, args ->
                    when (method.name) {
                        "onTetheringStarted" -> callback?.onTetheringStarted()
                        "onTetheringFailed" -> callback?.onTetheringFailed()
                        else -> ProxyBuilder.callSuper(proxy, method, args)
                    }
                    null
                }).build()
        } catch (e: java.lang.Exception) {
            LogUtils.e(
                TAG,
                "Error in enableTethering ProxyBuilder"
            )
            e.printStackTrace()
            return false
        }
        var method: Method? = null
        try {
            method = mConnectivityManager!!.javaClass.getDeclaredMethod(
                "startTethering",
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                onStartTetheringCallbackClass(),
                Handler::class.java
            )
            if (method == null) {
                LogUtils.e(TAG, "startTetheringMethod is null")
            } else {
                method.invoke(
                    mConnectivityManager,
                    ConnectivityManager.TYPE_MOBILE,
                    false,
                    proxy,
                    null
                )
                LogUtils.d(TAG, "startTethering invoked")
            }
            return true
        } catch (e: java.lang.Exception) {
            LogUtils.e(TAG, "Error in enableTethering")
            e.printStackTrace()
        }
        return false
    }


    private fun onStartTetheringCallbackClass(): Class<*>? {
        try {
            return Class.forName("android.net.ConnectivityManager\$OnStartTetheringCallback")
        } catch (e: ClassNotFoundException) {
            LogUtils.e(
                TAG,
                "OnStartTetheringCallbackClass error: $e"
            )
            e.printStackTrace()
        }
        return null
    }

    /**
     * 关闭热点
     */
    fun stopTethering() {
        mSsid = getSsid() ?: ""
        mPassphrase = getPassphrase() ?: ""

        try {
            val method = mConnectivityManager?.javaClass?.getDeclaredMethod(
                "stopTethering",
                Int::class.javaPrimitiveType
            )
            if (method == null) {
                LogUtils.e(TAG, "stopTetheringMethod is null")
            } else {
                method.invoke(mConnectivityManager, ConnectivityManager.TYPE_MOBILE)
                LogUtils.d(TAG, "stopTethering invoked")
            }
        } catch (e: java.lang.Exception) {
            LogUtils.e(TAG, "stopTethering error: $e")
            e.printStackTrace()
        }

    }

    /**
     * 获取ap名称
     */
    fun getSsid(): String? {
        val wifiApInfo = getWifiApConfiguration()
        if (wifiApInfo == null) return ""
        val ssid = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            (wifiApInfo as SoftApConfiguration).ssid
        } else {
            (wifiApInfo as WifiConfiguration).SSID
        }
        return ssid
    }

    /**
     * 获取Ap密码
     */
    fun getPassphrase(): String? {
        val wifiApInfo = getWifiApConfiguration()
        if (wifiApInfo == null) return ""
        val passphrase = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            (wifiApInfo as SoftApConfiguration).passphrase
        } else {
            (wifiApInfo as WifiConfiguration).preSharedKey
        }
        return passphrase
    }

    private fun getWifiApConfiguration(): Any? {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            return try {
                val method = wifiManager?.javaClass?.getMethod("getSoftApConfiguration")
                method?.isAccessible = true
                method?.invoke(wifiManager)?.let {
                    it as SoftApConfiguration
                } ?: kotlin.run {
                    null
                }
            } catch (e: java.lang.Exception) {
                LogUtils.e(TAG, e.toString())
                null
            }
        } else {
            return try {
                val method = wifiManager?.javaClass?.getMethod("getWifiApConfiguration")
                method?.isAccessible = true
                if (wifiManager != null) {
                    method?.invoke(wifiManager) as WifiConfiguration
                } else {
                    null
                }
            } catch (e: java.lang.Exception) {
                LogUtils.e(TAG, e.toString())
                null
            }
        }

    }


    /**
     * 热点是否打开
     */
    fun isWifiApOn(): Boolean {
        var method: Method? = null
        var i = 0
        try {
            method = wifiManager?.javaClass?.getMethod("getWifiApState")
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }
        try {
            i = method?.invoke(wifiManager) as Int
            Log.d(TAG, "getWifiApState: $i")
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return i == 13 // 13 为已打开
    }

    /**
     * 热点是否失败
     * SWIM-251947
     * 20230826_1343中复现的应该是wifi fw crash的现象
     * 后面启动ap失败后有改变状态，state 14代表ap异常(Wi-Fi AP is in a failed state. This state will occur when an error occurs during enabling or disabling)
     * 08-26 13:36:26.748797 1535 10784 V WifiManager: SoftApCallbackProxy: onStateChanged: state=14, failureReason=0
     * 根据上次语音沟通，ap启动异常时是能返回状态的，请app在收到，ap异常状态后延迟2秒再启动ap
     */
    fun wifiApOpenFailed(): Boolean {
        var method: Method? = null
        var i = 0
        try {
            method = wifiManager?.javaClass?.getMethod("getWifiApState")
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }
        try {
            i = method?.invoke(wifiManager) as Int
            Log.d(TAG, "getWifiApState: $i")
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return i == 14 || i == 11 //state 14代表ap异常
    }

    fun removeTetheringCallback() {
        LogUtils.e("MainActivity", "removeTetheringCallback")
        if (this.startTetheringCallback != null) {
            this.startTetheringCallback = null
        }
    }


}