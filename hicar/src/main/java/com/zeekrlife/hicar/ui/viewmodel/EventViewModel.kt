package com.zeekrlife.hicar.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.livedata.BooleanLiveData
import com.zeekrlife.common.livedata.StringLiveData
import com.zeekrlife.connect.base.manager.ConnectServiceManager
import com.zeekrlife.hicar.R
import com.zeekrlife.hicar.data.PageType
import com.zeekrlife.hicar.data.response.OrderReCharged
import com.zeekrlife.hicar.service.HiCarCoreServiceListener
import com.zeekrlife.hicar.utils.HiCarServiceManager
import com.zeekrlife.hicar.utils.StringResourceProvider
import com.zeekrlife.net.interception.logging.util.LogUtils
import com.zeekrlife.net.interception.logging.util.logE
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EventViewModel : BaseViewModel() {

    val orderReChargedEvent = UnPeekLiveData<OrderReCharged>()
    val showLoadingBackground = UnPeekLiveData<Boolean>().apply { value = false }
}