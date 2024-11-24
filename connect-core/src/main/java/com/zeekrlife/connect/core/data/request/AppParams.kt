package com.zeekrlife.connect.core.data.request

/**
 * @author Lei.Chen29
 * @date 2022/5/20 15:39
 * description：
 */

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class AppParams(
    val RequestApp: RequestApp? = null
) : Parcelable

@Parcelize
data class RequestApp(
    val Action: String? = "",
    val AppPackage: String? = ""
) : Parcelable

