package com.zeekrlife.connect.core.data.cache

/**
 * 描述　:
 */
object ValueKey {

    //mmkv全局缓存key(退出登录清除缓存)
    const val MMKV_APP_KEY = "connect_core_app"
    //mmkv全局缓存key(退出登录不清除缓存)
    const val MMKV_APP_KEY_SAVE = "connect_core_app_save"

    //用户协议
    const val USER_AGREEMENT_INFO = "user_agreement_info"
    //隐私信息
    const val LAUNCHER_PROTOCOL_INFO = "launcher_protocol_info"

    //openAPi数据( 通过mmkv缓存 不用在异步去获取了)
    const val OPENAPI_INFO = "openapi_info"

    //当前用户是否显示过隐私弹窗
    const val IS_SHOWED_PRIVACY = "is_showed_privacy"

    const val USER_INFO = "user_info"

    //我的应用中已安装应用
    const val CACHE_MY_INSTALL_APPS = "MY_INSTALL_APPS"


    //每页请求数量
    const val REQUEST_PAGE_SIZE = 30

    //免责声明
    const val DISCLAIMER = "DISCLAIMER"
    //HiCar applist
    const val HICAR_APP_LIST = "hicar_app_list"
    //HiCar 手动断开连接
    const val HICAR_MANUAL_RECONNECT = "hicar_manual_reconnect"
    //HiCar 手动断开连接
    const val HICAR_LAST_TRUST_PHONE = "hicar_last_trust_phone"

}