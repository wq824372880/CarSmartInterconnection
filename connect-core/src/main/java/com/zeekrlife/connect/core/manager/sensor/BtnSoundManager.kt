package com.zeekrlife.connect.core.manager.sensor//package com.zeekr.dlnavideo.btn


import android.util.Log
import com.zeekr.basic.appContext
import com.zeekr.sdk.mediacenter.bean.ClientType
import com.zeekr.sdk.mediacenter.callback.DriverRestrictionsCallback
import com.zeekr.sdk.mediacenter.impl.MediaCenterAPI
import com.zeekr.sdk.mediacenter.impl.ZeekrMediaCenterProxy
import com.zeekrlife.net.interception.logging.util.logE

class BtnSoundManager private constructor() {


    companion object {
        const val TAG = "TrafficRestrictionsManager"
        val sInstance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BtnSoundManager()
        }
    }

    private var token: Any? = null

    var customMusicClient: MusicClientImpl? = null



//    /**
//     * 申请媒体中心焦点
//     */
//    fun requestPlay() {
//        if (BuildConfig.PRODUCT_NAME == DLNAVideoApplication.BX1E) {
//            LogUtils.d(TAG, "bx 不申请媒体中心焦点")
//            return
//        }
//        if (MediaCenterAPI.get() == null || token == null) {
//            LogUtils.d(TAG, "requestPlay  fail")
//            return
//        }
//        try {
//            MediaCenterAPI.get().requestPlay(token)
//        } catch (e: Exception) {
//            LogUtils.d(TAG, "requestPlay  exception")
//        }
//        LogUtils.d(TAG, "requestPlay  success")
//    }
//
//    /**
//     * 更新DIM播放信息
//     */
//    fun updateCurrentSourceType(playStatus: Int = PlaybackState.STATE_VIDEO_DLNA_STOP) {
//        if (BuildConfig.PRODUCT_NAME == DLNAVideoApplication.BX1E) {
//            LogUtils.d(TAG, "bx 不更新媒体状态")
//            return
//        }
//        if (MediaCenterAPI.get() == null || token == null) {
//            LogUtils.d(TAG, "updateCurrentSourceType fail")
//            return
//        }
//        MediaCenterAPI.get().updateCurrentSourceType(token, SourceType.SOURCE_TYPE_DLNA)
//        val mMusicPlaybackInfo: MusicPlaybackInfo = MyMusicPlaybackInfo(playStatus)
//        MediaCenterAPI.get().updateMusicPlaybackState(token, mMusicPlaybackInfo)
//        LogUtils.d(TAG, "updateCurrentSourceType success")
//    }

    /**
     * 行车限制，true可以播放
     *
     */
    val isCanPlayVideo: Boolean
        get() {
            if (MediaCenterAPI.get() == null) {
                Log.e(TAG, "MediaCenterAPI null() called")
                return false
            }
            return MediaCenterAPI.get().getDrivingRestrictions(ClientType.VIDEO)
        }

    /**
     * 行车限制监听
     */
    fun initVideoCanPlayListener(toPlayVideo: ((Boolean) -> Unit)) {
        Log.e(TAG,"---initVideoCanPlayListener-")
        if (MediaCenterAPI.get() == null) {
            Log.e(TAG, "initVideoCanPlayListener() called MediaCenterAPI.get()==null")
        }
        "isCanPlay===$isCanPlayVideo".logE(TAG)
        toPlayVideo.invoke(false)
        MediaCenterAPI.get().initDrivingRestrictions(ClientType.VIDEO, DriverRestrictionsCallback {
            Log.e(TAG, "initVideoCanPlayListener() called $it")
            toPlayVideo.invoke(it)
        })
    }


}