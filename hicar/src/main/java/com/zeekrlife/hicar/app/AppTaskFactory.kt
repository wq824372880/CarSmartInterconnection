package com.zeekrlife.hicar.app
import com.effective.android.anchors.task.Task
import com.effective.android.anchors.task.TaskCreator
import com.effective.android.anchors.task.project.Project
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadSir
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.mmkv.MMKV
import com.zeekr.car.adaptapi.CarApiProxy
import com.zeekr.car.api.DeviceApiManager
import com.zeekr.car.api.UserApiManager
import com.zeekr.car.api.VehicleApiManager
import com.zeekr.sdk.mediacenter.impl.MediaCenterAPI
import com.zeekr.sdk.policy.impl.PolicyAPI
import com.zeekrlife.common.widget.refresh.CustomRefreshFooter
import com.zeekrlife.common.widget.refresh.CustomRefreshHeader
import com.zeekrlife.common.widget.state.BaseEmptyCallback
import com.zeekrlife.common.widget.state.BaseErrorCallback
import com.zeekrlife.common.widget.state.BaseLoadingCallback
import com.zeekrlife.connect.base.manager.ConnectServiceManager
import com.zeekrlife.net.interception.logging.util.logE
import java.util.*


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
        SmartRefreshLayout.setDefaultRefreshInitializer { _, layout ->
            //设置 SmartRefreshLayout 通用配置
            layout.setEnableScrollContentWhenLoaded(true)//是否在加载完成时滚动列表显示新的内容
            layout.setFooterTriggerRate(0.6f)
        }
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ ->
            //设置 Head
            CustomRefreshHeader(context)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            //设置 Footer
            CustomRefreshFooter(context)
        }
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
class InitUtils : Task(TASK_ID, true) {
    companion object {
        const val TASK_ID = "3"
    }

    override fun run(name: String) {
        //初始化Log打印
        MMKV.initialize(App.application)
//        CacheExt.mmkvInitialize()
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
        const val TAG = "InitZeekrCommonAPI"
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
//        MediaCenterApiManager.getInstance().init(appContext)

        //AdaptApi实例化 (DC1E上初始化如果是子线程报错)
        CarApiProxy.getInstance(App.application)

        MediaCenterAPI.get().init(App.application) { po, _ ->
            "mediaCenterApi init $po".logE(TAG)
        }

        PolicyAPI.get().init(App.application){p0,_->
            "PolicyApi init $p0".logE(TAG)
        }

    }
}

class InitAdapterAPI : Task(TASK_ID, true) {
    companion object {
        const val TASK_ID = "10"
    }

    override fun run(name: String) {
         ConnectServiceManager.getInstance().initConnectService(App.application)
    }
}




class AppTaskFactory : Project.TaskFactory(TaskCreate)

/**
 * 模拟初始化SDK
 * @param millis Long
 */
fun doJob(millis: Long) {
    val nowTime = System.currentTimeMillis()
    while (System.currentTimeMillis() < nowTime + millis) {
        //程序阻塞指定时间
        val min = 10
        val max = 99
        val random = Random()
        val num = random.nextInt(max) % (max - min + 1) + min
    }
}