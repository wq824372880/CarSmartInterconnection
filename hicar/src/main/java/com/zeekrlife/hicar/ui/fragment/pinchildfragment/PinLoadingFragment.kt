package com.zeekrlife.hicar.ui.fragment.pinchildfragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.connect.base.manager.ConnectServiceManager
import com.zeekrlife.hicar.R
import com.zeekrlife.hicar.app.base.BaseFragment
import com.zeekrlife.hicar.data.PageType
import com.zeekrlife.hicar.databinding.PinLoadingLayoutBinding
import com.zeekrlife.hicar.utils.StringResourceProvider
import com.zeekrlife.net.interception.logging.util.logE

/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description: 相关加载页 对标PageType：BLUETOOTHl_TURN_ON CONNECTION_CODE_GET HICAR_CONNECTING
 *@author: e-Yang.Dong1
 *@date: 2023/5/15 09:24:49
 *@version: V1.0
 */
class PinLoadingFragment : BaseFragment<BaseViewModel,PinLoadingLayoutBinding>(){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        "PinLoadingFragment onCreate".logE("PinLoadingFragment")
        mBind.loadingNoticeTV.text = StringResourceProvider.resourceProvider.getString(R.string.wireless_connect_code_notice)
        mBind.loadingContentTV.text = StringResourceProvider.resourceProvider.getString(R.string.code_getting)

    }

    override fun initView(savedInstanceState: Bundle?) {
    }
}