package com.zeekrlife.hicar.ui.activity

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.gyf.immersionbar.ktx.immersionBar
import com.zeekr.basic.finishAllActivity
import com.zeekrlife.common.ext.toStartActivity
import com.zeekrlife.hicar.R
import com.zeekrlife.hicar.app.aop.SingleClick
import com.zeekrlife.hicar.app.base.BaseActivity
import com.zeekrlife.hicar.data.cache.CacheExt
import com.zeekrlife.hicar.databinding.ActivityLauncherBinding
import com.zeekrlife.hicar.ui.viewmodel.LauncherViewModel
import com.zeekrlife.net.interception.logging.util.logE
import com.zeekrlife.net.load.LoadStatusEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class LauncherActivity : BaseActivity<LauncherViewModel, ActivityLauncherBinding>() {
    private var mBundle: Bundle = Bundle()

    private var mShowNoticeCountDown = true
    private val SHOWNOTICECOUNTDOWNKEY = "mShowNoticeCountDown"


    private val timer = AdvTimer(
        FRESH_PIN_INTERVAL,
        FRESH_DISPLAY_INTERVAL
    )
    companion object{
        const val START_MAIN_ACTIVITY_FORM_LAUNCHER = "start_main_activity_form_launcher"
        private const val TAG = "LauncherActivity"
        private const val FRESH_PIN_INTERVAL = (6 * 1000).toLong()
        private const val FRESH_DISPLAY_INTERVAL: Long = 1000
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        savedInstanceState?.let {
            mShowNoticeCountDown = it.getBoolean(SHOWNOTICECOUNTDOWNKEY)
        }
        if (mShowNoticeCountDown) {
            mShowNoticeCountDown = false
            timer.start()
        }else{
            mBind.tvConfirm.text = resources.getString(R.string.agree)
            mBind.tvConfirm.isClickable = true
            mBind.tvConfirm.alpha = 1.0F
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SHOWNOTICECOUNTDOWNKEY,mShowNoticeCountDown)
    }

    override fun onResume() {
        super.onResume()
        immersionBar {
            navigationBarColor(R.color.launcher_bg_color)
        }
    }

    override fun initObserver() {

    }

    override fun onBindViewClick() {
        mBind.tvCancel.setOnClickListener @SingleClick {
            lifecycleScope.launch {
                delay(500)
                finishAllActivity()
                exitProcess(0)
            }
        }

        mBind.tvConfirm.setOnClickListener @SingleClick {
            CacheExt.setAgreeDisclaimer()
            gotoMain()
        }
    }

    override fun onStop() {
        super.onStop()
        "LauncherActivity onStop".logE(TAG)
    }

    private fun gotoMain(){
        mBundle.putBoolean(START_MAIN_ACTIVITY_FORM_LAUNCHER,true)
        toStartActivity(MainActivity::class.java, mBundle)
        finish()
    }

    override fun onRequestEmpty(loadStatus: LoadStatusEntity) {
    }

    override fun showToolBar() = false
    internal inner class AdvTimer(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(millisUntilFinished: Long) {
            mBind.tvConfirm.isClickable = false
            mBind.tvConfirm.text = resources.getString(R.string.agree_countdown, (millisUntilFinished / 1000).toString())
            mBind.tvConfirm.alpha = 0.4F
        }

        override fun onFinish() {
            mBind.tvConfirm.text = resources.getString(R.string.agree)
            mBind.tvConfirm.isClickable = true
            mBind.tvConfirm.alpha = 1.0F
        }
    }
}