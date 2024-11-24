package com.zeekrlife.hicar.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.zeekrlife.aidl.IHiCarAppInfo
import com.zeekrlife.aidl.IHiCarListener
import com.zeekrlife.connect.base.manager.ConnectServiceManager
import com.zeekrlife.hicar.HiCarRequestCode
import com.zeekrlife.hicar.R
import com.zeekrlife.hicar.app.base.BaseFragment
import com.zeekrlife.hicar.data.PageType
import com.zeekrlife.hicar.data.cache.CacheExt
import com.zeekrlife.hicar.databinding.PinLayoutBinding
import com.zeekrlife.hicar.service.HiCarCoreServiceListener
import com.zeekrlife.hicar.ui.activity.MainActivity
import com.zeekrlife.hicar.ui.fragment.pinchildfragment.PinCodeFragment
import com.zeekrlife.hicar.ui.fragment.pinchildfragment.PinDisConnectFragment
import com.zeekrlife.hicar.ui.fragment.pinchildfragment.PinFailFragment
import com.zeekrlife.hicar.ui.fragment.pinchildfragment.PinLoadingFragment
import com.zeekrlife.hicar.ui.viewmodel.PinViewModel
import com.zeekrlife.hicar.utils.HiCarServiceManager
import com.zeekrlife.net.interception.logging.util.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description: 各个复用Fragment的统一管理入口，提供页面间通用PinViewModel
 *@author: e-Yang.Dong1
 *@date: 2023/5/10 13:03:13
 *@version: V1.0
 */
class PinFragment : BaseFragment<PinViewModel, PinLayoutBinding>() {
    val TAG = "zzzPinFragment"

    private val pinCodeFragment: PinCodeFragment by lazy { PinCodeFragment() }
    private val pinFailFragment: PinFailFragment by lazy { PinFailFragment() }
    private val pinLoadingFragment: PinLoadingFragment by lazy { PinLoadingFragment() }
    private val pinDisConnectFragment: PinDisConnectFragment by lazy { PinDisConnectFragment() }

    val pinViewModel: PinViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    companion object {
        var mDeviceId: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        "PinFragment onCreatee".logE(TAG)
//        if (CacheExt.getAgreeDisclaimer()) {
            if(!ConnectServiceManager.getInstance().ensureServiceAvailable()){  //确保service bind，延迟300ms再启动
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(300)
                    initRegister()
                }
            }else{
                initRegister()
            }
//        }

    }


    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    override fun initView(savedInstanceState: Bundle?) {
        replaceFragment()

        ConnectServiceManager.getInstance().setUnRegisterHiCarListenerCallback(object : ConnectServiceManager.UnRegisterHiCarListenerCallback{
            override fun onUnRegisterHiCarListenerCallback(result: Boolean) {
                initRegister()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        "PinFragment onResumee".logE(TAG)
    }

    private fun replaceFragment() {
        //替换内部 Fragment
        var fragment: Fragment? = null
        pinViewModel.pageType.observe(this) {
            "zzzPinFragment observe pinViewModel.pageType: $it".logE(TAG)
            if (pinViewModel.isPinLoadingPage(it)) {
                "展示loading fragment".logE(TAG)
                fragment = getFunPinLoadingFragment()
            } else if (pinViewModel.isPinCodePage(it)) {
                "展示PinCode fragment".logE(TAG)
                fragment = getFunPinCodeFragment()
            } else if (pinViewModel.isPinFailPage(it)) {
                "展示PinFail fragment".logE(TAG)
                fragment = getFunPinFailFragment()
            } else if (pinViewModel.isSurfacePage(it)) {
                "展示SurfacePage fragment".logE(TAG)
//                fragment = getPinSurfaceFragment()
            } else if (pinViewModel.isDisConnectPage(it)) {
                "展示DisConnectPage fragment".logE(TAG)
                fragment = getFunPinDisConnectFragment()
            }
            fragment?.let { fragment1 ->
                replaceFragment(fragment1)
            }
        }


    }

    private val mServiceListener: IHiCarListener.Stub = object : IHiCarListener.Stub() {
        override fun onDeviceChange(deviceId: String?, event: Int, errorcode: Int) {
            if (!deviceId.isNullOrEmpty()) {
                mDeviceId = deviceId
            }
            "onDeviceChange deviceId:$deviceId event:$event errorcode:$errorcode".logE(TAG)
            lifecycleScope.launch(Dispatchers.Main) {
                when (event) {
                    HiCarCoreServiceListener.EVENT_DEVICE_CONNECT -> {
                        delay(2000)
                        activity?.let {
                            it.finish()
                            exitProcess(0)
                        }
                    }

                    HiCarCoreServiceListener.EVENT_DEVICE_DISCONNECT -> onDeviceDisconnect()

                    HiCarCoreServiceListener.EVENT_DEVICE_CONNECT_FAILD -> pinViewModel.updatePageType(
                        PageType.HICAR_CONNECTION_FAILED
                    )

                    HiCarCoreServiceListener.EVENT_DEVICE_RECONNECT_FAILED -> pinViewModel.updatePageType(
                        PageType.HICAR_CONNECTION_FAILED
                    )

                    HiCarCoreServiceListener.EVENT_DEVICE_PROJECT_CONNECT -> {}


                    HiCarCoreServiceListener.EVENT_DEVICE_PROJECT_DISCONNECT -> onDeviceProjectDisconnect(
                        deviceId
                    )

                    else -> {}
                }
            }

        }

        override fun onDeviceServiceChange(serviceId: String?, event: Int) {
            "onDeviceServiceChange:$serviceId event:$event errorcode:$event".logE(TAG)
            lifecycleScope.launch(Dispatchers.Main) {
                when (event) {
                    HiCarCoreServiceListener.EVENT_DEVICE_SERVICE_PAUSE -> onDeviceServicePause()
                    HiCarCoreServiceListener.EVENT_DEVICE_SERVICE_RESUME -> {}
                    HiCarCoreServiceListener.EVENT_DEVICE_SERVICE_START -> {
//                           ToastUtils.show(
//                            context!!, "HUAWEI HiCar连接成功",
//                            R.drawable.ic_toast_success, Toast.LENGTH_LONG)
//                        Log.e(TAG, "HiCarCoreServiceListener.EVENT_DEVICE_SERVICE_START")

                    }

                    HiCarCoreServiceListener.EVENT_DEVICE_SERVICE_STOP -> pinViewModel.updatePageType(
                        PageType.HICAR_CONNECTION_FAILED
                    )


                    HiCarCoreServiceListener.EVENT_DEVICE_MIC_REQUEST -> {
                        //释放车机mic 供hiCar使用
                        Log.e(TAG, "--onDeviceChange--EVENT_DEVICE_MIC_REQUEST--")
                    }

                    HiCarCoreServiceListener.EVENT_DEVICE_MIC_RELEASE -> {
                        Log.e(TAG, "--onDeviceChange--EVENT_DEVICE_MIC_RELEASE--")
                        //继续使用mic
                    }

                    HiCarCoreServiceListener.EVENT_DEVICE_DISPLAY_SERVICE_PLAYING -> {
//                        ToastUtils.show(
//                            context!!, "HUAWEI HiCar连接成功",
//                            R.drawable.ic_toast_success, Toast.LENGTH_LONG)
                        lifecycleScope.launch {
//                            delay(2000)
//                            if(!pinViewModel.background.value){
                            pinViewModel.updatePageType(PageType.HICAR_DISCONNECT)
//                            }
                        }
                    }

                    HiCarCoreServiceListener.EVENT_DEVICE_DISPLAY_SERVICE_PLAY_FAILED -> {
                        //投屏失败
//                         Log.e(TAG, "--onDeviceChange--投屏失败--")
//                        pinViewModel.updatePageType(PageType.HICAR_CONNECTION_FAILED)

                    }

                    else -> {}
                }
            }
        }

        override fun onHiCarApplistChange(list: MutableList<IHiCarAppInfo>?) {
            "zzzPinFragment onHiCarApplistChange:${list?.size}".logE(TAG)
//            if(activity?.isFinishing == true || activity?.isDestroyed == true){
//                "zzzPinFragment onHiCarApplistChange22:${list?.size}".logE(TAG)
////                return
//            }
//            activity?.finish()
            MainScope().launch(Dispatchers.Main) {
                "MainActivity finishAllActivity".logE(TAG)
                activity?.finish()
                exitProcess(0)
            }
            "zzzPinFragment onHiCarApplistChange33:${list?.size}".logE(TAG)
        }

        override fun onDataReceive(key: String?, dataType: Int, data: ByteArray?) {
            when (dataType) {
                HiCarRequestCode.DATA_TYPE_CALL_STATE_FOCUS -> {
                    "receive hiCar focus change ->>>>>".logE(TAG)
                    //如果callState == 1 来电态  发送仲裁信息到dim
                    //ConnectServiceManager.getInstance().updateDimInfo()
                }

                HiCarRequestCode.DATA_TYPE_AA_APPS -> {
//                    "onDataReceive dataType = $dataType,data:${GsonUtils.toJson(String(data?:"".toByteArray()))}".logE(TAG)
                }

            }

        }


        override fun onPinCode(code: String?) {
            lifecycleScope.launch(Dispatchers.Main) {
                code?.let {
                    Log.e(TAG, "onPinCode :$it")
                    pinViewModel.updatePinCode(it)//更新连接码
                    delay(500)
                    pinViewModel.updatePageType(PageType.HICAR_CONNECT)
                }
            }
        }

        override fun onWifiAPState(isOpen: Boolean) {
        }

        override fun onBinderDied() {
            Log.e(TAG, "onBinderDied")
            (activity as MainActivity).quit()
        }

        override fun onShowStartPage() {
            pinViewModel.updatePageType(PageType.CONNECTION_CODE_GET)
        }

    }

    fun initRegister() {
        val getCurrentEventType = HiCarServiceManager.getCurrentEventType()
        "getCurrentEventType :$getCurrentEventType".logE(TAG)
        pinViewModel.updatePageType(pinViewModel.getPageTypeFromEvent(getCurrentEventType))
        HiCarServiceManager.registerHiCarListener(
            mServiceListener,
            getCurrentEventType,
            activity as MainActivity
        )
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.innerFragmentContainer, fragment)
            .commit()
    }

    private fun getFunPinLoadingFragment(): PinLoadingFragment {
        return pinLoadingFragment
    }

    private fun getFunPinCodeFragment(): PinCodeFragment {
        return pinCodeFragment
    }

    private fun getFunPinFailFragment(): PinFailFragment {
        return pinFailFragment
    }


    private fun getFunPinDisConnectFragment(): PinDisConnectFragment {
        return pinDisConnectFragment
    }

    /**
     * 设备断开连接
     */
    private fun onDeviceDisconnect() {
        Log.e(TAG, "onDeviceDisconnect")
        pinViewModel.updatePageType(PageType.HICAR_CONNECTION_FAILED)
//        ConnectServiceManager.getInstance().closeWifi()
//        HiCarServiceManager.disconnectDevice(PinFragment.mDeviceId)
    }


    /**
     * 投屏断开连接
     */
    private fun onDeviceProjectDisconnect(deviceId: String?) {
        Log.e(TAG, "onDeviceProjectDisconnect")
        pinViewModel.updatePageType(PageType.HICAR_CONNECTION_FAILED)
//        (activity as MainActivity).quit()
    }

    /**
     * 投屏服务暂停
     */
    private fun onDeviceServicePause() {
        Log.e(TAG, "onDeviceServicePause")
//        HiCarServiceManager.pauseProjection()
//        quit()
    }


    /**
     * 投屏服务停止
     */
    private fun onDeviceServiceStop() {
        Log.e(TAG, "onDeviceServiceStop")
        (activity as MainActivity).quit()
    }

    override fun onPause() {
        super.onPause()
        "PinFragment onPause".logE(TAG)
    }
    override fun onStop() {
        super.onStop()
        "PinFragment onStop".logE(TAG)
    }
    override fun onDestroy() {
        super.onDestroy()
        "PinFragment onDestroy".logE(TAG)
        HiCarServiceManager.unregisterHiCarListener(mServiceListener)
    }


}