package com.zeekrlife.hicar.ui.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import com.zeekrlife.hicar.R
import com.zeekrlife.net.api.NetUrl
import com.zeekrlife.hicar.app.base.BaseFragment
import com.zeekrlife.hicar.databinding.ActivityListBinding
import com.zeekrlife.hicar.ui.adapter.TestAdapter
import com.zeekrlife.hicar.ui.viewmodel.ListViewModel
import com.zeekrlife.common.ext.*
import com.zeekrlife.net.load.LoadStatusEntity
import com.zeekrlife.common.util.decoration.DividerOrientation
import com.zeekrlife.hicar.app.aop.SingleClick
import com.zeekrlife.net.interception.logging.util.logD
import com.zeekrlife.net.interception.logging.util.toast

class TestFragment1 : BaseFragment<ListViewModel, ActivityListBinding>() {
    private var isLoaded = false
    private val testAdapter: TestAdapter by lazy { TestAdapter(arrayListOf()) }

    override fun initView(savedInstanceState: Bundle?) {
//
//        "我是test1 initView".logD()
//        mBind.listSmartRefresh.refresh {
//            //刷新
//            mViewModel.getList(true)
//        }.loadMore {
//            //加载更多
//            mViewModel.getList(false)
//        }
//        //初始化 recycleView
//        mBind.listRecyclerView.grid(1).divider {
//            orientation = DividerOrientation.GRID
//            includeVisible = true
//            setDivider(10,true)
//            setColor(getColorExt(R.color.colorRed))
//        }.adapter = testAdapter
    }

    /**
     * 懒加载 第一次获取视图的时候 触发
     * 由于纵向tablayout配合Fragment剔除了viewadapter 所以此方法实现懒加载，正常重写 onLoadRetry()即可实现懒加载
     */
    override fun onHiddenChanged(hidden: Boolean) {
        if(!hidden && !isLoaded){
            isLoaded = true
            "我是test1 isLoaded$isLoaded".logD()
            onLoadRetry()
        }
    }

    /**
     * 请求成功
     */
    override fun onRequestSuccess() {
        mViewModel.listData.observe(this, Observer {
            //请求到列表数据
            testAdapter.loadListSuccess(it,mBind.listSmartRefresh)
        })
    }

    /**
     * 请求失败
     * @param loadStatus LoadStatusEntity
     */
    override fun onRequestError(loadStatus: LoadStatusEntity) {
        when (loadStatus.requestCode) {
            NetUrl.HOME_LIST -> {
                //列表数据请求失败
                testAdapter.loadListError(loadStatus,mBind.listSmartRefresh)
            }
        }
    }

    /**
     * 错误界面 空界面 点击重试
     */
    override fun onLoadRetry() {
        "我是test1 onLoadRetry$isLoaded".logD()
        mViewModel.getList(isRefresh = true, loadingXml = true)
    }

    override fun onBindViewClick() {
        super.onBindViewClick()

        testAdapter.setOnItemClickListener @SingleClick
        { adapter, view, position ->
            "防重复点击使用例子".toast()
        }
    }
}