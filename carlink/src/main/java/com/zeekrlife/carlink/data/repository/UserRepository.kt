package com.zeekrlife.carlink.data.repository

import com.zeekrlife.carlink.data.cache.CacheExt
import com.zeekrlife.carlink.data.response.HomeItemCategoryBean
import com.zeekrlife.carlink.data.response.UserInfo
import com.zeekrlife.net.api.ApiPagerResponse
import com.zeekrlife.net.api.NetUrl
import rxhttp.wrapper.coroutines.Await
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.param.toResponse

/**
 * 描述　: 数据仓库
 */
object UserRepository {

    /**
     * 车辆信息
     */
    private val deviceInfo by lazy { CacheExt.getOpenApi()?.deviceInfo }

    /**
     * 车辆款式
     * DeviceApi.getInstance().vehicleModel
     */
    private val vehicleModel = /*deviceInfo?.getVehicleModel ?: */""

    /**
     * 车辆系列，必填,如：DC1E-012
     * DeviceApi.getInstance().vehicleType
     */
    private val vehicleType = deviceInfo?.getVehicleType.run {
        if (isNullOrEmpty()) "BX1E" else this
    }


    /**
     * 登录
     */
    fun login(userName: String, password: String): Await<UserInfo> {
        return RxHttp.postForm(NetUrl.LOGIN)
            .add("username", userName)
            .add("password", password)
            .toResponse()
    }

    /**
     * 获取列表信息
     */
    fun getList(pageIndex: Int): Await<ApiPagerResponse<Any>> {
        return RxHttp.get(NetUrl.HOME_LIST, pageIndex)
            .toResponse()
    }

    /**
     * 获取分类列表信息
     */
    fun getHomeList(): Await<MutableList<HomeItemCategoryBean>> {
        return RxHttp.get(NetUrl.HOME_CATEGORT_LIST)
            .toResponse()
    }


}

