package com.zeekrlife.hicar.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeekrlife.hicar.R

class TestAdapter(data:ArrayList<Any>) :BaseQuickAdapter<Any,BaseViewHolder>(R.layout.item_test),LoadMoreModule{
    
    override fun convert(holder: BaseViewHolder, item: Any) {
        holder.setText(R.id.item_test_button,item.toString())
    }

}