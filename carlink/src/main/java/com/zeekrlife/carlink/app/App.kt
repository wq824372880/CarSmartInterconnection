package com.zeekrlife.carlink.app

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.multidex.MultiDex
import com.effective.android.anchors.AnchorsManager
import com.effective.android.anchors.anchors
import com.effective.android.anchors.graphics
import com.effective.android.anchors.startUp
import com.effective.android.anchors.taskFactory
import com.zeekr.basic.Common
import com.zeekr.basic.currentProcessName
import com.zeekrlife.carlink.ui.viewmodel.EventViewModel
import com.zeekrlife.carlink.utils.CrashHandler
import com.zeekrlife.carlink.BuildConfig
import com.zeekrlife.carlink.utils.StringResourceProvider

//Application全局的ViewModel，用于发送全局通知操作
val eventViewModel: EventViewModel by lazy { App.eventViewModelInstance }

open class App : Application(), ViewModelStoreOwner {

    companion object {
        lateinit var eventViewModelInstance: EventViewModel
    }

    private lateinit var mAppViewModelStore: ViewModelStore

    private var mFactory: ViewModelProvider.Factory? = null

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()
        CrashHandler.getInstance().init()
        Common.init(this, BuildConfig.DEBUG)
        StringResourceProvider.init(this) //字符串资源获取类
        mAppViewModelStore = ViewModelStore()
        val processName = currentProcessName
        if (currentProcessName == packageName) {
            // 主进程初始化
            onMainProcessInit()
        } else {
            // 其他进程初始化
            processName?.let { onOtherProcessInit(it) }
        }
    }

    /**
     * @description  代码的初始化请不要放在onCreate直接操作，按照下面新建异步方法
     */
    private fun onMainProcessInit() {
        eventViewModelInstance = getAppViewModelProvider().get(EventViewModel::class.java)

        AnchorsManager.getInstance()
            .debuggable(BuildConfig.DEBUG)
            .taskFactory { AppTaskFactory() }
            //设置锚点
            .anchors {
                arrayOf(
                    InitMarketTask.TASK_ID, InitDataProviderTask.TASK_ID, InitZeekrCommonAPI.TASK_ID
                )
            }
            .graphics {
                arrayOf(
                    InitDefault.TASK_ID,
                    InitComm.TASK_ID, InitUtils.TASK_ID, InitToast.TASK_ID,
                    InitMarketTask.TASK_ID, InitDataProviderTask.TASK_ID,
                    InitZeekrCommonAPI.TASK_ID, InitAdapterAPI.TASK_ID
                )
            }
            .startUp()

    }

    /**
     * 其他进程初始化，[processName] 进程名
     */
    private fun onOtherProcessInit(processName: String) {

    }

    /**
     * 获取一个全局的ViewModel
     */
    private fun getAppViewModelProvider(): ViewModelProvider {
        return ViewModelProvider(this, this.getAppFactory())
    }

    private fun getAppFactory(): ViewModelProvider.Factory {
        if (mFactory == null) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(this)
        }
        return mFactory as ViewModelProvider.Factory
    }

    override fun getViewModelStore(): ViewModelStore {
        return mAppViewModelStore
    }

}