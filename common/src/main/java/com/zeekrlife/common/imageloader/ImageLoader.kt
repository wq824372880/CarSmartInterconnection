package com.zeekrlife.common.imageloader

import android.graphics.Color
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

object ImageLoader {

    fun ImageView.load(url: String) {
        Glide.with(this).load(url).transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }

    fun ImageView.load(url: String, placeHolder: Int, error: Int) {
        val options = RequestOptions().placeholder(placeHolder).error(error)
        Glide.with(this).load(url).apply(options).into(this)
    }

    /**
     * @param url
     * @param placeHolder
     * @param error
     * @param corner
     */
    fun ImageView.loadWithCorner(url: String, placeHolder: Int, error: Int, corner: Int) {
        val options = RequestOptions().placeholder(placeHolder)
            .error(error)
            .optionalTransform(CenterCrop())
            .optionalTransform(RoundedCorners(corner))
        Glide.with(this).load(url).apply(options).into(this)
    }

    /**
     * 圆形图片带边框
     *
     * @param url
     * @param placeHolder
     * @param error
     * @param corner
     */
    fun ImageView.loadCornerWithSide(
        url: String,
        placeHolder: Int,
        error: Int,
        corner: Int,
        side: Int = 6,
        color: Int = Color.LTGRAY
    ) {
        val options = RequestOptions()
            .placeholder(placeHolder)
            .error(error)
            .optionalTransform(CenterCrop())
            .optionalTransform(RoundSideHeaderTransformation(corner, side, color))
        Glide.with(this)
            .load(url)
            .apply(options)
            .into(this)
    }

    fun ImageView.load(url: String, options: RequestOptions) {
        Glide.with(this).load(url).apply(options).into(this)
    }
}