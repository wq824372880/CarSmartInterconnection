/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */
package com.zeekrlife.connect.core.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.ViewTreeObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.zeekr.sdk.mediacenter.bean.ClientType
import com.zeekr.sdk.mediacenter.impl.MediaCenterAPI
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.ext.finishCurrentActivity
import com.zeekrlife.common.ext.gone
import com.zeekrlife.common.ext.visible
import com.zeekrlife.connect.core.ConnectServiceImpl
import com.zeekrlife.connect.core.app.base.BaseActivity
import com.zeekrlife.connect.core.app.eventViewModel
import com.zeekrlife.connect.core.databinding.ActivitySurfaceBinding
import com.zeekrlife.net.interception.logging.util.logE
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SurfaceActivity : BaseActivity<BaseViewModel, ActivitySurfaceBinding>() {
    private val TAG = "SurfaceActivity"
    var booStartSurfaceFromApp = false
    var booPauseProjection = false
    var startPackageName: String? = null
    var lastPackageName: String? = null
    var mType:Int? = 1
    private var onNewIntentRepeatTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MediaCenterAPI.get().initDrivingRestrictions(ClientType.VIDEO) {
            "initDrivingRestrictions:-----> $it".logE(TAG)
            runOnUiThread { showProhibitDialog(it) }
        }
        "onCreate: --->".logE(TAG)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ConnectServiceImpl.instance.startAppResponseMap.clear()
        mBind.loadingContentTV.text = if(ConnectServiceImpl.instance.phoneName.isEmpty()) "正在连接..." else "正在连接 ${ConnectServiceImpl.instance.phoneName}"
        startPackageName = intent?.extras.let {
            it?.getString("packageName")
        }
        mType = intent?.extras.let {
            it?.getString("type")
        }?.toInt()
        if (startPackageName.isNullOrEmpty()) {
            if(ProtocolActivity.isFirstProjection){//新增： 首次连接的加载showLoadingUi()
                showLoadingUi()
                mBind.connectLoading.gone()
            }else{
                mBind.connectLoading.visible()
                showSuccessUi()
            }
            booStartSurfaceFromApp = false
            ConnectServiceImpl.instance.currentStartApp = ""
        } else {
            showLoadingUi()
            mBind.connectLoading.gone()
            "SurfaceActivity initView() error  startPackageName:$startPackageName,type:$mType".logE(TAG)
            booStartSurfaceFromApp = true
            ConnectServiceImpl.instance.currentStartApp = startPackageName
        }
        booPauseProjection = true

        "SurfaceActivity initView() startPackageName:$startPackageName,type:$mType".logE(TAG)
        showProhibitDialog(ProhibitWrapper.instance.isProhibitBylaw)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        mIsProhibitDialogShowing = false

        if (isExecutable()) {
            setIntent(intent)
            startPackageName = intent?.extras.let {
                it?.getString("packageName")
            }
            mType = intent?.extras.let {
                it?.getString("type")
            }?.toInt()
            if (startPackageName.isNullOrEmpty() && !ConnectServiceImpl.instance.currentStartApp.isNullOrEmpty()) { //当前已打开并且没有重新打开别的app
                mBind.connectLoading.gone()
                showSuccessUi()
                booPauseProjection = false
                booStartSurfaceFromApp = false
            } else { //打开app
                mBind.connectLoading.gone()
                showLoadingUi()
                booPauseProjection = true
                booStartSurfaceFromApp = true
            }
            "SurfaceActivity onNewIntent startPackageName:$startPackageName,type:$mType,booPauseProjection:$booPauseProjection".logE(TAG)
            showProhibitDialog(ProhibitWrapper.instance.isProhibitBylaw)
        }
        "onNewIntent:---->".logE(TAG)
    }

    override fun onResume() {
        super.onResume()
        "SurfaceActivity onResume()".logE(TAG)
        startProjectionForApp()
    }

    override fun onPause() {
        super.onPause()
        "SurfaceActivity onPause()".logE(TAG)
        if (startPackageName?.isNotEmpty() == true) {
            lastPackageName = startPackageName
        }
    }

    override fun onStop() {
        super.onStop()
        "SurfaceActivity onStop()".logE(TAG)
        ProhibitWrapper.instance.dismiss()
    }

    override fun initObserver() {
        "SurfaceActivity addOnGlobalLayoutListener".logE(TAG)
        mBind.surface.viewTreeObserver.addOnGlobalLayoutListener(mGlobalLayoutListener)
        finishActivityObserver()
        eventViewModel.startAppSuccess.observe(this, Observer {
            if(it){
                "SurfaceActivity startAppSuccess".logE(TAG)
                lifecycleScope.launch {
                    delay(1500)
                    mBind.connectLoading.gone()
                    showSuccessUi()
                }
            }

        })
    }

    override fun showToolBar(): Boolean {
        return false
    }


    override fun onDestroy() {
        super.onDestroy()
        "SurfaceActivity onDestroy".logE(TAG)
        mBind.surface.viewTreeObserver.removeOnGlobalLayoutListener(mGlobalLayoutListener)
        mIsProhibitDialogShowing = false
        ConnectServiceImpl.instance.startAppResponseMap.clear()
    }

    private var mSurfaceHolder: SurfaceHolder? = null
    private var surfaceWidth: Int = 0
    private var surfaceHeight: Int = 0
    private val mGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener = object :
        ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            mBind.surface.viewTreeObserver.removeOnGlobalLayoutListener(this)
            mBind.surface.holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
                    Log.e(TAG, " surface view surfaceCreated")
                }

                override fun surfaceChanged(
                    surfaceHolder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                    Log.e(TAG, " surface view changed: width = $width, height = $height")
                    if (surfaceHolder.surface != null && surfaceHolder.surface.isValid
                    ) {
                        Log.e(TAG, " surface holder is valid")
                        mSurfaceHolder = surfaceHolder
                        surfaceWidth = width
                        surfaceHeight = height
                    } else {
                        Log.e(TAG, " surface holder or surface is null or invalid")
                        booPauseProjection = true
                        ConnectServiceImpl.instance.pauseProjection()
                    }
                }

                override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
                    Log.e(TAG, " surface view destroyed")
                    booPauseProjection = true
                    ConnectServiceImpl.instance.pauseProjection()
//                    if (eventViewModel.background.value == false) {
//                        ConnectServiceImpl.instance.disconnectDevice(ConnectServiceImpl.connectedDevice)
//                        this@SurfaceActivity.finish()
//                    }
                }
            })
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    private fun finishActivityObserver() {
        eventViewModel.finishActivityEvent.observe(this){
            if (it) {
                "finishActivityObserver finishCurrentActivity".logE(TAG)
//                lifecycleScope.launch{
//                    delay(800)
//                    booPauseProjection = true
//                    ConnectServiceImpl.instance.pauseProjection()
//                    finishCurrentActivity(this@SurfaceActivity)
//                }
                booPauseProjection = true
                ConnectServiceImpl.instance.pauseProjection()
                finishCurrentActivity(this@SurfaceActivity)
//                this@SurfaceActivity.moveTaskToBack(true)
//                this@SurfaceActivity.finish()
            }
        }
    }


    private fun startProjectionForApp() {
        "startProjectionForApp booPauseProjection = $booPauseProjection,startPackageName = $startPackageName".logE(
            TAG
        )
        lifecycleScope.launch {
            delay(300)
            if (startPackageName.isNullOrEmpty() && ConnectServiceImpl.instance.currentStartApp.isNullOrEmpty()){
                return@launch
            }
            if (!booPauseProjection) return@launch
            if (ConnectServiceImpl.instance
                    .updateCarConfig(mSurfaceHolder?.surface, surfaceWidth, surfaceHeight)
            ) {
                "onResume surfaceChanged startProjection调用了".logE(TAG)
                booPauseProjection = false
                ConnectServiceImpl.instance.startProjection()
            }
            startPackageName.takeIf {
                it?.isNotEmpty() == true
            }.run {
                this?.let {
                    "onResume startPackageName.takeIf it?.isNotEmpty() == true".logE(TAG)
                    ConnectServiceImpl.instance.startAppResponseMap[it] = false
                    ConnectServiceImpl.instance.requestStartApp(it)
                    lastPackageName = it
                    startPackageName = ""

//                    if("com.baidu.BaiduMap".contentEquals(it)){
//                        delay(1000)
//                        ConnectServiceImpl.instance.requestStartApp(it)
//                    }
//                    delay(2000)
//                    if(ConnectServiceImpl.instance.startAppResponseMap[it] == false){//避免百度地图首次打开进到智慧桌面的地图(fixed华为bug)
//                        "startAppResponseMap[it] == false".logE(TAG)
//                        mBind.connectLoading.gone()
//                        showSuccessUi()
//                    }
                }
            } ?: kotlin.run {
                "onResume startPackageName.takeIf it?.isNotEmpty() == false".logE(TAG)
                if (ConnectServiceImpl.instance.currentStartApp?.isNotEmpty() == true) {
                    mBind.connectLoading.gone()
                    showSuccessUi()
                } else {
                    ConnectServiceImpl.instance.requestStartApp("com.huawei.hicar")
                }

            }

        }

    }

    /**
     * 防止onNewIntent多次回调
     */
    private fun isExecutable(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - onNewIntentRepeatTime >= 1000) {
            onNewIntentRepeatTime = currentTime
            return true
        }
        return false
    }

    private var mIsProhibitDialogShowing = false
    private fun showProhibitDialog(allow:Boolean) {

        "mType:$mType---->showProhibitDialog()---->mIsProhibitDialogShowing:$mIsProhibitDialogShowing---->allow:$allow".logE(TAG)

        if (mType != 6) {
            return
        }

        if (mIsProhibitDialogShowing && allow){
            ProhibitWrapper.instance.dismiss()
            mIsProhibitDialogShowing = false
        }

        if (!mIsProhibitDialogShowing && !allow){
            ProhibitWrapper.instance.show(this) {
                "prohibit surfaceActivity ".logE(TAG)
                moveTaskToBack(true)
                mIsProhibitDialogShowing = false
            }.startCountDown(lifecycleScope) {
                moveTaskToBack(true)
                mIsProhibitDialogShowing = false
            }
        }
    }

}