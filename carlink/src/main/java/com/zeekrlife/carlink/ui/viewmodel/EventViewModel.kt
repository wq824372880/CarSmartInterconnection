package com.zeekrlife.carlink.ui.viewmodel

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.carlink.data.response.OrderReCharged

class EventViewModel : BaseViewModel() {

    val orderReChargedEvent = UnPeekLiveData<OrderReCharged>()

    //首页切换tab
    val switchTabEvent = UnPeekLiveData<Int>()

}