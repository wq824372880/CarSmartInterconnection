package com.zeekrlife.carlink.app.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.zeekrlife.carlink.R
import com.zeekrlife.carlink.databinding.ConnectCodeViewBinding


/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description: 连接码及获取失败错误提示
 *@author: e-Yang.Dong1
 *@date: 2023/5/11 15:45:15
 *@version: V1.0
 */

const val PAGE_TYPE_CODE = 101
const val PAGE_TYPE_ERROR = 102
const val PAGE_TYPE_ERROR_DEVICE = 103 //连接某设备失败

class ConnectCodeView constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var pageType = PAGE_TYPE_CODE
    private lateinit var binding: ConnectCodeViewBinding

    init {
        initView()
        val inflater = LayoutInflater.from(context)
        binding = ConnectCodeViewBinding.inflate(inflater, this,)
        refreshPage()
    }

    private fun initView() {
        this.background = AppCompatResources.getDrawable(context, R.drawable.connect_code_layout_bg)
    }


    /**
     * 显示连接码
     */
    fun showCodeNumber(str: String) {
        val codeList = str.toCharArray()
        codeList.let {
            if (codeList.size < 6) { //数据异常,页面做相应处理
                showErrorPage()
                return
            }
            binding.code1.text = codeList[0].toString()
            binding.code2.text = codeList[1].toString()
            binding.code3.text = codeList[2].toString()
            binding.code4.text = codeList[3].toString()
            binding.code5.text = codeList[4].toString()
            binding.code6.text = codeList[5].toString()
            showCodePage()

        }
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
        if (pageType == PAGE_TYPE_CODE) {
            binding.codeGroup.visibility = View.VISIBLE
            binding.connectFailedGroup.visibility = View.INVISIBLE
        } else if (pageType == PAGE_TYPE_ERROR || pageType == PAGE_TYPE_ERROR_DEVICE) {
            binding.codeGroup.visibility = View.INVISIBLE
            binding.connectFailedGroup.visibility = View.VISIBLE
        }
    }
}