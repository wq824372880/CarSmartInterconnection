/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */
package com.zeekrlife.hicar.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.zeekr.basic.finishAllActivity
import com.zeekrlife.common.ext.toStartActivity
import com.zeekrlife.hicar.R
import com.zeekrlife.hicar.app.base.BaseActivity
import com.zeekrlife.hicar.data.cache.CacheExt
import com.zeekrlife.hicar.databinding.ActivityHomeBinding
import com.zeekrlife.hicar.ui.fragment.PinFragment
import com.zeekrlife.hicar.ui.viewmodel.PinViewModel
import com.zeekrlife.hicar.utils.HiCarServiceManager
import com.zeekrlife.net.interception.logging.util.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : BaseActivity<PinViewModel, ActivityHomeBinding>() {
    private val TAG = "MainActivity"
    var booStartSurfaceFromApp = false
    var booPauseProjection = false
    var reConnectMac = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if(!CacheExt.getAgreeDisclaimer()) { //同意过免责声明直接进入主页    1120：认证要求去掉协议页
//            toStartActivity(LauncherActivity::class.java, Bundle())
//        }
    }


    override fun onResume() {
        super.onResume()
        "MainActivity333  onresume reConnectMac：$reConnectMac".logE(TAG)
    }
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        "MainActivity333  initView reConnectMac：$reConnectMac".logE(TAG)
    }

    override fun initObserver() {
    }

    override fun showToolBar(): Boolean {
        return false
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        val bundle: Bundle? = intent?.extras
        bundle?.takeIf {
            it.getBoolean(LauncherActivity.START_MAIN_ACTIVITY_FORM_LAUNCHER)
        }?.run {
            val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.fragment)
            "MainActivity fragment:${fragment}".logE(TAG)
            fragment?.takeIf {
                it is PinFragment
            }?.run {
                (this as PinFragment).initRegister()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        "MainActivity onDestroy".logE(TAG)

    }

    override fun onStop() {
        super.onStop()
        "MainActivity onStop".logE(TAG)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    //退出
    fun quit() {
        mViewModel.releasePageTypeObserve = true
        lifecycleScope.launch(Dispatchers.Default) {
            HiCarServiceManager.handleStoptAdv()
            HiCarServiceManager.disconnectDevice(PinFragment.mDeviceId)
            delay(500)
            launch(Dispatchers.Main) {
                "MainActivity finishAllActivity".logE(TAG)
//                finish()
                finishAllActivity()
                exitProcess(0)
            }
        }

    }

    fun moveBackTask(){
        moveTaskToBack(true)
    }
}