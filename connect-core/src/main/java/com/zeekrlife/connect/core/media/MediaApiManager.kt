package com.zeekrlife.connect.core.media

import android.text.TextUtils
import android.util.Log
import com.google.gson.JsonObject
import com.zeekr.basic.appContext
import com.zeekr.sdk.mediacenter.SourceType
import com.zeekr.sdk.mediacenter.impl.MediaCenterAPI
import com.zeekrlife.common.util.GsonUtils
import com.zeekrlife.net.interception.logging.util.logE

/**
 * create by Yueming.Zhao
 * 2023.09.07
 */
object  MediaApiManager {

    const val TAG = "CommonApiManager:"
    private var mToken :Any ? = null // 媒体token mediaCenter的所有指令都需要加上

    init {
        MediaCenterAPI.get().init(appContext) { isSuccess, message ->
            "MediaCenterAPI init : $isSuccess  :  $message".logE(TAG)
            getToken()
        }
    }

    private fun getToken(){
        mToken = MediaCenterAPI.get().registerMusic(appContext.packageName, HicarMusicClient())
    }


    fun updateSourceType(type: Int) {
        //SOURCE_TYPE_ONLINE  todo 待确认
        checkInit()
        MediaCenterAPI.get().updateCurrentSourceType(mToken,SourceType.SOURCE_TYPE_ONLINE);
    }

    private fun exposeMediaInfo(playbackInfo: HicarMusicPlaybackInfo) {
        checkInit()
        MediaCenterAPI.get().updateMusicPlaybackState(mToken, playbackInfo)
    }

    fun updateProgress(progress: Long) {
        checkInit()
        MediaCenterAPI.get().updateCurrentProgress(mToken,progress)
    }

    private fun checkInit(){
        if (mToken == null) {
            getToken()
        }
    }

    fun uploadMediaData(data: ByteArray) {
        val toJson = GsonUtils.toJson(String(data))
        val subString = if (toJson.startsWith("\"") && toJson.endsWith("\"")) {
            toJson.substring(1, toJson.length - 1)
        } else {
            toJson
        }

        Log.e(TAG,"----toJson: $toJson")

        val medialBean = GsonUtils.fromJson(subString.replace("\\",""), MediaData::class.java)
        Log.e(TAG,"----fromJson: $medialBean")

        medialBean?.MediaData?.Artist?.let {
            Log.e(TAG,"----send media center: $it")
            exposeMediaInfo(HicarMusicPlaybackInfo(medialBean.MediaData))
        }

    }


}