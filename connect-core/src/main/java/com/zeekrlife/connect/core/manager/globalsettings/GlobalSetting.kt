package com.zeekrlife.connect.core.manager.globalsettings

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import com.zeekrlife.connect.core.ConnectServiceImpl
import com.zeekrlife.connect.core.app.App
import com.zeekrlife.connect.core.app.InitUtils.Companion.globalSettingCurrentCastType
import com.zeekrlife.connect.core.app.eventViewModel
import com.zeekrlife.connect.core.manager.wifi.ap.WifiApConnector
import com.zeekrlife.net.interception.logging.util.logE


object GlobalSetting {

    const val FROM_IDLE = 0 //空闲
    const val FROM_DLNA = 1 //影视投屏（DLNA）
    const val FROM_USB = 2 //有线投屏
    const val FROM_HICAR = 3
    const val FROM_CARLINK = 4
    const val FROM_WIRELESSMIRROR = 5 //无线镜像
    const val FROM_HICAR_PREPARE = 33 //hicar未投屏但抢占了焦点  场景：启动后发生投屏冲突，关掉正在连接的投屏方式，抢占投屏焦点但未开始投屏
    const val FROM_CARLINK_PREPARE = 44 //carlink未投屏但抢占了焦点  场景：启动后发生投屏冲突，关掉正在连接的投屏方式，抢占投屏焦点但未开始投屏
    const val CASTTYPE = "CastType"  // global key
    const val NAVFOCUS = "NavFocus"  // global key
    const val NAV_HICAR = "HICAR"
    const val NAV_NATIVE = "NATIVE"

    const val BLUETOOTH_MAC_HICAR_CONNECTING = "bluetooth_mac_hicar_connecting"  // global key


    fun hicarConnected(castType:Int,mac: String){
        putCastType(castType)
        putBluetoothMac(mac)
    }

    fun hicarDisconnect(){
        putCastType(0)
        putBluetoothMac("")
    }




    fun getCastType():Int{
        return Settings.System.getInt(
            App.application.contentResolver,
            CASTTYPE
        )
    }

    fun updateNavFocus(navFocus: String) {
        Settings.System.putString(
            App.application.contentResolver,
            NAVFOCUS, navFocus
        )
//        val uri = Settings.System.getUriFor(CASTTYPE)
//        App.application.contentResolver.notifyChange(uri, null)
        "navFocus $navFocus".logE("NAVFOCUS")
    }

    fun observerNavFocus(){
        val resolver: ContentResolver = App.application.contentResolver
        val uri = Settings.System.getUriFor(NAVFOCUS)

        val observer: ContentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                "observerNavFocus $uri".logE("NavFocus")
                if (uri == Settings.System.getUriFor(NAVFOCUS)) {
                    val newValue = Settings.System.getString(resolver, NAVFOCUS)
                    if (NAV_NATIVE == newValue) {
                        ConnectServiceImpl.instance.stopHicarNav()
                    }
                }
            }

        }

        resolver.registerContentObserver(uri, true, observer)
    }


    fun putCastType(type:Int){
        Settings.System.putInt(
            App.application.contentResolver,
            CASTTYPE, type
        )
        val uri = Settings.System.getUriFor(CASTTYPE)
        App.application.contentResolver.notifyChange(uri, null)
        if(type == 0 && '0' == (WifiApConnector.getInstance()?.mPassphraseAppend?.lastOrNull())){ //断开后热点恢复原始状态
            WifiApConnector.getInstance()?.startWifiAp(WifiApConnector.getInstance()?.mSsid?:"ZEEKR-0001", "12345678")
            WifiApConnector.getInstance()?.stopTethering()
            "'0' == (WifiApConnector.getInstance()?.mPassphrase true".logE("CastType")
        }
        "putCastType $type".logE("CastType")
    }

    fun observerCaseType(){
        val resolver: ContentResolver = App.application.contentResolver
        val uri = Settings.System.getUriFor(CASTTYPE)
        globalSettingCurrentCastType = Settings.System.getInt(resolver, CASTTYPE,0)
        val observer: ContentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                if (uri == Settings.System.getUriFor(CASTTYPE)) {
                    val newValue = Settings.System.getInt(resolver, CASTTYPE)
                    globalSettingCurrentCastType = newValue
                    "observerCaseType $uri,globalSettingCurrentCastType:$globalSettingCurrentCastType".logE("CastType")
                    if(newValue != 0 && newValue != 3 &&  newValue != 4){
                        eventViewModel.castTypeEvent.postValue(newValue)
                        "eventViewModel.castTypeEvent uri:$uri,CASTTYPE:$newValue".logE("CastType")
                    }
                }
            }

        }

        resolver.registerContentObserver(uri, true, observer)
    }

    fun putBluetoothMac(mac:String){
        Settings.System.putString(
            App.application.contentResolver,
            BLUETOOTH_MAC_HICAR_CONNECTING, mac
        )
        val uri = Settings.System.getUriFor(BLUETOOTH_MAC_HICAR_CONNECTING)
        App.application.contentResolver.notifyChange(uri, null)
        "bluetooth_mac_hicar_connecting: $mac".logE("GlobalSetting")
    }

}