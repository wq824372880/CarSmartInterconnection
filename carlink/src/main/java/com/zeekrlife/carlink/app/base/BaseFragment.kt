package com.zeekrlife.carlink.app.base

import androidx.viewbinding.ViewBinding
import com.zeekrlife.common.base.BaseVbFragment
import com.zeekrlife.common.base.BaseViewModel

/**
 * 描述　: 新创建的 使用 ViewBinding，需要自定义修改什么就重写什么 具体方法可以 搜索 BaseIView 查看
 */
abstract class BaseFragment<VM : BaseViewModel,DB: ViewBinding> : BaseVbFragment<VM, DB>(){

    //需要自定义修改什么就重写什么 具体方法可以 搜索 BaseIView 查看

}