package com.zeekrlife.connect.core.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ManualReconnectInfo(
    var isUserDisconnect: Int? = 0
) : Parcelable