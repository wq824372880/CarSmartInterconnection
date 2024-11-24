package com.zeekrlife.hicar.utils

import android.content.Context
import android.os.Process.killProcess
import android.provider.Settings
import android.util.Log
import com.zeekr.basic.appContext
import com.zeekr.basic.currentActivity
import com.zeekr.basic.finishAllActivity
import com.zeekr.component.dialog.DialogButtonCallback
import com.zeekr.component.dialog.ZeekrDialogAction
import com.zeekr.component.dialog.ZeekrDialogCreate
import com.zeekr.component.dialog.common.DialogParam
import com.zeekrlife.common.ComConstants
import com.zeekrlife.common.util.ToastUtils
import com.zeekrlife.common.util.threadtransform.ThreadPoolUtil
import com.zeekrlife.connect.base.manager.ConnectServiceManager
import com.zeekrlife.hicar.R
import com.zeekrlife.hicar.app.App
import com.zeekrlife.hicar.data.PageType
import com.zeekrlife.hicar.ui.activity.MainActivity
import com.zeekrlife.hicar.ui.fragment.PinFragment
import com.zeekrlife.net.interception.logging.util.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.system.exitProcess

/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description:
 *@author: Yueming.Zhao
 *@date: 2023/8/23 19:14:12
 *@version: V1.0
 */
object DisplayConflictUtil {

    /**
     * 当前冲突 处理 只处理 hicar与carlink
     * 当打开首页后 判断另一种投屏在连接中 -> 弹出确认框
     */
    const val FROM_IDLE = 0
    const val FROM_DLNA = 1 //影视投屏（DLNA）
    const val FROM_USB = 2 //有线投屏
    const val FROM_HICAR = 3
    const val FROM_CARLINK = 4
    const val FROM_WIRELESSMIRROR = 5 //无线镜像
    const val FROM_HICAR_PREPARE = 33 //hicar未投屏但抢占了焦点  场景：启动后发生投屏冲突，关掉正在连接的投屏方式，抢占投屏焦点但未开始投屏
    const val FROM_CARLINK_PREPARE = 44 //carlink未投屏但抢占了焦点  场景：启动后发生投屏冲突，关掉正在连接的投屏方式，抢占投屏焦点但未开始投屏

    const val CASTTYPE = "CastType"  // global key
    val TAG = "DisplayConflictUtil"
    var isGoMain = false
    val currentDeviceType = ""
    private val mutex = Mutex()
    fun disposeConflict(ctx: Context, gotoMain: () -> Unit){

        val connectServerName =  "carlink"

        ZeekrDialogCreate(ctx).show {
            title(ctx.getString(R.string.switch_device))
            content(String.format(ctx.getString(R.string.switch_device_content),connectServerName))

            realButton (R.string.common_confirm,null,object :DialogButtonCallback{
                override fun invoke(p1: ZeekrDialogAction) {
                    isGoMain = true
                    dialogAction.dismiss()
                    ToastUtils.show("$connectServerName 连接已断开")
                    ConnectServiceManager.getInstance().disconnectCarlink()
                }
            })
            dismissOnListener {
                if (isGoMain) {
                    gotoMain.invoke()
                }
            }
            ghostButton (R.string.common_cancel,null,object :DialogButtonCallback{
                override fun invoke(p1: ZeekrDialogAction) {

                    finishAllActivity()
                }
            })
        }
    }


    fun isConflict(ctx:Context,fromType:Int, gotoMain: () -> Unit):Boolean{


        val currentType = Settings.System.getInt(ctx.contentResolver, CASTTYPE, 0)

        Log.e(TAG,"---curremtType: $currentType")

        val conflict = fromType == FROM_HICAR && currentType == FROM_CARLINK

        if(!conflict){
            gotoMain.invoke()
        }

        return  conflict
    }

    var loadingDialog: ZeekrDialogAction? = null

    fun disposeAllConflict(activity: MainActivity?, info: Pair<Boolean, String>){
        MainScope().launch(Dispatchers.Main) {
          mutex.withLock {
              delay(300)
              val dialogParam = DialogParam(
                  isDismissOnBackPressed = false,
                  isDismissOnTouchOutside = false,
              )
              "disposeAllConflict current:${activity}".logE(TAG)
              if(activity == null) return@launch
              activity.mViewModel.updatePageType(PageType.CONNECTION_CODE_GET)
              if(loadingDialog?.isShowing == true) return@launch
              loadingDialog?.dialog?.dismiss()
              loadingDialog?.dismiss()
              loadingDialog = null
              loadingDialog = ZeekrDialogCreate(activity).show {
                  dialogParam(dialogParam)
                  title(currentActivity!!.getString(R.string.switch_device))
                  content(String.format(activity.getString(R.string.switch_device_content),info.second))

                  realButton (R.string.common_confirm,null,object :DialogButtonCallback{
                      override fun invoke(zeekrDialogAction: ZeekrDialogAction) {
                          ConnectServiceManager.getInstance().closeWifi()
                          isGoMain = true
                          zeekrDialogAction.dismiss()
                          loadingDialog?.dismiss()
                          //通知其他投屏设备断开
                          Settings.System.putInt(activity.contentResolver, CASTTYPE, FROM_HICAR_PREPARE)
                          MainScope().launch {
                              delay(1000)
                              ConnectServiceManager.getInstance().openWifi()
                              ConnectServiceManager.getInstance().modifyAPPassWord("123456780")
                              delay(1000)
                              HiCarServiceManager.handleStartAdv(activity)
                          }

                      }
                  })

                  ghostButton (R.string.common_cancel,null,object :DialogButtonCallback{
                      override fun invoke(zeekrDialogAction: ZeekrDialogAction) {
//                    ConnectServiceManager.getInstance().disconnectDevice(PinFragment.mDeviceId)
                          zeekrDialogAction.dismiss()
                          loadingDialog?.dismiss()
                          MainScope().launch(Dispatchers.Main) {
                              delay(500)
                              finishAllActivity()
                          }
                      }
                  })
              }
          }
        }

    }


    fun isRandomConflict(fromType:Int, callBack: ((info: Pair<Boolean, String>) -> Unit)? = null){

        var conflict = false
        var currentDevice = ""
        val currentType = Settings.System.getInt(App.application.contentResolver, CASTTYPE, 0)
        val currentType2 = ConnectServiceManager.getInstance().currentCastType
//        Log.e(TAG,"---curremtType: $currentType")
        Log.e(TAG,"---curremtType2: $currentType2")
        when(currentType2){
            FROM_IDLE ->{
                conflict = false
//                currentDevice = "HUAWEI HiCar"
            }
            FROM_DLNA ->{
                if(fromType != FROM_DLNA){
                    conflict = true
                    currentDevice = "影视投屏（DLNA）"
                }
            }
            FROM_USB ->{
                if(fromType != FROM_USB){
                    conflict = true
                    currentDevice = "有线投屏"
                }
            }
            FROM_HICAR ->{
                if(fromType != FROM_HICAR){
                    conflict = true
                    currentDevice = "HUAWEI HiCar"
                }
            }
            FROM_CARLINK ->{
                if(fromType != FROM_CARLINK){
                    conflict = true
                    currentDevice = "CarLink"
                }
            }
            FROM_WIRELESSMIRROR ->{
                if(fromType != FROM_WIRELESSMIRROR){
                    conflict = true
                    currentDevice = "无线镜像"
                }
            }
            FROM_HICAR_PREPARE ->{
                conflict = false
                currentDevice = "HUAWEI HiCar"
            }
            FROM_CARLINK_PREPARE ->{
                conflict = false
                currentDevice = "CarLink"
            }


        }
        callBack?.invoke(Pair(conflict,currentDevice))


//        val conflict = fromType == ComConstants.FROM_HICAR || currentType == ComConstants.FROM_CARLINK
    }
}