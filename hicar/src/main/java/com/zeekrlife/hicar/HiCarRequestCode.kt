package com.zeekrlife.hicar

/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description:
 *@author: Yueming.Zhao
 *@date: 2023/7/7 11:23:04
 *@version: V1.0
 */
object HiCarRequestCode {
        /**
         * 语音通话
         */
        const val STREAM_VOICE_CALL = 0

        /**
         * 系统音
         */
        const val STREAM_SYSTEM = 1

        /**
         * 铃声
         */
        const val STREAM_RING = 2

        /**
         * 媒体
         */
        const val STREAM_MUSIC = 3

        /**
         * 警报
         */
        const val STREAM_ALARM = 4

        /**
         * 通知音
         */
        const val STREAM_NOTIFICATION = 5

        /**
         * Used to identify the volume of audio streams for phone calls when connected on bluetooth
         */
        const val STREAM_BLUETOOTH_SCO = 6

        /**
         * 系统强制音
         */
        const val STREAM_SYSTEM_ENFORCED = 7

        /**
         * 双音多频
         */
        const val STREAM_DTMF = 8

        /**
         * 辅助音
         */
        const val STREAM_ACCESSIBILITY = 10

        /**
         * content type unknown
         */
        const val CONTENT_TYPE_UNKNOWN = 0

        /**
         * Content type value to use when the content type is speech.
         */
        const val CONTENT_TYPE_SPEECH = 1

        /**
         * Content type value to use when the content type is music.
         */
        const val CONTENT_TYPE_MUSIC = 2

        /**
         * Content type value to use when the content type is a soundtrack, typically accompanying
         * a movie or TV program.
         */
        const val CONTENT_TYPE_MOVIE = 3

        /**
         * Content type value to use when the content type is a sound used to accompany a user
         * action, such as a beep or sound effect expressing a key click, or event, such as the
         * type of a sound for a bonus being received in a game. These sounds are mostly synthesized
         * or short Foley sounds.
         */
        const val CONTENT_TYPE_SONIFICATION = 4

        /**
         * start adv message
         */
        const val MSG_START_ADV = 2

        /**
         * stop adv message
         */
        const val MSG_STOP_ADV = 3


        /**
         * onDataReceive事件
         */
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

}