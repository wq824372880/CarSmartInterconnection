package com.zeekrlife.hicar.data.response

/**
 * @author Lei.Chen29
 * @date 2022/5/20 15:39
 * description：
 */

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class AppBackageRespInfo(
    val projectActionResp: ProjectActionResp? = null
) : Parcelable

@Parcelize
data class ProjectActionResp(
    val RespCode: Int? = 0
) : Parcelable

