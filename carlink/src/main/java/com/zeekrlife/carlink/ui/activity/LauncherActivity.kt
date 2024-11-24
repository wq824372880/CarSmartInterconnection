package com.zeekrlife.carlink.ui.activity

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Process
import androidx.lifecycle.lifecycleScope
import com.gyf.immersionbar.ktx.immersionBar
import com.zeekrlife.common.ext.getColorExt
import com.zeekrlife.common.ext.toStartActivity
import com.zeekrlife.carlink.R
import com.zeekrlife.carlink.app.aop.SingleClick
import com.zeekrlife.carlink.app.base.BaseActivity
import com.zeekrlife.carlink.data.cache.CacheExt
import com.zeekrlife.carlink.data.response.OpenApiDeviceInfo
import com.zeekrlife.carlink.data.response.OpenApiUserInfo
import com.zeekrlife.carlink.data.response.ProtocolInfoBean
import com.zeekrlife.carlink.databinding.ActivityLauncherBinding
import com.zeekrlife.carlink.ui.viewmodel.LauncherViewModel
import com.zeekrlife.net.load.LoadStatusEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class LauncherActivity : BaseActivity<LauncherViewModel, ActivityLauncherBinding>() {
    var userApiInfo: OpenApiUserInfo? = null
    var deviceApiInfo: OpenApiDeviceInfo? = null
    var userAgreement: ProtocolInfoBean? = null //用户协议
    var protocolInfo: ProtocolInfoBean? = null //隐私信息
    private var mBundle: Bundle = Bundle()
    private val timer = AdvTimer(
        FRESH_PIN_INTERVAL,
        FRESH_DISPLAY_INTERVAL
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        if(CacheExt.getAgreeDisclaimer()) { //同意过免责声明直接进入主页
            gotoMain()
        }
        super.onCreate(savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
//        mViewModel.getOpenApiInfo(UserAPI.get(), DeviceAPI.get())
        mBind.tvBgZeekr.apply {
            val colorArray =
                intArrayOf(getColorExt(R.color.color_33656F80), getColorExt(R.color.transparent), getColorExt(R.color.transparent))
            val positionArray = floatArrayOf(0f, 0.5f, 0.9f)
            paint.shader = LinearGradient(
                0f, 0f, 0f, measuredHeight.toFloat(),
                colorArray, positionArray, Shader.TileMode.REPEAT
            )
        }
        timer.start()
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
                finish()
            }
        }

        mBind.tvConfirm.setOnClickListener @SingleClick {
            CacheExt.setAgreeDisclaimer()
            gotoMain()
        }
    }

    private fun gotoMain(){
        toStartActivity(MainActivity::class.java, mBundle)
        finish()
    }

    override fun onRequestEmpty(loadStatus: LoadStatusEntity) {
    }

    override fun showToolBar() = false
    internal inner class AdvTimer(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(millisUntilFinished: Long) {

            mBind.tvConfirm.setBackgroundColor(Color.parseColor("#B6B6D8"))
            mBind.tvConfirm.isClickable = false
            mBind.tvConfirm.text = resources.getString(R.string.agree_countdown, (millisUntilFinished / 1000).toString())
            mBind.tvConfirm.alpha = 0.5F
        }

        override fun onFinish() {
            mBind.tvConfirm.text = resources.getString(R.string.agree)
            mBind.tvConfirm.isClickable = true
            mBind.tvConfirm.alpha = 1.0F
        }
    }
    companion object {
        private const val TAG = "LauncherActivity"
        private const val FRESH_PIN_INTERVAL = (5 * 1000).toLong()
        private const val FRESH_DISPLAY_INTERVAL: Long = 1000
    }
}