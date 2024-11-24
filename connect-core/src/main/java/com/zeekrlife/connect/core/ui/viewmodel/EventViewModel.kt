package com.zeekrlife.connect.core.ui.viewmodel

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.livedata.BooleanLiveData

class EventViewModel: BaseViewModel() {
    //手机连接蓝牙状态
    val BtConnectStateEvent = UnPeekLiveData<Int>()

    val background = UnPeekLiveData<Boolean>() //false：否，将HiCar切换到前台显示；true：是，将HiCar切换到后台显示。

    val moveTaskToBack = UnPeekLiveData<Boolean>()

    val finishActivityEvent = UnPeekLiveData<Boolean>()

    val startAppSuccess = UnPeekLiveData<Boolean>().apply { value = false }

    val castTypeEvent = UnPeekLiveData<Int>() //投屏冲突
}