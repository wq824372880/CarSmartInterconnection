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
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.ext.finishCurrentActivity
import com.zeekrlife.common.ext.gone
import com.zeekrlife.common.ext.visible
import com.zeekrlife.connect.core.ConnectServiceImpl
import com.zeekrlife.connect.core.app.base.BaseActivity
import com.zeekrlife.connect.core.app.eventViewModel
import com.zeekrlife.connect.core.databinding.ActivityProtocolBinding
import com.zeekrlife.connect.core.databinding.ActivitySurfaceBinding
import com.zeekrlife.net.interception.logging.util.logE
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProtocolActivity : BaseActivity<BaseViewModel, ActivityProtocolBinding>() {
    private val TAG = "ProtocolActivity"
    var booStartSurfaceFromApp = false
    var booPauseProjection = false
    var startPackageName: String? = null
    var lastPackageName: String? = null

    companion object{
        var isFirstProjection = false
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ConnectServiceImpl.instance.currentStartApp = ""
        mBind.loadingContentTV.text = if(ConnectServiceImpl.instance.phoneName.isEmpty()) "正在连接..." else "正在连接 ${ConnectServiceImpl.instance.phoneName}"

//        showLoadingUi()
//        lifecycleScope.launch {
//            delay(1500)
//            showSuccessUi()
//        }
//        startPackageName = intent?.extras.let {
//            it?.getString("packageName")
//        }
//        if (startPackageName.isNullOrEmpty()) {
//            showSuccessUi()
//            mBind.connectLoading.visible()
//            booStartSurfaceFromApp = false
//            ConnectServiceImpl.instance.currentStartApp = ""
//        } else {
//            mBind.connectLoading.gone()
//            showLoadingUi()
//            booStartSurfaceFromApp = true
//            ConnectServiceImpl.instance.currentStartApp = startPackageName
//        }
//        booPauseProjection = true
//
//        mBind.connectLoading.visible()
//        mBind.loadingContentTV.text = "正在连接..."
//        "SurfaceActivity initView() startPackageName:$startPackageName".logE(TAG)
        isFirstProjection = true
        startProjectionForApp()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        ConnectServiceImpl.instance.currentStartApp = ""
        mBind.loadingContentTV.text = if(ConnectServiceImpl.instance.phoneName.isEmpty()) "正在连接..." else "正在连接 ${ConnectServiceImpl.instance.phoneName}"
//        if (isExecutable()) {
//            setIntent(intent)
//            startPackageName = intent?.extras.let {
//                it?.getString("packageName")
//            }
//            if (startPackageName.isNullOrEmpty() && ConnectServiceImpl.instance.currentStartApp.isNullOrEmpty()) { //刚开始投屏
//                "SurfaceActivity initView() lastPackageName:${ConnectServiceImpl.instance.currentStartApp}".logE(
//                    TAG
//                )
//                showSuccessUi()
//                mBind.connectLoading.visible()
//                booPauseProjection = true
//                booStartSurfaceFromApp = true
//            } else if (startPackageName.isNullOrEmpty() && !ConnectServiceImpl.instance.currentStartApp.isNullOrEmpty()) { //当前已打开并且没有重新打开别的app
//                mBind.connectLoading.gone()
//                showSuccessUi()
//                booPauseProjection = false
//                booStartSurfaceFromApp = false
//            } else { //打开app
//                mBind.connectLoading.gone()
//                showLoadingUi()
//                booPauseProjection = true
//                booStartSurfaceFromApp = true
//            }
//            "SurfaceActivity onNewIntent startPackageName:$startPackageName,booPauseProjection:$booPauseProjection".logE(TAG)
//        }
//        startProjectionForApp()

    }

    override fun onResume() {
        super.onResume()
        "ProtocolActivity onResume()".logE(TAG)
//        startProjectionForApp()
//        showLoadingUi()
//        lifecycleScope.launch {
//            delay(1000)
//            showSuccessUi()
//        }
    }

    override fun onPause() {
        super.onPause()
        "ProtocolActivity onPause()".logE(TAG)
//        if (startPackageName?.isNotEmpty() == true) {
//            lastPackageName = startPackageName
//        }
    }

    override fun onStop() {
        super.onStop()
        "ProtocolActivity onStop()".logE(TAG)
    }

    override fun initObserver() {
        mBind.surface.viewTreeObserver.addOnGlobalLayoutListener(mGlobalLayoutListener)
//        backgroundObserver()
        finishActivityObserver()
    }

    override fun showToolBar(): Boolean {
        return false
    }


    override fun onDestroy() {
        super.onDestroy()
        "ProtocolActivity onDestroy".logE(TAG)
        ConnectServiceImpl.instance.pauseProjection()
        mBind.surface.viewTreeObserver.removeOnGlobalLayoutListener(mGlobalLayoutListener)

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
                    if (eventViewModel.background.value == false) {
                        ConnectServiceImpl.instance.disconnectDevice(ConnectServiceImpl.connectedDevice)
                        isFirstProjection = false
                        this@ProtocolActivity.finish()
                    }
                }
            })
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }



    private fun startProjectionForApp() {
        "startProjectionForApp booPauseProjection = $booPauseProjection,startPackageName = $startPackageName".logE(
            TAG
        )
        lifecycleScope.launch {
            delay(500)
            if (ConnectServiceImpl.instance
                    .updateCarConfig(mSurfaceHolder?.surface, surfaceWidth, surfaceHeight)
            ) {
                " surfaceChanged startProjection调用了".logE(TAG)
                ConnectServiceImpl.instance.startProjection()
            }
            delay(2000)
//            showSuccessUi()
            mBind.connectLoading.gone()

        }

    }

    private fun finishActivityObserver() {
        eventViewModel.finishActivityEvent.observe(this){
            if (it) {
                "finishActivityObserver finishCurrentActivity".logE(TAG)
//                lifecycleScope.launch{
//                    delay(800)
//                    booPauseProjection = true
//                    ConnectServiceImpl.instance.pauseProjection()
//                    finishCurrentActivity(this@ProtocolActivity)
//                }
                //                    delay(800)
                    booPauseProjection = true
                    ConnectServiceImpl.instance.pauseProjection()
                    finishCurrentActivity(this@ProtocolActivity)
            }
        }
    }

}