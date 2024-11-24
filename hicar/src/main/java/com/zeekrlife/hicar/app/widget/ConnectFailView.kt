package com.zeekrlife.hicar.app.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.zeekrlife.hicar.R
import com.zeekrlife.hicar.databinding.ConnectCodeViewBinding
import com.zeekrlife.hicar.databinding.ConnectFailViewBinding


/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description: 连接码及获取失败错误提示
 *@author: e-Yang.Dong1
 *@date: 2023/5/11 15:45:15
 *@version: V1.0
 */


class ConnectFailView constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var pageType = PAGE_TYPE_CODE
    private var binding: ConnectFailViewBinding

    init {
        initView()
        val inflater = LayoutInflater.from(context)
        binding = ConnectFailViewBinding.inflate(inflater, this,)
        refreshPage()
    }

    private fun initView() {
        this.background = AppCompatResources.getDrawable(context, R.drawable.connect_code_layout_bg)
    }


    /**
     * 显示错误页面，设备
     */
    fun showErrorPage(deviceName: String) {
        pageType = PAGE_TYPE_ERROR_DEVICE
        refreshPage()
        binding.failedTV.text = context.getString(R.string.app_connect_fail_device_notice, deviceName)
    }

    /**
     * 显示错误页面
     */
    fun showErrorPage() {
        pageType = PAGE_TYPE_ERROR
        refreshPage()
        binding.failedTV.text = context.getString(R.string.app_connect_fail_notice)
    }

    /**
     * 显示连接码页面
     */
    fun showCodePage() {
        pageType = PAGE_TYPE_CODE
        refreshPage()
    }

    private fun refreshPage() {
            binding.connectFailedGroup.visibility = View.VISIBLE

//            binding.errorLine1.text = if(pageType == PAGE_TYPE_ERROR) context.getString(R.string.connect_retry_notice) else context.getString(R.string.app_connect_fail_notice)
//            binding.errorLine2.text = if(pageType == PAGE_TYPE_ERROR) context.getString(R.string.connect_restart_notice) else context.getString(R.string.app_connect_fail_notice)
    }
}