package com.zeekrlife.connect.core.data.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description:
 *@author: Yueming.Zhao
 *@date: 2023/7/21 14:38:30
 *@version: V1.0
 */
@Parcelize
class VoiceStateParams(val frontEndInfo:FrontEndInfo) : Parcelable {


    @Parcelize
    data class FrontEndInfo(val wakeType:String,
                            val intent:String,
                            val voice:Voice,
                            val centralControl:CentralControl ?=null,
                            val car:Car ?= null) : Parcelable {
    //语音信息
    @Parcelize
    data class Voice(val wakeWord:String,
                     val vendorName:String = "",
                     val version:String = "",
                     val infoToVendorCloud:String="") : Parcelable

    //车载中控信息
    @Parcelize
    data class CentralControl(val vendorName:String,val version:String) : Parcelable

    @Parcelize
    data class Car(val micNumber:Int,val speakerNumber:Int) : Parcelable
    }
}