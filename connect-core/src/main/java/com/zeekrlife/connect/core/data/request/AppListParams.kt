package com.zeekrlife.connect.core.data.request

/**
 * @author Lei.Chen29
 * @date 2022/5/20 15:39
 * description：
 */

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class AppListParams(
    val RequestData: String? = "",
    val RequestCaller: String? = ""
) : Parcelable

