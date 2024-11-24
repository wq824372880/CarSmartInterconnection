package com.zeekrlife.hicar.ui.fragment.pinchildfragment

import android.content.Context
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.drake.interval.Interval
import com.zeekrlife.common.ext.gone
import com.zeekrlife.common.util.SizeUtils
import com.zeekrlife.hicar.app.base.BaseFragment
import com.zeekrlife.hicar.databinding.PinCodeLayoutBinding
import com.zeekrlife.hicar.data.PageType
import com.zeekrlife.hicar.service.HiCarCoreServiceListener
import com.zeekrlife.hicar.ui.activity.MainActivity
import com.zeekrlife.hicar.ui.viewmodel.PinViewModel
import com.zeekrlife.hicar.utils.HiCarServiceManager
import com.zeekrlife.net.interception.logging.util.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description: 连接页 对标PageType：HICAR_CONNECT CONNECTION_CODE_GET_FAILED HICAR_CONNECTION_FAILED HICAR_DISCONNECT
 *@author: e-Yang.Dong1
 *@date: 2023/5/12 16:34:11
 *@version: V1.0
 */
class PinCodeFragment  : BaseFragment<PinViewModel,PinCodeLayoutBinding>() {

    val pinViewModel: PinViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )
    var advSend = false
    var interval: Interval? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        "PinCodeFragment onCreate".logE("PinCodeFragment")
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBind.pinCloseButton.setOnClickListener {
            quit()
        }
        interval = Interval(-1,2L, TimeUnit.MINUTES,initialDelay = 2L)
            .life(this@PinCodeFragment)
            .subscribe {
                "::interval".logE("PinCodeFragment")
                HiCarServiceManager.handleStartAdv(activity as MainActivity)
            }.onlyResumed(viewLifecycleOwner)
            .start()
    }

    override fun initObserver() {
        pinViewModel.pinCode.observe(this@PinCodeFragment){
            it?.let {
                mBind.codeLoading.gone()
                showConnectPage(pinViewModel.pinCode.value)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val getCurrentEventType = HiCarServiceManager.getCurrentEventType()
        Log.e("PinCodeFragment onResume", "getCurrentEventType :${HiCarServiceManager.getCurrentEventType()},pinViewModel.pinCode.value:${pinViewModel.pinCode.value}")
        if(getCurrentEventType == HiCarCoreServiceListener.EVENT_DEVICE_DISCONNECT || getCurrentEventType == HiCarCoreServiceListener.EVENT_DEVICE_CONNECT_FAILD){
            HiCarServiceManager.handleStartAdv(activity as MainActivity)
        }else if(getCurrentEventType == HiCarCoreServiceListener.EVENT_DEVICE_CONNECT){
            pinViewModel.updatePageType(PageType.HICAR_DISCONNECT)
        }else if(advSend){
            HiCarServiceManager.handleStartAdv(activity as MainActivity)
            advSend = false
        }else{
            if(pinViewModel.pinCode.value.isEmpty()){
                HiCarServiceManager.handleStartAdv(activity as MainActivity)
            }else{
                mBind.codeLoading.gone()
                showConnectPage(pinViewModel.pinCode.value)
                return
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            delay(800)
            mBind.codeLoading.gone()
            showConnectPage(pinViewModel.pinCode.value)
        }
    }

    override fun onPause() {
        super.onPause()
        if(HiCarServiceManager.getCurrentEventType() == HiCarCoreServiceListener.EVENT_DEVICE_DISCONNECT  || HiCarServiceManager.getCurrentEventType() == HiCarCoreServiceListener.EVENT_DEVICE_CODE_SUCCESS){
            advSend = true
        }
    }

    /**
     *  更新辅助线位置
     */
    private fun showConnectPage(type: PageType){
        if(type == PageType.HICAR_CONNECT){
            mBind.guidelineLeft.setGuidelineBegin(SizeUtils.dp2px(240F))
            mBind.guidelineRight.setGuidelineEnd(SizeUtils.dp2px(900F))
        }else{
            mBind.guidelineLeft.setGuidelineBegin(SizeUtils.dp2px(450F))
            mBind.guidelineRight.setGuidelineEnd(SizeUtils.dp2px(450F))
        }
    }
    /**
     * 显示连接页面
     */
    private fun showConnectPage(str: String){
        mBind.connectCodeView.showCodeNumber(str)
    }


    override fun onDestroy() {
        "PinCodeFragment onDestroy".logE("PinCodeFragment")
        super.onDestroy()
    }

    private fun quit(){
        (activity as MainActivity).quit()
    }
}