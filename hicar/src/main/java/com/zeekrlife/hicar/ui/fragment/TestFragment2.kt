package com.zeekrlife.hicar.ui.fragment

import android.os.Bundle
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.hicar.R
import com.zeekrlife.net.interception.logging.util.logD
import com.zeekrlife.common.ext.setOnclickNoRepeat
import com.zeekrlife.hicar.app.base.BaseFragment
import com.zeekrlife.hicar.databinding.FragmentTest2Binding

class TestFragment2 : BaseFragment<BaseViewModel, FragmentTest2Binding>() {

    private var isLoaded = false

    override fun initView(savedInstanceState: Bundle?) {
    }


    override fun onResume() {
        super.onResume()
//           mBind.customToolbar.setCenterTitle(R.string.bottom_title_my_app)
//           mBind.customToolbar.setBackgroundResource(R.color.colorRed)
//            titleBar(mBind.customToolbar)
//            statusBarDarkFont(true)
    }

    /**
     * 懒加载 第一次获取视图的时候 触发
     */
    override fun onHiddenChanged(hidden: Boolean) {
        if(!hidden && !isLoaded){
            isLoaded = true
            "我是test2 isLoaded$isLoaded".logD()
        }
    }
    override fun onBindViewClick() {
        setOnclickNoRepeat(mBind.cvFindCountdown){
            when(it.id){
             R.id.cv_find_countdown ->{
                 mBind.cvFindCountdown.start()
                }
            }
        }
    }

}