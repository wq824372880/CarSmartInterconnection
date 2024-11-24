package com.zeekrlife.connect.core.data.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ManualReconnectParams(
    val isUserDisconnect: Int? = 0

) : Parcelable