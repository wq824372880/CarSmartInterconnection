package com.zeekrlife.connect.core.media

import com.zeekr.sdk.mediacenter.MusicPlaybackInfo


/**
 * create by Yueming.Zhao
 * 2023.09.07
 *
 * 对媒体中心卡片同步信息对象
 * todo 需要和媒体中心对一下字段
 */
class HicarMusicPlaybackInfo(private val hicarMediaData: HicarMediaData): MusicPlaybackInfo() {

    override fun getTitle(): String {
        return hicarMediaData.Name
    }

    override fun getArtist(): String {
        return hicarMediaData.Artist
    }

    override fun getDuration(): Long {
        return hicarMediaData.TotalTime
    }


    override fun getAlbum(): String {
        return hicarMediaData.AlbumName
    }

    override fun getPlaybackStatus(): Int {
        return hicarMediaData.Status
    }


    override fun getAppName(): String {
        return hicarMediaData.AppName
    }

    override fun getPackageName(): String {
        return hicarMediaData.AppPackageName
    }

}