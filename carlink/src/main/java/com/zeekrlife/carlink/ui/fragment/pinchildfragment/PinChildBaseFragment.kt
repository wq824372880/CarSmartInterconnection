package com.zeekrlife.carlink.ui.fragment.pinchildfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.zeekrlife.carlink.ui.viewmodel.PinViewModel


/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description: pin相关页面的base ，此处Fragment做view使用，减少不必要的判断条件提升绘制速度
 *@author: e-Yang.Dong1
 *@date: 2023/5/12 16:36:20
 *@version: V1.0
 */
abstract class PinChildBaseFragment<VB: ViewDataBinding> : Fragment() {
    private var _binding: VB? = null
    val mBind: VB get() = _binding!!

    val pinViewModel: PinViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding(inflater, container)
        mBind.lifecycleOwner = viewLifecycleOwner
        return  mBind.root
    }

    protected abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?):VB

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}