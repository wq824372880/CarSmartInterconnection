package com.zeekrlife.hicar.data

/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description: 页面状态
 *@author: e-Yang.Dong1
 *@date: 2023/5/10 13:40:58
 *@version: V1.0
 */
enum class PageType {
    //开启蓝牙
    BLUETOOTHl_TURN_ON,
    //获取连接码loading页面
    CONNECTION_CODE_GET,
    //获取连接码失败
    CONNECTION_CODE_GET_FAILED,
    //HICarPinCode连接页面
    HICAR_CONNECT,
    //HICar正在连接...
    HICAR_CONNECTING,
    //HIcar连接失败
    HICAR_CONNECTION_FAILED,
    //断开连接
    HICAR_DISCONNECT,
    //进入app的加载页
    APP_ENTER_LOADING,
    //hicar-app的投屏展示
    APP_SHOWING,
    //hicar-app加载失败
    APP_LOAD_FAILED,
}

enum class DialogType {
    //切换设备（carlink连接中是否断开当前carlink设备连接）
    CARLINK_DISCONNECT,
    //显示设备切换（不支持双开，从手机切换到车机）
    DEVICE_HANDOFF,
    //档位限制，视频播放
    GEAR_LIMIT,
}

enum class LoadingType {
    //hicar-连接码获取中
    CONNECTION_CODE_GETTING,
    //hicar-连接中
    HICAR_CONNECTING,
}