package com.zeekrlife.connect.core.app

import android.util.Log
import com.effective.android.anchors.task.Task
import com.effective.android.anchors.task.TaskCreator
import com.effective.android.anchors.task.project.Project
import com.huawei.authagent.service.utils.LogUtils
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadSir
import com.tencent.mmkv.MMKV
import com.zeekr.car.adaptapi.CarApiProxy
import com.zeekr.car.api.DeviceApiManager
import com.zeekr.car.api.MediaCenterApiManager
import com.zeekr.car.api.UserApiManager
import com.zeekr.car.api.VehicleApiManager
import com.zeekr.sdk.base.ApiReadyCallback
import com.zeekr.sdk.device.impl.DeviceAPI
import com.zeekr.sdk.keepalive.impl.KeepAliveAPI
import com.zeekr.sdk.keepalive.support.IKeepAliveProcess
import com.zeekr.sdk.keepalive.support.KeepAliveLevel
import com.zeekr.sdk.keepalive.support.KeepAliveStrategy
import com.zeekrlife.common.widget.state.BaseEmptyCallback
import com.zeekrlife.common.widget.state.BaseErrorCallback
import com.zeekrlife.common.widget.state.BaseLoadingCallback
import com.zeekrlife.connect.core.manager.globalsettings.GlobalSetting
import com.zeekrlife.connect.core.utils.VehicleUtil
import com.zeekrlife.net.interception.logging.util.logE

object TaskCreate : TaskCreator {
    override fun createTask(taskName: String): Task {
        return when (taskName) {
            InitComm.TASK_ID -> InitComm()
            InitUtils.TASK_ID -> InitUtils()
            InitToast.TASK_ID -> InitToast()
            InitMarketTask.TASK_ID -> InitMarketTask()
            InitDataProviderTask.TASK_ID -> InitDataProviderTask()
            InitZeekrCommonAPI.TASK_ID -> InitZeekrCommonAPI()
            InitAdapterAPI.TASK_ID -> InitAdapterAPI()
            else -> InitDefault()
        }
    }
}

class InitDefault : Task(TASK_ID, true) {
    companion object {
        const val TASK_ID = "0"
    }

    override fun run(name: String) {

    }
}

//初始化常用控件类
class InitComm : Task(TASK_ID, true) {
    companion object {
        const val TASK_ID = "2"
    }

    override fun run(name: String) {
        //注册界面状态管理
        LoadSir.beginBuilder()
            .addCallback(BaseErrorCallback())
            .addCallback(BaseEmptyCallback())
            .addCallback(BaseLoadingCallback())
            .setDefaultCallback(SuccessCallback::class.java)
            .commit()
    }
}

//初始化Utils
class InitUtils : Task(TASK_ID, false) {
    companion object {
        const val TASK_ID = "3"
        var globalSettingCurrentCastType = 0
    }

    override fun run(name: String) {
        //初始化Log打印
        MMKV.initialize(App.application)
        GlobalSetting.observerCaseType()
        GlobalSetting.observerNavFocus()
    }
}

//初始化Utils
class InitToast : Task(TASK_ID, true) {
    companion object {
        const val TASK_ID = "4"
    }

    override fun run(name: String) {
        //初始化吐司 这个吐司必须要主线程中初始化
//        ToastUtils.init(appContext)
//        ToastUtils.setGravity(Gravity.BOTTOM, 0, 100.dp)
    }
}

//初始化下载、更新服务
class InitMarketTask : Task(TASK_ID, false) {
    companion object {
        const val TASK_ID = "5"
    }

    override fun run(name: String) {
    }
}

//初始化埋点
class InitDataProviderTask : Task(TASK_ID, false) {
    companion object {
        const val TASK_ID = "6"
    }

    override fun run(name: String) {
    }
}

class InitZeekrCommonAPI : Task(TASK_ID, false) {
    companion object {
        const val TASK_ID = "9"
        var mUserApiReady = false
        var mDeviceApiReady = false
        var mNaviApiReady = false
    }

    override fun run(name: String) {


        UserApiManager.getInstance().userAPI.init(App.application) { p0, _ ->
            if (p0) {
                mUserApiReady = p0
            }
        }


        DeviceApiManager.getInstance().deviceAPI.init(App.application) { p0, _ ->

             if (p0) {
                mDeviceApiReady = p0
             }
        }


        //车辆能力实例化
        VehicleApiManager.getInstance().init(App.application)

        //媒体中心初始化
        MediaCenterApiManager.getInstance().init(App.application)

        //AdaptApi实例化 (DC1E上初始化如果是子线程报错)
        CarApiProxy.getInstance(App.application)
        //保活
        registerKeepAlive()

    }
}

class InitAdapterAPI : Task(TASK_ID, false) {
    companion object {
        const val TASK_ID = "10"
    }

    override fun run(name: String) {
//        if( ConnectServiceManager.getInstance().ensureServiceAvailable()){
//            ConnectServiceManager.getInstance().loadHiCarSDK()
//        }
//         ConnectServiceManager.getInstance().initConnectService(appContext)
    }
}


private fun registerKeepAlive() {
    try {
        KeepAliveAPI.get().init(App.application, ApiReadyCallback { isReady, msg ->

            /**
             * @param isReady 是否初始化成功。true:初始化成功；false:初始化失败
             * @param msg 初始化失败时的原因说明。
             */
            /**
             * @param isReady 是否初始化成功。true:初始化成功；false:初始化失败
             * @param msg 初始化失败时的原因说明。
             */
            LogUtils.e(
                "zzzApp",
                "KeepAliveAPI onAPIReady isReady: $isReady; msg: $msg"
            )
            if (!isReady) {
                return@ApiReadyCallback
            }
            val iKeepAliveProcess: IKeepAliveProcess = object : IKeepAliveProcess {
                override fun getPackageName(): String {
                "ConnectService startService mVersionCode:${App.application.packageManager.getPackageInfo(
                    App.application.packageName,0).versionCode}".logE("zzzApp")
                    return App.application.packageName
                }

                override fun getAction(): String {
                    return "zeekr.intent.action.ConnectService_SERVICE_START"
                }

                override fun getLevel(): KeepAliveLevel {
                    return KeepAliveLevel.LEVEL_2
                }

                override fun getStrategy(): KeepAliveStrategy {
                    return KeepAliveStrategy.START_SERVICE_STRATEGY
                }
            }
            KeepAliveAPI.get().registerKeepAliveProcess(iKeepAliveProcess)
        })
    } catch (thr: Throwable) {
        thr.printStackTrace()
        LogUtils.e(
            "zzzApp",
            "registerKeepAlive error: " + Log.getStackTraceString(thr)
        )
    }
}


class AppTaskFactory : Project.TaskFactory(TaskCreate)
