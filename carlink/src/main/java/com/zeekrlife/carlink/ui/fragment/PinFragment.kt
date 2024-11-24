package com.zeekrlife.carlink.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.zeekrlife.carlink.R
import com.zeekrlife.carlink.app.App
import com.zeekrlife.carlink.app.base.BaseFragment
import com.zeekrlife.carlink.ui.fragment.pinchildfragment.PinLoadingFragment
import com.zeekrlife.carlink.data.PageType
import com.zeekrlife.carlink.databinding.PinLayoutBinding
import com.zeekrlife.carlink.ui.fragment.pinchildfragment.PinCodeFragment
import com.zeekrlife.carlink.ui.fragment.pinchildfragment.PinSurfaceFragment
import com.zeekrlife.carlink.ui.viewmodel.PinViewModel

/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description: 各个复用Fragment的统一管理入口，提供页面间通用PinViewModel
 *@author: e-Yang.Dong1
 *@date: 2023/5/10 13:03:13
 *@version: V1.0
 */
class PinFragment : BaseFragment<PinViewModel, PinLayoutBinding>() {
    private lateinit var mPinCodeFragment: PinCodeFragment
    private lateinit var mPinLoadingFragment: PinLoadingFragment
    private lateinit var mPinSurfaceFragment: PinSurfaceFragment

    override fun initView(savedInstanceState: Bundle?) {
        replaceFragment()

        mBind.show1.setOnClickListener {
            mViewModel.updatePageType(PageType.BLUETOOTHl_TURN_ON)
        }
        mBind.show2.setOnClickListener {
            mViewModel.updatePageType(PageType.CONNECTION_CODE_GET)
        }
        mBind.show3.setOnClickListener {
            mViewModel.updatePageType(PageType.HICAR_CONNECTING)
        }

        mBind.show4.setOnClickListener {
            mViewModel.updatePageType(PageType.HICAR_CONNECT)

        }
        mBind.show5.setOnClickListener {
            mViewModel.updatePageType(PageType.CONNECTION_CODE_GET_FAILED)
        }
        mBind.show6.setOnClickListener {
            mViewModel.updatePageType(PageType.HICAR_CONNECTION_FAILED)
        }
        mBind.show7.setOnClickListener {
            mViewModel.updatePageType(PageType.HICAR_DISCONNECT)
        }
        App.eventViewModelInstance.orderReChargedEvent.observe(this){
            //利用全局广播获取状态变化
            it?.let {
                //String转enum
                val pageType = PageType.values().find { str-> str.name == it.orderNo }
                if (pageType != null) {
                    mViewModel.updatePageType(pageType)
                    if(pageType == PageType.HICAR_CONNECT){ //更新连接码
                        it.description?.let { pinCode ->
                            mViewModel.updatePinCode(pinCode)
                        }

                    }
                } else {

                }

            }

        }

    }

    private fun replaceFragment(){
        //替换内部 Fragment
        var fragment: Fragment? = null
        mViewModel.pageType.observe(this){
            if(mViewModel.isPinLoadingPage(it)){
                fragment = getPinLoadingFragment()
            }else if(mViewModel.isPinCodePage(it)){
                fragment = getPinCodeFragment()
            }else if(mViewModel.isSurfacePage(it)){
                fragment = getPinSurfaceFragment()
            }
            fragment?.let{ fragment1 ->
                replaceFragment(fragment1)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment){
        childFragmentManager.beginTransaction()
            .replace(R.id.innerFragmentContainer, fragment)
            .commit()
    }

    private fun getPinLoadingFragment() : PinLoadingFragment {
        return if(::mPinLoadingFragment.isInitialized){
            mPinLoadingFragment
        }else{
            PinLoadingFragment()
        }
    }

    private fun getPinCodeFragment() : PinCodeFragment{
        return if(::mPinCodeFragment.isInitialized){
            mPinCodeFragment
        }else{
            PinCodeFragment()
        }
    }

    private fun getPinSurfaceFragment() : PinSurfaceFragment{
        return if(::mPinSurfaceFragment.isInitialized){
            mPinSurfaceFragment
        }else{
            PinSurfaceFragment()
        }
    }
}