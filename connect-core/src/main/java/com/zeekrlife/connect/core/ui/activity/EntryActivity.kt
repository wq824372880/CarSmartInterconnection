package com.zeekrlife.connect.core.ui.activity

import android.content.Intent
import android.os.Bundle
import com.zeekrlife.common.base.BaseVBActivity
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.connect.core.ConnectService
import com.zeekrlife.connect.core.databinding.ActivityLauncherBinding

class EntryActivity:BaseVBActivity<BaseViewModel, ActivityLauncherBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
//        val serviceIntent = Intent(this, ConnectService::class.java)
//        startService(serviceIntent)
    }
}