package com.zeekrlife.carlink.ui.fragment.pinchildfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zeekrlife.carlink.databinding.PinCodeLayoutBinding
import com.zeekrlife.carlink.data.PageType

/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description: 连接页 对标PageType：HICAR_CONNECT CONNECTION_CODE_GET_FAILED HICAR_CONNECTION_FAILED HICAR_DISCONNECT
 *@author: e-Yang.Dong1
 *@date: 2023/5/12 16:34:11
 *@version: V1.0
 */
class PinCodeFragment  : PinChildBaseFragment<PinCodeLayoutBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): PinCodeLayoutBinding {
        val mBind = PinCodeLayoutBinding.inflate(inflater, container, false)
        mBind.vm = pinViewModel
        return mBind
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pinViewModel.pageType.observe(this) {
            when (it){
//                PageType.HICAR_CONNECT -> showConnectPage()
                PageType.CONNECTION_CODE_GET_FAILED -> {
                    showPinGetFailedPage()
                    refreshErrorOrDisconnectNotice()
                }
                PageType.HICAR_CONNECTION_FAILED -> showConnectFailedPage()
                PageType.HICAR_DISCONNECT -> {
                    showConnectFailedPage()
                    refreshErrorOrDisconnectNotice()
                }
                else -> {}
            }
        }
    }

    private fun refreshErrorOrDisconnectNotice(){
        mBind.pinErrorNoticeLayout.connectPinErrorLine1.text = pinViewModel.getErrorOrDisConnectContentString()
        mBind.pinErrorNoticeLayout.connectPinErrorLine2.text = pinViewModel.getErrorOrDisConnectNoticeString()
    }

    /**
     * 显示连接页面
     */
    private fun showConnectPage(str: String){
        mBind.connectCodeView.showCodeNumber(str)
    }

    /**
     * 显示pin码获取失败页面
     */
    private fun showPinGetFailedPage(){
        mBind.connectCodeView.showErrorPage()
    }
    /**
     * 显示连接失败页面
     */
    private fun showConnectFailedPage(){
        mBind.connectCodeView.showErrorPage("董阳的华为")
    }
}