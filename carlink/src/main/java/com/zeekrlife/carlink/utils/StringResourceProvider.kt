package com.zeekrlife.carlink.utils

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes

/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description:提供字符串资源读取，此处的context为application的context（方便在ViewModel中使用资源字符串）
 *@author: e-Yang.Dong1
 *@date: 2023/5/15 13:44:47
 *@version: V1.0
 */
class StringResourceProvider private constructor(application: Application) {

    private val context: Context = application.applicationContext

    fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    fun getString(@StringRes resId: Int, formatString:String): String {
        return context.getString(resId, formatString)
    }

    companion object {
        private lateinit var instance: StringResourceProvider

        @Synchronized
        fun init(application: Application) {
            if (!Companion::instance.isInitialized) {
                instance = StringResourceProvider(application)
            }
        }

        val resourceProvider: StringResourceProvider by lazy {
            if (!Companion::instance.isInitialized) {
                throw IllegalStateException("StringResourceProvider must be initialized before use.")
            }
            instance
        }
    }
}