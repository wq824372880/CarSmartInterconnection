package com.zeekrlife.connect.core.data.request

/**
 * @author Lei.Chen29
 * @date 2022/5/20 15:39
 * description：
 */

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class AppBackgroundParams(
    val ProjectAction: ProjectAction? = null
) : Parcelable

@Parcelize
data class ProjectAction(
    val Background: Boolean = false
) : Parcelable

