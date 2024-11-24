package com.zeekrlife.common.ext

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.zeekrlife.common.R


/**
 * 显示消息弹窗
 * @param message 显示对话框的内容 必填项
 * @param title 显示对话框的标题 默认 温馨提示
 * @param positiveButtonText 确定按钮文字 默认确定
 * @param positiveAction 点击确定按钮触发的方法 默认空方法
 * @param negativeButtonText 取消按钮文字 默认空 不为空时显示该按钮
 * @param negativeAction 点击取消按钮触发的方法 默认空方法
 *
 */
fun Activity.showDialogMessage(
    message: String,
    title: String = "【温馨提示】",
    positiveButtonText: String = "确定",
    positiveAction: () -> Unit = {},
    negativeButtonText: String = "",
    negativeAction: () -> Unit = {}
) {
    if (!this.isFinishing) {
        MaterialDialog(this)
            .cornerRadius(8f)
            .cancelOnTouchOutside(false)
            .cancelable(false)
            .show {
                title(text = title)
                message(text = message)
                positiveButton(text = positiveButtonText) {
                    positiveAction.invoke()
                }
                if (negativeButtonText.isNotEmpty()) {
                    negativeButton(text = negativeButtonText) {
                        negativeAction.invoke()
                    }
                }
            }
    }
}

/**
 * @param message 显示对话框的内容 必填项
 * @param title 显示对话框的标题 默认 温馨提示
 * @param positiveButtonText 确定按钮文字 默认确定
 * @param positiveAction 点击确定按钮触发的方法 默认空方法
 * @param negativeButtonText 取消按钮文字 默认空 不为空时显示该按钮
 * @param negativeAction 点击取消按钮触发的方法 默认空方法
 */
fun Fragment.showDialogMessage(
    message: String,
    title: String = "【温馨提示】",
    positiveButtonText: String = "确定",
    positiveAction: () -> Unit = {},
    negativeButtonText: String = "",
    negativeAction: () -> Unit = {}
) {
    activity?.let {
        if (!it.isFinishing) {
            MaterialDialog(it)
                .cancelOnTouchOutside(false)
                .cancelOnTouchOutside(false)
                .cornerRadius(8f)
                .show {
                    title(text = title)
                    message(text = message)
                    positiveButton(text = positiveButtonText) {
                        positiveAction.invoke()
                    }
                    if (negativeButtonText.isNotEmpty()) {
                        negativeButton(text = negativeButtonText) {
                            negativeAction.invoke()
                        }
                    }
                }
        }
    }
}

/*****************************************loading框********************************************/
private var loadingDialog: Dialog? = null

/**
 * 打开等待框
 */
fun AppCompatActivity.showLoadingExt(message: String = "请求网络中...") {
    dismissLoadingExt()
    if (!this.isFinishing) {
        var tips : TextView? = null
        if (loadingDialog == null) {
            //弹出loading时 把当前界面的输入法关闭
            this.hideOffKeyboard()
            val loadingView = LayoutInflater.from(this@showLoadingExt)
                .inflate(R.layout.layout_zeekr_loading_view, null).apply {
                    tips = this.findViewById(R.id.loading_zeekr_tips)
                }
            loadingDialog = Dialog(this, R.style.loadingDialogTheme).apply {
                setCancelable(true)
                setCanceledOnTouchOutside(false)
                setContentView(loadingView)
            }
            loadingDialog?.setOnDismissListener {
                //设置dialog关闭时 置空 不然会出现 一个隐藏bug 这里就不细说了
                dismissLoadingExt()
            }
        }
        tips?.text = message
        loadingDialog?.show()
    }
}

/**
 * 打开等待框
 */
fun Fragment.showLoadingExt(message: String = "加载中...") {
    dismissLoadingExt()
    activity?.let {
        if (!it.isFinishing) {
            if (loadingDialog == null) {
                //弹出loading时 把当前界面的输入法关闭
                it.hideOffKeyboard()
                loadingDialog = Dialog(requireActivity(), R.style.loadingDialogTheme).apply {
                    setCancelable(true)
                    setCanceledOnTouchOutside(false)
                    setContentView(
                        LayoutInflater.from(it)
                            .inflate(R.layout.layout_zeekr_loading_view, null).apply {
                                this.findViewById<TextView>(R.id.loading_zeekr_tips).text = message
                            })
                }
                loadingDialog?.setOnDismissListener {
                    //设置dialog关闭时 置空 不然会出现 一个隐藏bug 这里就不细说了
                    dismissLoadingExt()
                }
            }
            loadingDialog?.show()
        }
    }
}

/**
 * 关闭等待框
 */
fun AppCompatActivity.dismissLoadingExt() {
    loadingDialog?.dismiss()
    loadingDialog = null
}

/**
 * 关闭等待框
 */
fun Fragment.dismissLoadingExt() {
    loadingDialog?.dismiss()
    loadingDialog = null
}


