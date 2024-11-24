package com.zeekrlife.carlink.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.ext.rxHttpRequest
import com.zeekrlife.carlink.data.cache.CacheExt
import com.zeekrlife.carlink.data.repository.UserRepository
import com.zeekrlife.carlink.data.response.HomeItemCategoryBean
import com.zeekrlife.carlink.data.response.ProtocolInfoBean

class HomeViewModel : BaseViewModel() {

    var mCategoryList = MutableLiveData<MutableList<HomeItemCategoryBean>>()
    var tabs = mutableListOf("我的应用", "精品推荐", "影音娱乐", "高效办公", "某某分类", "某某分类", "某某分类", "我的小程序", "UI展示分类")
    var remoteUserAgreementBean: ProtocolInfoBean? = null
    var remoteProtocolInfoBean: ProtocolInfoBean? = null

    fun getHomeList() {
        rxHttpRequest {
            onRequest = {
                mCategoryList.value = UserRepository.getHomeList().await()
            }

            onError = {
                mCategoryList.value = mutableListOf()
            }
        }
    }

    fun mock() {
        val mHomeItemCategoryBeanList: MutableList<HomeItemCategoryBean> = arrayListOf()
        tabs.forEach {
            val mHomeItemBean = HomeItemCategoryBean()
            mHomeItemBean.categoryName = it
            mHomeItemCategoryBeanList.add(mHomeItemBean)
        }
        mCategoryList.value = mHomeItemCategoryBeanList
    }

    /**
     *  因启动页性能更佳 获取协议详情放在首页（下次启动生效）
     */
    fun getProtocolInfo() {
        rxHttpRequest {
            onRequest = {

            }

        }
    }
}