package com.zeekrlife.carlink.ui.fragment

import android.os.Bundle
import com.gyf.immersionbar.ktx.immersionBar
import com.zeekrlife.carlink.R
import com.zeekrlife.carlink.app.base.BaseFragment
import com.zeekrlife.carlink.databinding.FragmentTwoBinding
import com.zeekrlife.common.base.BaseViewModel

class TwoFragment : BaseFragment<BaseViewModel, FragmentTwoBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        mBind.customToolbar.setCenterTitle(R.string.bottom_title_paper)
        mBind.customToolbar.setBackgroundResource(R.color.colorRed)
    }

    override fun onResume() {
        super.onResume()
        immersionBar {
            titleBar(mBind.customToolbar)
        }
    }
}