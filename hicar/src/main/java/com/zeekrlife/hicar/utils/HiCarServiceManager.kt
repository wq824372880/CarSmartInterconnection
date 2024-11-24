package com.zeekrlife.hicar.utils

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.zeekrlife.aidl.IHiCarListener
import com.zeekrlife.connect.base.manager.ConnectServiceManager
import com.zeekrlife.hicar.ui.activity.MainActivity
import com.zeekrlife.net.interception.logging.util.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description:
 *@author: e-Yang.Dong1
 *@date: 2023/5/31 10:12:12
 *@version: V1.0
 */
class HiCarServiceManager {


    companion object {
        val TAG = "HiCarServiceManager"

        /**
         *
         */
        @JvmStatic
        fun handleStartAdv(activity: MainActivity?) {
            Log.i(TAG, "handleStartAdv")
            DisplayConflictUtil.isRandomConflict(DisplayConflictUtil.FROM_HICAR) {
                if (it.first) {
                    DisplayConflictUtil.disposeAllConflict(activity, it)
                } else {
                    if (getBlueToothState() && getWifiState()) {
//                        MainScope().launch(Dispatchers.Default) {
//                            delay(1000)
                            ConnectServiceManager.getInstance().handleStartAdv()
//                        }

                    } else {
//                        if(!getBlueToothState()){
                            ConnectServiceManager.getInstance().openBlueTooth()
//                        }
//                        if(!getWifiState()){
                            ConnectServiceManager.getInstance().openWifi()
//                        }
                        MainScope().launch(Dispatchers.Default) {
                            delay(2000)
                            ConnectServiceManager.getInstance().handleStartAdv()
                        }
                    }
                }
            }
        }

        @JvmStatic
        fun handleStartReconnect(reConnectMac: String?) {
            Log.i(TAG, "handleStartAdv")
            ConnectServiceManager.getInstance().handleStartReconnect(reConnectMac)
        }

        /**
         *
         */
        @JvmStatic
        fun handleStoptAdv() {
            Log.i(TAG, "handleStoptAdv")
            ConnectServiceManager.getInstance().handleStoptAdv()
        }

        @JvmStatic
        fun requestAppList() {
            Log.i(TAG, "requestStartApp")
            ConnectServiceManager.getInstance().requestAppList()
        }

        @JvmStatic
        fun requestStartApp(packageName: String) {
            Log.i(TAG, "requestStartApp")
            ConnectServiceManager.getInstance().requestStartApp(packageName)
        }


        /**
         * 注册监听
         */
        @JvmStatic
        fun registerHiCarListener(
            listener: IHiCarListener,
            idle: Int,
            activity: MainActivity
        ) {
            "idle:$idle".logE(TAG)
            if (ConnectServiceManager.getInstance().ensureServiceAvailable()) {
                Log.e(TAG, "registerHiCarListener")
//                ConnectServiceManager.getInstance().openBlueTooth()
//                ConnectServiceManager.getInstance().openWifi()
                unregisterHiCarListener(listener)
                ConnectServiceManager.getInstance().isUnRegisterListener = false
                ConnectServiceManager.getInstance().registerHiCarListener(listener)
                if (idle < 0) {
                    activity.lifecycleScope.launch(Dispatchers.Default) {
                        delay(500)
                        handleStartAdv(activity)
                        Log.e(TAG, "registerHiCarListener handleStartAdv")
                    }
                }
            } else {
                ConnectServiceManager.getInstance().setInitConnectServiceCallback { success ->
                    if (success) {
                        Log.e(TAG, "registerHiCarListener2 callback success")
//                        ConnectServiceManager.getInstance().openBlueTooth()
//                        ConnectServiceManager.getInstance().openWifi()
                        ConnectServiceManager.getInstance().isUnRegisterListener = false
                        ConnectServiceManager.getInstance().registerHiCarListener(listener)
                        Log.e(TAG, "registerHiCarListener2 handleStartAdv")
                        if (idle < 0) {
                            Log.e(TAG, "registerHiCarListener idle:true handleStartAdv")
                            activity.lifecycleScope.launch(Dispatchers.Default) {
                                delay(500)
                                handleStartAdv(activity)
                                Log.e(TAG, "registerHiCarListener handleStartAdv")
                            }
                        }
                    } else {
                        Log.e(TAG, "registerHiCarListener2 callback fail")
                        unregisterHiCarListener(listener)
                    }
                }
            }

        }

        /**
         * 解除注册监听
         */
        @JvmStatic
        fun unregisterHiCarListener(listener: IHiCarListener) {
            Log.i(TAG, "unregisterHiCarListener")
            ConnectServiceManager.getInstance().unregisterHiCarListener(listener)
        }

        /**
         * 服务杀死后再次注册监听
         */
        @JvmStatic
        fun retryRegisterHiCarListener(
            listener: IHiCarListener,
            idle: Int,
            activity: MainActivity
        ) {
            "idle:$idle".logE(TAG)
            if (ConnectServiceManager.getInstance().ensureServiceAvailable()) {
                Log.e(TAG, "registerHiCarListener")
//                    ConnectServiceManager.getInstance().openBlueTooth()
                ConnectServiceManager.getInstance().openWifi()
                unregisterHiCarListener(listener)
                ConnectServiceManager.getInstance().registerHiCarListener(listener)
                ConnectServiceManager.getInstance().isUnRegisterListener = false
                if (idle < 0) {
                    activity.lifecycleScope.launch(Dispatchers.Default) {
                        delay(500)
                        handleStartAdv(activity)
                        Log.e(TAG, "registerHiCarListener handleStartAdv")
                    }
                }
            }

        }

        /**
         * 开始投屏
         */
        @JvmStatic
        fun startProjection() {
            Log.i(TAG, "startProjection")
            ConnectServiceManager.getInstance().startProjection()
        }

        /**
         * 暂停投屏
         */
        @JvmStatic
        fun pauseProjection() {
            Log.i(TAG, "pauseProjection")
            ConnectServiceManager.getInstance().pauseProjection()
        }

        /**
         * 停止投屏
         */
        @JvmStatic
        fun stopProjection() {
            Log.i(TAG, "stopProjection")
            ConnectServiceManager.getInstance().stopProjection()
        }

        /**
         * 设备断开
         */
        @JvmStatic
        fun disconnectDevice(
            deviceId: String?,
            callbackForConnect: ((boo: Boolean) -> Unit) = {},
            callbackForDisconnect: ((boo: Boolean) -> Unit) = {}
        ) {
            Log.i(TAG, "disconnectDevice:$deviceId")
            ConnectServiceManager.getInstance().disconnectDevice(deviceId ?: "")
            deleteApplist()
        }

        /**
         * 获取当前状态
         */
        @JvmStatic
        fun getCurrentEventType(): Int {
            return ConnectServiceManager.getInstance().currentEventType
        }

        /**
         * 获取当前连接的手机名称
         */
        @JvmStatic
        fun getPhoneName(): String {
            val phoneName = ConnectServiceManager.getInstance().phoneName
            Log.e(TAG, "getPhoneName : $phoneName")
            return phoneName ?: ""
        }

        /**
         * hicar服务端投屏销毁
         */
        @JvmStatic
        fun finishActivity(isBack: Boolean) {
            Log.e(TAG, "moveTaskToBack : $isBack")
            ConnectServiceManager.getInstance().finishActivity(isBack)
        }

        /**
         * hicar服务端退到后台
         */
        @JvmStatic
        fun moveTaskToBack(isBack: Boolean) {
            Log.e(TAG, "moveTaskToBack : $isBack")
            ConnectServiceManager.getInstance().moveTaskToBack(isBack)
        }

        /**
         * hicar清除applist
         */
        @JvmStatic
        fun deleteApplist() {
            ConnectServiceManager.getInstance().deleteAppList()
        }

        @JvmStatic
        fun getCurrentCastType(): Int {
            return ConnectServiceManager.getInstance().currentCastType
        }

        @JvmStatic
        fun getBlueToothState(): Boolean {
            return ConnectServiceManager.getInstance().blueToothState
        }

        @JvmStatic
        fun getWifiState(): Boolean {
            return ConnectServiceManager.getInstance().wifiState
        }

    }


}