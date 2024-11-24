package com.zeekrlife.hicar.utils

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import java.util.HashMap;


/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description: 帧动画播放管理
 *@author: e-Yang.Dong1
 *@date: 2023/5/15 09:33:53
 *@version: V1.0
 */
class FrameAnimationManager {

    private val TAG = "FrameAnimationUtils"

    @Volatile
    private var mInstance: FrameAnimationManager? = null
    private val hashMap: HashMap<Int, Int> = HashMap()

    /**
     * 获取单例
     */
    companion object {
        @Volatile
        private var instance: FrameAnimationManager? = null
        fun getInstance(): FrameAnimationManager {
            return instance ?: synchronized(this) {
                instance ?: FrameAnimationManager().also { instance = it }
            }
        }
    }

    fun createFramesAnimation(): FramesAnimation {
        return FramesAnimation()
    }

    /**
     * 循环读取帧---循环播放帧
     */
    class FramesAnimation internal constructor() {
        /**
         * 帧数组
         */
        private lateinit var mFrames: IntArray

        /**
         * 当前帧
         */
        @Volatile
        private var mIndex = 0
        /**
         *
         */
        /**
         * 开始/停止播放用
         */
        @get:Synchronized
        @Volatile
        var isRunning = false
            private set

        /**
         * 动画是否正在播放，防止重复播放
         */
        private var mIsRunning = false

        /**
         * 软引用ImageView，以便及时释放掉
         */
        private var mSoftReferenceImageView: ImageView? = null
        private val mHandler: Handler = Handler()
        private var mDelayMillis = 0
        private var fps = 0
        private var isRepeat = false
        var lastPictureId = -1 //播放的最后一帧的图片ID
            private set
        private var mBitmap: Bitmap? = null
        private var cycleTotalTime = 1000 // 一个循环总耗时

        /**
         * Bitmap管理类，可有效减少Bitmap的OOM问题
         */
        private var mBitmapOptions: BitmapFactory.Options? = null

        /**
         * 播放监听
         */
        private var animationListener: AnimationListener? = null

        /**
         * @param imageView image组件
         * @param fps       FPS为每秒播放帧数，FPS = 1/T，（T--每帧间隔时间秒）
         * @param isRepeat  是否重复播放
         * @return 帧动画
         */
        fun setFrameData(imageView: ImageView, frames: IntArray, fps: Int, isRepeat: Boolean) {
            mFrames = frames
            this.fps = fps
            mIndex = -1
            mSoftReferenceImageView = imageView
            isRunning = false
            mIsRunning = false
            //帧动画时间间隔，毫秒
            mDelayMillis = cycleTotalTime / fps
            this.isRepeat = isRepeat

            // 当图片大小类型相同时进行复用，避免频繁GC
            val bmp: Bitmap = (imageView.drawable as BitmapDrawable).bitmap
            //            Bitmap bmp = ((BitmapDrawable) imageView.getBackground()).getBitmap();
            val width: Int = bmp.width
            val height: Int = bmp.height
            val config: Bitmap.Config = bmp.config
            mBitmap = Bitmap.createBitmap(width, height, config)
            mBitmapOptions = BitmapFactory.Options()
            //设置Bitmap内存复用

            //Bitmap复用内存块，类似对象池，避免不必要的内存分配和回收
            mBitmapOptions?.let {
                it.inBitmap = mBitmap
                //解码时返回可变Bitmap
                it.inMutable = true
                //缩放比例
                it.inSampleSize = 1
            }

        }

        /**
         * 设置帧数
         *
         * @param fps 帧数
         */
        @Synchronized
        fun setFps(fps: Int) {
            this.fps = fps
            mDelayMillis = cycleTotalTime / fps
        }

        /**
         * 一个循环总耗时
         *
         * @param cycleTotalTime 循环总耗时
         */
        @Synchronized
        fun setCycleTotalTime(cycleTotalTime: Int) {
            this.cycleTotalTime = cycleTotalTime
            mDelayMillis = cycleTotalTime / fps
        }

        /**
         * 设置帧数
         *
         * @param fpsRat 速率
         */
        @Synchronized
        fun setFpsRat(fpsRat: Float) {
            mDelayMillis = (cycleTotalTime / (fps * fpsRat)).toInt()
        }

        /**
         * 循环读取下一帧
         *
         * @return 下一帧
         */
        private val next: Int
            private get() {
                mIndex++
                if (mIndex >= mFrames.size) {
                    mIndex = 0
                    if (!isRepeat) {
                        isRunning = false
                    }
                }
                return mFrames[mIndex]
            }

        /**
         * 播放动画，同步锁防止多线程读帧时，数据安全问题
         */
        @Synchronized
        fun start() {
            isRunning = true
            if (mIsRunning) {
                return
            }
            if (animationListener != null) {
                animationListener!!.onAnimationStarted()
            }
            val runnable: Runnable = object : Runnable {
                override fun run() {
                    val imageRes: Int = next
                    val imageView: ImageView? = mSoftReferenceImageView
                    if (!isRunning || imageView == null) {
                        mIsRunning = false
                        if (animationListener != null) {
                            animationListener!!.onAnimationStopped()
                        }
                        return
                    }
                    mIsRunning = true
                    //新开线程去读下一帧
                    mHandler.postDelayed(this, mDelayMillis.toLong())
                    setFrameImage(imageView, imageRes)
                }
            }
            mHandler.post(runnable)
        }

        /**
         * 停止播放
         */
        @Synchronized
        fun stop() {
            if (!isRunning) {
                return
            }
            isRunning = false
            mIndex = 0
        }

        private fun setFrameImage(imageView: ImageView, imageRes: Int) {
            lastPictureId = imageRes
            if (!imageView.isShown) {
                imageView.visibility = View.VISIBLE
            }
            if (mBitmap != null) {
                var bitmap: Bitmap? = null
                try {
                    bitmap = BitmapFactory.decodeResource(
                        imageView.resources, imageRes,
                        mBitmapOptions
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                } else {
                    imageView.setImageResource(imageRes)
                    mBitmap?.let {
                        it.recycle()
                        mBitmap = null
                    }

                }
            } else {
                imageView.setImageResource(imageRes)
            }
        }

        /**
         * 设置播放监听
         *
         * @param listener 动画停止的监听
         */
        fun setAnimationListener(listener: AnimationListener?): FramesAnimation {
            animationListener = listener
            return this
        }
    }


    /**
     * 从xml中读取帧数组
     *
     * @param resId 动画资源arrayIds
     */
    private fun getData(context: Context, resId: Int): IntArray? {
        val array: TypedArray = context.resources.obtainTypedArray(resId)
        val len: Int = array.length()
        val intArray = IntArray(len)
        for (i in 0 until len) {
            intArray[i] = array.getResourceId(i, 0)
        }
        array.recycle()
        return intArray
    }

    /**
     * 播放监听
     */
    interface AnimationListener {
        /**
         * 开始播放
         */
        fun onAnimationStarted()

        /**
         * 停止播放
         */
        fun onAnimationStopped()
    }


}