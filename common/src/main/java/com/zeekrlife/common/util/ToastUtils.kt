package com.zeekrlife.common.util

import android.content.Context
import android.widget.Toast
import com.zeekr.basic.appContext
import com.zeekr.component.toast.showToast
import com.zeekr.dialog.toast.ZeekrToast
import com.zeekr.dialog.toast.inflateToastLayout
import com.zeekr.dialog.toast.inflateToastPaddingTextLayout
import com.zeekrlife.common.ext.getScreenWidthIs2560
import com.zeekrlife.net.interception.logging.util.logE

/**
 * @author
 */
object ToastUtils {

    fun show(context: Context, message: String, icon: Int = 0,duration: Int = Toast.LENGTH_SHORT) {
        try {
            if(getScreenWidthIs2560()){
                context.showToast(message,icon,duration)
            }else{
                ZeekrToast.show(context) { context.inflateToastPaddingTextLayout(message) }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun show(message: String) {
        try {
            if(getScreenWidthIs2560()){
                appContext.showToast(message)
            }else{
                ZeekrToast.show(appContext) { appContext.inflateToastPaddingTextLayout(message) }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showNoPadding(message: String) {
        try {
            ZeekrToast.show(appContext) { appContext.inflateToastLayout(message) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}