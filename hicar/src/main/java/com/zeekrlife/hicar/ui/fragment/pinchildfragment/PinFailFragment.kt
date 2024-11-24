package com.zeekrlife.hicar.ui.fragment.pinchildfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.zeekrlife.hicar.app.base.BaseFragment
import com.zeekrlife.hicar.databinding.PinFailLayoutBinding
import com.zeekrlife.hicar.ui.activity.MainActivity
import com.zeekrlife.hicar.ui.viewmodel.PinViewModel
import com.zeekrlife.net.interception.logging.util.logE

/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description: 连接页 对标PageType：HICAR_CONNECT CONNECTION_CODE_GET_FAILED HICAR_CONNECTION_FAILED HICAR_DISCONNECT
 *@author: e-Yang.Dong1
 *@date: 2023/5/12 16:34:11
 *@version: V1.0
 */
class PinFailFragment  : BaseFragment<PinViewModel,PinFailLayoutBinding>() {

    val pinViewModel: PinViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        "PinCodeFragment onCreate".logE("PinCodeFragment")
    }

    override fun initView(savedInstanceState: Bundle?) {

        mBind.pinCloseButton.setOnClickListener {
            quit()
        }
        mBind.pinRetryButton.setOnClickListener {
            pinViewModel.retryClick(activity as MainActivity)
        }
    }

    override fun onDestroy() {
        "PinCodeFragment onDestroy".logE("PinCodeFragment")
        super.onDestroy()
    }

    private fun quit(){
        (activity as MainActivity).quit()
    }
}