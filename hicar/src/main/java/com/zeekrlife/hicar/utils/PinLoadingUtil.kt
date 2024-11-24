package com.zeekrlife.hicar.utils

import android.widget.ImageView



/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description: 连接页动画加载工具
 *@author: e-Yang.Dong1
 *@date: 2023/5/15 09:48:48
 *@version: V1.0
 */
class PinLoadingUtil(private val animationImg: ImageView) {

    private val framesAnimation by lazy{
        FrameAnimationManager.getInstance().createFramesAnimation()
    }
    fun startAnimation(frames: IntArray) {
        startAnimation(frames, 10)
    }
    fun startAnimation(frames: IntArray, delay: Int, cycleTotalTime:Int) {
        framesAnimation.setCycleTotalTime(cycleTotalTime)
        startAnimation(frames, delay)
    }
    //播放动画
    fun startAnimation(frames: IntArray, delay: Int) {
        if (framesAnimation.isRunning) {
            framesAnimation.stop()
        }
        framesAnimation.setFrameData(animationImg, frames, delay, true)
        framesAnimation.start()
    }

    fun stopAnimation(){
        framesAnimation.stop()
    }
}