package com.zeekrlife.carlink.ui.fragment.pinchildfragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.zeekrlife.carlink.R

import com.zeekrlife.carlink.utils.PinLoadingUtil
import com.zeekrlife.carlink.data.PageType
import com.zeekrlife.carlink.databinding.PinLoadingLayoutBinding

/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description: 相关加载页 对标PageType：BLUETOOTHl_TURN_ON CONNECTION_CODE_GET HICAR_CONNECTING
 *@author: e-Yang.Dong1
 *@date: 2023/5/15 09:24:49
 *@version: V1.0
 */
class PinLoadingFragment : PinChildBaseFragment<PinLoadingLayoutBinding>(){
    private val codeGettingLoadingUtil by lazy {
         PinLoadingUtil(mBind.loadingIV)
    }
    private val connectingLoadingUtil by lazy {
        PinLoadingUtil(mBind.loadingIV)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): PinLoadingLayoutBinding {
        val mBind = PinLoadingLayoutBinding.inflate(inflater, container, false)
        mBind.vm = pinViewModel
        return mBind
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        codeGettingLoadingUtil.let {
//            it.startAnimation(pinViewModel.getLoadingImageArray(PageType.CONNECTION_CODE_GET))
//        }



        pinViewModel.pageType.observe(viewLifecycleOwner) { newValue ->
            Log.i("yang", "pinViewModel.pageType changed: $newValue")
            if(pinViewModel.isPinLoadingPage(newValue)){
                mBind.loadingNoticeTV.text = pinViewModel.getLoadingNoticeString()
                mBind.loadingContentTV.text = pinViewModel.getLoadingContentString()
            }
            when (newValue){
                PageType.BLUETOOTHl_TURN_ON -> {
                    mBind.loadingIV.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.bluetooth, null))
                    codeGettingLoadingUtil.stopAnimation()
                }
                PageType.CONNECTION_CODE_GET -> {
                    mBind.loadingIV.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.loading_0, null))
                    codeGettingLoadingUtil.startAnimation(pinViewModel.getLoadingImageArray(PageType.CONNECTION_CODE_GET))
                }
                PageType.HICAR_CONNECTING -> {

                }
                else -> {

                }
            }
        }



    }
}