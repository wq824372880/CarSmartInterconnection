package com.zeekrlife.connect.core.data.response

/**
 * @author Lei.Chen29
 * @date 2022/5/20 15:39
 * description：
 */

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class AppRespInfo(
    val RequestAppResp: RequestAppResp? = null
) : Parcelable

@Parcelize
data class RequestAppResp(
    var RespCode: Int? = 0,
    val AppPackage: String? = null,
    val Description: String? = null,
) : Parcelable

