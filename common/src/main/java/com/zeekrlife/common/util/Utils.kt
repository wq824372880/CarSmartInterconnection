package com.zeekrlife.common.util

import android.graphics.Color
import android.os.Environment
import android.os.StatFs
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import com.zeekr.basic.appContext
import java.util.Locale
import java.util.regex.Pattern

object Utils {
    private const val MIN_NEED_SPACE = 250 * 1024 * 1024 //250M

    /**
     * 将云端返回的大小转换为byte单位
     *
     * @param size
     * @return
     */
//    fun castSizeToBytes(size: String): Long {
//        try {
//            if (TextUtils.isEmpty(size)) {
//                return 0
//            }
//            if (size.contains("K") || size.contains("KB")) {
//                return (size.replace("K|KB".toRegex(), "").toFloat() * 1024).toLong()
//            } else if (size.contains("M") || size.contains("MB")) {
//                return (size.replace("M|MB".toRegex(), "").toFloat() * 1024 * 1024).toLong()
//            } else if (size.contains("G") || size.contains("GB")) {
//                return (size.replace("G|GB".toRegex(), "").toFloat() * 1024 * 1024 * 1024).toLong()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return 0
//    }

    fun calculateMBSize(size: String): Long {
        try {
            if (TextUtils.isEmpty(size)) {
                return 0
            }
            return (size.toFloat() * 1024 * 1024).toLong()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 磁盘空间是否足够
     * 剩余空间 - 下载需要的大小 >= 250M ：足够，否则不足够
     *
     * @param size 下载需要的大小
     * @return
     */
    fun isSpaceEnough(size: String): Boolean {
        val needSize: Long = calculateMBSize(size)
        return isSpaceEnough(needSize)
    }

    fun isSpaceEnough(size: Long): Boolean {
        if (SDCardUtils.isSDCardEnableByEnvironment()) {
            appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?.let { externalFilesDir ->
                    val pathStr = externalFilesDir.absolutePath
                    val stat = StatFs(pathStr)
                    val blockSize = stat.blockSizeLong
                    val totalBlocks = stat.availableBlocksLong
                    val availableSize = blockSize * totalBlocks
                    return availableSize - size >= MIN_NEED_SPACE
                }
        }
        return false
    }

    /**
     * （不区分大小写、关键字多次出现多次变色)：
     */
    fun stringInterceptionChangeColor(text: String?, keyword: String?): SpannableString {
        if (text.isNullOrEmpty()) return SpannableString("")
        val string = text.lowercase(Locale.ROOT)
        val key = keyword?.lowercase(Locale.ROOT) ?: ""
        val pattern = Pattern.compile(key)
        val matcher = pattern.matcher(string)
        val ss = SpannableString(text)
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            ss.setSpan(
                ForegroundColorSpan(Color.parseColor("#f88650")), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return ss
    }
}