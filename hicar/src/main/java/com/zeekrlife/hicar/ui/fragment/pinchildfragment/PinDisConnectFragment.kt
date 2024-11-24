package com.zeekrlife.hicar.ui.fragment.pinchildfragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.zeekrlife.connect.base.manager.ConnectServiceManager
import com.zeekrlife.hicar.app.base.BaseFragment
import com.zeekrlife.hicar.data.PageType
import com.zeekrlife.hicar.databinding.PinDisconnectLayoutBinding
import com.zeekrlife.hicar.ui.activity.MainActivity
import com.zeekrlife.hicar.ui.fragment.PinFragment
import com.zeekrlife.hicar.ui.viewmodel.PinViewModel
import com.zeekrlife.hicar.utils.HiCarServiceManager
import com.zeekrlife.net.interception.logging.util.logE

/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description:
 *@author: e-Yang.Dong1
 *@date: 2023/5/30 09:35:14
 *@version: V1.0
 */
class PinDisConnectFragment : BaseFragment<PinViewModel, PinDisconnectLayoutBinding>() {

    val pinViewModel: PinViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        "PinDisConnectFragment onCreate".logE("PinDisConnectFragment")
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBind.disconnectDeviceNameTV.text = HiCarServiceManager.getPhoneName()

        mBind.pinCloseButton.setOnClickListener {
            HiCarServiceManager.finishActivity(true)
            (activity as MainActivity).moveBackTask()
        }

        mBind.disconnectDeviceButton.setOnClickListener {
            if(!ConnectServiceManager.getInstance().ensureServiceAvailable()){
                pinViewModel.updatePageType(PageType.CONNECTION_CODE_GET_FAILED)
            }
            HiCarServiceManager.disconnectDevice(PinFragment.mDeviceId)
        }
    }

    override fun onDestroy() {
        "PinDisConnectFragment onDestroy".logE("PinDisConnectFragment")
        super.onDestroy()
    }

    private fun quit(){
        (activity as MainActivity).quit()
    }


}