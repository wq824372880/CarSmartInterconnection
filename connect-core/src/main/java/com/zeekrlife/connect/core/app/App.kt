package com.zeekrlife.connect.core.app

import android.app.Application
import android.content.Context
import androidx.annotation.Keep
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
import com.zeekrlife.connect.core.BuildConfig
import com.zeekrlife.connect.core.ui.viewmodel.EventViewModel

//Application全局的ViewModel，用于发送全局通知操作
val eventViewModel: EventViewModel by lazy { App.eventViewModelInstance }
@Keep
open class App : Application(), ViewModelStoreOwner {

    companion object {
        lateinit var application: Application
        lateinit var eventViewModelInstance: EventViewModel
    }

    private lateinit var mAppViewModelStore: ViewModelStore

    private var mFactory: ViewModelProvider.Factory? = null

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
        mAppViewModelStore = ViewModelStore()
        eventViewModelInstance = getAppViewModelProvider()[EventViewModel::class.java]
    }
    @Keep
    override fun onCreate() {
        super.onCreate()
        application = this
        Common.init(this, BuildConfig.DEBUG)
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
        AnchorsManager.getInstance()
            .debuggable(BuildConfig.DEBUG)
            .taskFactory { AppTaskFactory() }
            //设置锚点
            .anchors {
                arrayOf(
                    InitMarketTask.TASK_ID, InitUtils.TASK_ID, InitDataProviderTask.TASK_ID, InitZeekrCommonAPI.TASK_ID
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