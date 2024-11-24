package com.zeekrlife.connect

import android.os.Bundle
import com.zeekrlife.common.base.BaseVBActivity
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.connect.databinding.ActivityMainConnectBinding

class MainConnectActivity: BaseVBActivity<BaseViewModel, ActivityMainConnectBinding>(){
    override fun initView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main_connect)

    }
}