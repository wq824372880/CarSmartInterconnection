/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */
package com.zeekrlife.connect.core.constants

/**
 * common parameter
 */
object ConstAudio {
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
}