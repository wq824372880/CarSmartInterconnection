package com.zeekrlife.hicar.ui.activity

import android.net.Uri
import android.os.Bundle
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.common.ext.toStartActivity
import com.zeekrlife.hicar.app.base.BaseActivity
import com.zeekrlife.hicar.app.eventViewModel
import com.zeekrlife.hicar.data.response.OrderReCharged
import com.zeekrlife.hicar.databinding.ActivityEntryBinding

/**
 * 网关层
 * 外部跳转入口类 所有从外部跳转到AppStore的,必须经过这个类分发
 * 除了EntryActivity和LauncherActivity之外，所有的Activity子类都要设置export=false
 */
class EntryActivity : BaseActivity<BaseViewModel, ActivityEntryBinding>() {

    private val routeMap: HashMap<String?, String?> = object : HashMap<String?, String?>() {
        init {
            put("main", ACT_MAIN)
            put("detail", ACT_APPS)
            put("search", ACT_SEARCH)
            put("manage", ACT_MANAGE_HOME)
            put("apps", ACT_APPS) //兼容dc的应用跳转详情
        }
    }

    companion object {
        var ACT_MAIN = "/main/MainActivity"
        var ACT_APPS = "/detail/DetailActivity"
        var ACT_SEARCH = "/search/SearchActivity"
        var ACT_MANAGE_HOME = "/manage/ManageHomeActivity"

        var EXTRA_KEY_ORDERNO = "KEY_ORDERNO"
        var EXTRA_KEY_ORDERSTATUS = "KEY_ORDERSTATUS"
        var EXTRA_KEY_USERID = "KEY_USERID"
        var EXTRA_KEY_DESCRIPTION = "KEY_DESCRIPTION"
        var EXTRA_KEY_ORDERSOURCE = "KEY_ORDERSOURCE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        if (intent == null) {
            finish()
            return
        }
        val uri = intent.data
        val extras = intent.extras
        if (uri != null) {
            if (uri.getQueryParameters(REAL_PATH) != null
                && uri.getQueryParameters(REAL_PATH).size != 0
                || extras != null && extras.containsKey(REAL_PATH)
            ) {
                return
            }
            val pathSegments = uri.pathSegments
            if (pathSegments == null) {
                finish()
                return
            }
            val pathLayer = pathSegments.size
            if (pathLayer == 1 && routeMap.containsKey(pathSegments[0])) {
                when (pathSegments[0]) {
                    "main" -> toStartActivity(MainActivity::class.java)
                    "manage" -> toStartActivity(MainActivity::class.java)
                    else -> toStartActivity(MainActivity::class.java)
                }
            } else if (pathLayer == 1 && "rechargedevent" == pathSegments[0]) {
                val orderno = intent.getStringExtra(EXTRA_KEY_ORDERNO)
                val orderStatus = intent.getStringExtra(EXTRA_KEY_ORDERSTATUS)
                val userid = intent.getStringExtra(EXTRA_KEY_USERID)
                val description = intent.getStringExtra(EXTRA_KEY_DESCRIPTION)
                val ordersource = intent.getStringExtra(EXTRA_KEY_ORDERSOURCE)

                eventViewModel.orderReChargedEvent.postValue(OrderReCharged(orderno, orderStatus, userid, description, ordersource))
            }
        }
        finish()
    }


    /**
     * 跳转搜索
     * @param uri   xc://com.zeekrlife.market/search?keyword=xxx
     */
    private fun startSearchActivity(uri: Uri) {
        val keyword = uri.getQueryParameter("keyword")
        toStartActivity(MainActivity::class.java, Bundle().apply {
            putString("keyword", keyword)
        })
    }
}