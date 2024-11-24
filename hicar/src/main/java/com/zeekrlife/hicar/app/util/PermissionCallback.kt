package com.zeekrlife.hicar.app.util

import android.app.Activity
import android.content.*
import android.os.*
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
//import com.hjq.toast.ToastUtils
import com.zeekrlife.common.ext.getStringExt
import com.zeekrlife.common.ext.showDialogMessage
import com.zeekrlife.common.util.ToastUtils
import com.zeekrlife.hicar.R
import java.util.*

/**
 *    desc   : 权限申请回调封装
 */
abstract class PermissionCallback(activity: Activity) : OnPermissionCallback {

    var mActivity: Activity = activity

    override fun onDenied(permissions: MutableList<String>, never: Boolean) {
        if (never) {
            showPermissionDialog(mActivity,permissions)
            return
        }
        if (permissions.size == 1 && (Permission.ACCESS_BACKGROUND_LOCATION == permissions[0])) {
            ToastUtils.show(getStringExt(R.string.common_permission_fail_4))
            return
        }
        ToastUtils.show(getStringExt(R.string.common_permission_fail_1))
    }

    /**
     * 显示授权对话框
     */
    private fun showPermissionDialog(activity: Activity,permissions: MutableList<String>) {
        activity.showDialogMessage(
            getPermissionHint(activity, permissions),
            getStringExt(R.string.common_permission_alert),
            getStringExt(R.string.common_confirm),
            positiveAction = {
//                XXPermissions.startPermissionActivity(activity, permissions)
            }
            ,
            getStringExt(R.string.common_cancel),
        )
    }

    /**
     * 根据权限获取提示
     */
    private fun getPermissionHint(context: Context, permissions: MutableList<String>): String {
        if (permissions.isEmpty()) {
            return context.getString(R.string.common_permission_fail_2)
        }
        val hints: MutableList<String> = ArrayList()
        for (permission: String? in permissions) {
            when (permission) {
                Permission.READ_EXTERNAL_STORAGE,
                Permission.WRITE_EXTERNAL_STORAGE,
                Permission.MANAGE_EXTERNAL_STORAGE -> {
                    val hint: String = context.getString(R.string.common_permission_storage)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }
                Permission.CAMERA -> {
                    val hint: String = context.getString(R.string.common_permission_camera)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }
                Permission.RECORD_AUDIO -> {
                    val hint: String = context.getString(R.string.common_permission_microphone)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }
                Permission.ACCESS_FINE_LOCATION,
                Permission.ACCESS_COARSE_LOCATION,
                Permission.ACCESS_BACKGROUND_LOCATION -> {
                    val hint: String = if (!permissions.contains(Permission.ACCESS_FINE_LOCATION) &&
                        !permissions.contains(Permission.ACCESS_COARSE_LOCATION)) {
                        context.getString(R.string.common_permission_location_background)
                    } else {
                        context.getString(R.string.common_permission_location)
                    }
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }
                Permission.READ_PHONE_STATE,
                Permission.CALL_PHONE,
                Permission.ADD_VOICEMAIL,
                Permission.USE_SIP,
                Permission.READ_PHONE_NUMBERS,
                Permission.ANSWER_PHONE_CALLS -> {
                    val hint: String = context.getString(R.string.common_permission_phone)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }
                Permission.GET_ACCOUNTS,
                Permission.READ_CONTACTS,
                Permission.WRITE_CONTACTS -> {
                    val hint: String = context.getString(R.string.common_permission_contacts)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }
                Permission.READ_CALENDAR,
                Permission.WRITE_CALENDAR -> {
                    val hint: String = context.getString(R.string.common_permission_calendar)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }
                Permission.READ_CALL_LOG,
                Permission.WRITE_CALL_LOG,
                Permission.PROCESS_OUTGOING_CALLS -> {
                    val hint: String = context.getString(if (Build.VERSION.SDK_INT >= 30) R.string.common_permission_call_log else R.string.common_permission_phone)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }
                Permission.BODY_SENSORS -> {
                    val hint: String = context.getString(R.string.common_permission_sensors)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }
                Permission.ACTIVITY_RECOGNITION -> {
                    val hint: String = context.getString(R.string.common_permission_activity_recognition)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }
                Permission.SEND_SMS,
                Permission.RECEIVE_SMS,
                Permission.READ_SMS,
                Permission.RECEIVE_WAP_PUSH,
                Permission.RECEIVE_MMS -> {
                    val hint: String = context.getString(R.string.common_permission_sms)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }
                Permission.REQUEST_INSTALL_PACKAGES -> {
                    val hint: String = context.getString(R.string.common_permission_install)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }
                Permission.NOTIFICATION_SERVICE -> {
                    val hint: String = context.getString(R.string.common_permission_notification)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }
                Permission.SYSTEM_ALERT_WINDOW -> {
                    val hint: String = context.getString(R.string.common_permission_window)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }
                Permission.WRITE_SETTINGS -> {
                    val hint: String = context.getString(R.string.common_permission_setting)
                    if (!hints.contains(hint)) {
                        hints.add(hint)
                    }
                }
            }
        }
        if (hints.isNotEmpty()) {
            val builder: StringBuilder = StringBuilder()
            for (text: String? in hints) {
                if (builder.isEmpty()) {
                    builder.append(text)
                } else {
                    builder.append("、")
                        .append(text)
                }
            }
            builder.append(" ")
            return context.getString(R.string.common_permission_fail_3, builder.toString())
        }
        return context.getString(R.string.common_permission_fail_2)
    }
}