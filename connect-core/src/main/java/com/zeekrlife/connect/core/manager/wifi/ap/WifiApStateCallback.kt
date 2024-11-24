package com.zeekrlife.connect.core.manager.wifi.ap

/**
Date:2022/11/21
@author:e-Dongdong.Qiu
 */
interface WifiApStateCallback {

    /**
     * 热点开启中
     */
    fun wifiApOpening()

    /**
     * 热点已经开启
     */
    fun wifiApOpenEd()

    /**
     * 热点关闭中
     */
    fun wifiApClosing()

    /**
     * 热点已经关闭
     */
    fun wifiApClosed()

    /**
     * 热点激活的接口集合
     */

    fun wifiApActive(isEmpty: Boolean)


    /**
     * 热点开始或者关闭过程中是否出错
     */

    fun wifiApError(isError: Boolean)

    /**
     * 热点开启失败
     */
    fun wifiApOpenFailed()

}