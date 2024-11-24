package com.zeekrlife.carlink.app.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.gyf.immersionbar.ImmersionBar
import com.zeekr.basic.finishAllActivity
import com.zeekrlife.common.base.BaseVBActivity
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.util.IntentUtils
import com.zeekrlife.carlink.R
import com.zeekrlife.carlink.app.widget.CustomToolBar
import kotlin.system.exitProcess

/**
 * 描述　: 新创建的 使用 ViewBinding 需要自定义修改什么就重写什么 具体方法可以 搜索 BaseIView 查看
 */
abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding> : BaseVBActivity<VM, VB>(){

    lateinit var mToolbar: CustomToolBar
    private var closeAppReceiver: BroadcastReceiver? = null
    protected var hasLauncher = false
    protected var isActive = false
    protected var realPath :String? = ""

     companion object{
        const val REAL_PATH = "INTERNAL_realPath"
        const val RAW_URI = "NTeRQWvye18AkPd6G"
        const val ACTION_ECARX_VR_APP_CLOSE = "ecarx.intent.broadcast.action.ECARX_VR_APP_CLOSE"
        const val CATEGORY_ECARX_VR_APP_CLOSE_STORE = "ecarx.intent.broadcast.category.ECARX_VR_APP_CLOSE_STORE"
        const val EXTRA_NAME_CLOSE_TYPE = "close_type"
        const val FROM_KEY = "from_key"
        const val MEMBER_CENTER_PACKAGE_NAME = "ecarx.membercenter"
        const val EXTRA_VALUE_MOVE_TASK_TO_BACK = 0 // 退到后台
        const val EXTRA_VALUE_EXIT_APPLICATION = 1 // 完全退出
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (hasLauncher) {
            try {
                // 设置vr监听
                closeAppReceiver = CloseAppReceiver()
                val intentFilter = IntentFilter(ACTION_ECARX_VR_APP_CLOSE)
                intentFilter.addCategory(CATEGORY_ECARX_VR_APP_CLOSE_STORE)
                registerReceiver(closeAppReceiver, intentFilter)
            } catch (throwable: Throwable) {
                // empty
            }
        }
    }


    override fun getTitleBarView(): View? {
        val titleBarView = LayoutInflater.from(this).inflate(R.layout.layout_titlebar_view, null)
        mToolbar = titleBarView.findViewById(R.id.customToolBar)
        return titleBarView
    }

    override fun initImmersionBar() {
        ImmersionBar.with(this).statusBarDarkFont(true).navigationBarColor(R.color.theme_main_background_color).init()
        //设置共同沉浸式样式
        if (showToolBar()) {
//            mToolbar.setBackgroundResource(R.color.white)
            ImmersionBar.with(this).titleBar(mToolbar).init()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        hasLauncher = IntentUtils.getLaunchAppIntent(packageName) != null


    }

    override fun onStart() {
        super.onStart()
        isActive = true
    }

    override fun onStop() {
        super.onStop()
        isActive = false
    }

    override fun onDestroy() {
        super.onDestroy()

        if (hasLauncher) {
            try {
                unregisterReceiver(closeAppReceiver)
            } catch (throwable: Throwable) {
                //
            } finally {
                closeAppReceiver = null
            }
        }
    }


    inner class CloseAppReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent == null) {
                return
            }
            if (MEMBER_CENTER_PACKAGE_NAME != intent.getStringExtra(FROM_KEY)) {
                // 根据from_key判断, 不符合用户中心包名的不处理，只能通过用户中心打开或者关闭应用市场
                return
            }
            if (ACTION_ECARX_VR_APP_CLOSE == intent.action) {
                when (intent.getIntExtra(EXTRA_NAME_CLOSE_TYPE, EXTRA_VALUE_EXIT_APPLICATION)) {
                    EXTRA_VALUE_MOVE_TASK_TO_BACK -> moveTaskToBack(true)
                    else -> {
                        finishAllActivity()
                        exitProcess(0)
                    }
                }
            }
        }


    }
}