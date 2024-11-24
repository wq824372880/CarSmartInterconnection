package com.zeekr.car.api.partnum

import android.util.Log
import com.zeekr.car.util.SystemProperties

/**
 * @author Lei.Chen29
 * @date 2023/6/2 14:35
 * description：零件号版本
 */
class DefPartNumVersion : PartNumVersion {

    /**
     * 系统零件号
     */
    override fun systemPartNumVersion(): String {
        try {
            val versionNumber = SystemProperties.get("ro.product.build.version_number", "")
            if (versionNumber.length > 4) {
                val vNumber = versionNumber.takeLast(4)
                if (isNumber(vNumber)) {
                    return vNumber
                }
            }
        } catch (e: Exception) {
            Log.e("DefPartNumVersion", "get systemPartNumVersion exception:${Log.getStackTraceString(e)}")
        }
        return ""
    }

    private fun isNumber(s: String?): Boolean {
        return !s.isNullOrEmpty() && s.matches(Regex("\\d+"))
    }

}