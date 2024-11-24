package com.zeekrlife.connect.core.data.repository

import android.annotation.IntDef
import com.huawei.hicarsdk.HiCarAdapter
import com.zeekrlife.common.util.GsonUtils
import com.zeekrlife.connect.core.data.request.AppBackgroundParams
import com.zeekrlife.connect.core.data.request.AppListParams
import com.zeekrlife.connect.core.data.request.AppParams
import com.zeekrlife.connect.core.data.request.ManualReconnectParams
import com.zeekrlife.connect.core.data.request.ProjectAction
import com.zeekrlife.connect.core.data.request.RequestApp
import com.zeekrlife.connect.core.data.response.UserInfo
import com.zeekrlife.connect.core.utils.VehicleUtil
import com.zeekrlife.net.api.NetUrl
import com.zeekrlife.net.interception.logging.util.logE
import rxhttp.wrapper.coroutines.Await
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.param.toResponse
import java.nio.charset.StandardCharsets

/**
 * 描述　: 数据仓库
 */
object UserRepository {

    /**
     * 车辆信息
     */
//    private val deviceInfo by lazy { CacheExt.getOpenApi()?.deviceInfo }

    /**
     * 车辆款式
     * DeviceApi.getInstance().vehicleModel
     */
    private val vehicleModel = /*deviceInfo?.getVehicleModel ?: */""

    /**
     * 车辆系列，必填,如：DC1E-012
     * DeviceApi.getInstance().vehicleType
     */
//    private val vehicleType = deviceInfo?.getVehicleType.run {
//        if (isNullOrEmpty()) "BX1E" else this
//    }


    /**
     * 登录
     */
    fun login(userName: String, password: String): Await<UserInfo> {
        return RxHttp.postForm(NetUrl.LOGIN)
            .add("username", userName)
            .add("password", password)
            .toResponse()
    }

    const val DATA_TYPE_HOTWORD = 2 // 车机侧唤醒HiCar语音交互
    const val DATA_TYPE_DAY_NIGHT_MODE = 501 // 深浅模式
    const val DATA_TYPE_BRAND_ICON_DATA = 502 // 车辆品牌名称及图标
    const val DATA_TYPE_NAV_FOCUS = 503 // 导航焦点
    const val DATA_TYPE_CALL_STATE_FOCUS = 504 // 通话状态
    const val DATA_TYPE_VOICE_STATE = 505 // 语音状态
    const val DATA_TYPE_DRIVING_MODE = 506 // 行驶状态
    const val DATA_TYPE_CAR_STATE = 507 // 低油低电低压状态
    const val DATA_TYPE_SERVICE_CHANNEL = 508 // 车服务通道
    const val DATA_TYPE_KEYCODE = 509 // 车机物理快捷键
    const val DATA_TYPE_SENSOR_DATA = 510 // 车辆传感器数据
    const val DATA_TYPE_NET_SERVICE = 511 // 共享上网
    const val DATA_TYPE_META_DATA_ABILITY = 514 // 元数据能力
    const val DATA_TYPE_MEDIA_META_DATA = 515 // 音频元数据
    const val DATA_TYPE_CALL_META_DATA = 516 // 通话元数据
    const val DATA_TYPE_NAV_META_DATA = 517 // 导航元数据（暂不支持）
    const val DATA_TYPE_USERACTIVEDISCONNECT = 518 // 主动断开车机连接通知
    const val DATA_TYPE_VOICE_PROMPT = 519 // 语音提示音播报通知
    const val DATA_TYPE_OPERATE_SCREEN = 521 // 车机屏幕息屏/亮屏
    const val DATA_TYPE_TAKE_PICTURE = 522 // 车辆摄像头拍照
    const val DATA_TYPE_SUPPORT_VIRTUAL_MODEM = 523 // 虚拟通话状态
    const val DATA_TYPE_AA_APPS = 528 // 融合桌面应用列表
    const val DATA_TYPE_AA_APP_OPERATION = 529 // 融合桌面应用操作
    const val DATA_TYPE_SCENCE_META_DATA = 530 // 场景元数据，包括天气、日历、情景智能、IoT等数据
    const val DATA_TYPE_MESSAGE_META_DATA = 531 // 消息元数据
    const val DATA_TYPE_SCREEN_CAST_AUTHORIZED = 532 // 融合桌面HiCar前后台切换
    const val DATA_TYPE_FOREGROUND_APP = 533 // HiCar当前的前台应用

    @Target(AnnotationTarget.EXPRESSION)
    @IntDef(DATA_TYPE_HOTWORD, DATA_TYPE_DAY_NIGHT_MODE, DATA_TYPE_BRAND_ICON_DATA, DATA_TYPE_NAV_FOCUS, DATA_TYPE_CALL_STATE_FOCUS, DATA_TYPE_VOICE_STATE, DATA_TYPE_DRIVING_MODE, DATA_TYPE_CAR_STATE, DATA_TYPE_SERVICE_CHANNEL, DATA_TYPE_KEYCODE, DATA_TYPE_SENSOR_DATA, DATA_TYPE_NET_SERVICE, DATA_TYPE_META_DATA_ABILITY, DATA_TYPE_MEDIA_META_DATA, DATA_TYPE_CALL_META_DATA, DATA_TYPE_NAV_META_DATA, DATA_TYPE_USERACTIVEDISCONNECT, DATA_TYPE_VOICE_PROMPT, DATA_TYPE_OPERATE_SCREEN, DATA_TYPE_TAKE_PICTURE, DATA_TYPE_SUPPORT_VIRTUAL_MODEM, DATA_TYPE_AA_APPS, DATA_TYPE_AA_APP_OPERATION, DATA_TYPE_SCENCE_META_DATA, DATA_TYPE_MESSAGE_META_DATA, DATA_TYPE_SCREEN_CAST_AUTHORIZED, DATA_TYPE_FOREGROUND_APP)
    @Retention(AnnotationRetention.SOURCE)
    annotation class CarDataType



    /**
     * 获取手机应用列表信息
     */
    fun requestHiCarAppList(hiCarAdapter: HiCarAdapter?) {
        val data = GsonUtils.toJson(AppListParams("all", VehicleUtil.getModeId()))
        "requestHiCarAppList sendCarData data:$data".logE("zzzRequestHiCarAppList")
        hiCarAdapter?.sendCarData(@CarDataType DATA_TYPE_AA_APPS, data.toByteArray(StandardCharsets.UTF_8))
    }

    /**
     * 打开手机应用
     */
    fun requestStartApp(hiCarAdapter: HiCarAdapter?,AppPackageName:String) {
        val data = GsonUtils.toJson(AppParams(RequestApp("start",AppPackageName)))
        "requestStartApp sendCarData data:$data".logE("zzzStartApp")
        hiCarAdapter?.sendCarData(@CarDataType DATA_TYPE_AA_APP_OPERATION, data.toByteArray(StandardCharsets.UTF_8))
    }

    /**
     * 融合桌面切到后台
     */
    fun requestBackground(hiCarAdapter: HiCarAdapter?,backGround:Boolean = true) {
        val data = GsonUtils.toJson(AppBackgroundParams(ProjectAction(backGround)))
        "requestBackground sendCarData data:$data".logE("zzzRequestBackground")
        hiCarAdapter?.sendCarData(@CarDataType DATA_TYPE_SCREEN_CAST_AUTHORIZED, data.toByteArray(StandardCharsets.UTF_8))
    }

    /**
     * 主动断开连接
     */
    fun requestManualReconnect(hiCarAdapter: HiCarAdapter?,isUserDisconnect:Int = 1) {
        val data = GsonUtils.toJson(ManualReconnectParams(isUserDisconnect = isUserDisconnect))
        "requestManualReconnect sendCarData data:$data".logE("zzzRequestManualReconnect")
        hiCarAdapter?.sendCarData(@CarDataType DATA_TYPE_USERACTIVEDISCONNECT, data.toByteArray(StandardCharsets.UTF_8))
    }

}

