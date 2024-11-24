package com.zeekrlife.connect.core.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class LastTrustPhoneInfo(
    val mPhoneId: String? = null,
    val mPhoneName: String? = null,
    val mPhoneBrMac: String? = null,
    val mLastConnectTime: Long = 0
) : Parcelable