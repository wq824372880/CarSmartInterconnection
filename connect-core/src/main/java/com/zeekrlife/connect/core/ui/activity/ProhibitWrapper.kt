package com.zeekrlife.connect.core.ui.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import com.zeekr.component.dialog.ZeekrDialogCreate
import com.zeekr.sdk.mediacenter.bean.ClientType
import com.zeekr.sdk.mediacenter.impl.MediaCenterAPI
import com.zeekrlife.connect.core.R
import com.zeekrlife.net.interception.logging.util.logE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProhibitWrapper private constructor() {

    private var mCountDownNum = 5
    private val TAG = "SurfaceActivity"
    private var zeekrDialog: ZeekrDialogCreate? = null

    companion object {
        val instance: ProhibitWrapper by lazy { ProhibitWrapper() }
    }

    private lateinit var mContext: Context
    private lateinit var mFunction: () -> Unit
    private var abandonCountDown = false
    fun show(context: Context, function: () -> Unit): ProhibitWrapper {
        "---prohibit onShow()--->".logE(TAG)
        this.mContext = context
        this.mFunction = function
        mCountDownNum = 5
        initDialog()
        return instance
    }

    private var confirmTv: TextView? = null

    private fun initDialog() {
        abandonCountDown = false
        zeekrDialog = ZeekrDialogCreate(mContext)
            .apply {
                mergeLayout {
                    val inflate =
                        LayoutInflater.from(mContext).inflate(R.layout.dialog_prohibit, it)
                    confirmTv = inflate.findViewById(R.id.tvConfirm)
                    confirmTv?.setOnClickListener {
                        "mFunction exit invoke--->".logE(TAG)
                        mFunction.invoke()
                        dismiss()
                    }
                }
                touchOutsideListener {
                    ObjectAnimator.ofFloat(it, "translationX", -10f, 10f).apply {
                        duration = 70
                        repeatCount = 5
                        repeatMode = ValueAnimator.REVERSE
                    }.start()
                }
                applyData()
            }
    }

    fun startCountDown(scope: CoroutineScope, finishFuc: () -> Unit) {

        scope.launch(Dispatchers.Default) {
            flow {
                for (t in mCountDownNum downTo 0) {
                    if (abandonCountDown) break
                    emit(t)
                    if (t != 0) delay(1000)
                }
            }.flowOn(Dispatchers.Main)
                .onEach {
                    "onEach----> $it".logE(TAG)
                    confirmTv?.text =
                        String.format("确认退出 (%d)", it)
                }.onCompletion {
                    "finishFuc:----->".logE(TAG)
                    dismiss()
                    finishFuc.invoke()
                }.launchIn(scope)
        }
    }

    fun dismiss() {
        zeekrDialog?.dialogAction?.dismiss()
        abandonCountDown = true
        zeekrDialog = null
    }

    val isProhibitBylaw: Boolean
        get() {
            if (MediaCenterAPI.get() == null) {
                Log.e(TAG, "MediaCenterAPI null() called")
                return false
            }
            return MediaCenterAPI.get().getDrivingRestrictions(ClientType.VIDEO)
        }

}