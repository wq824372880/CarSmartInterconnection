package com.zeekrlife.connect.core.manager.sensor
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.LinearInterpolator
import com.zeekr.component.button.ZeekrButton
import com.zeekr.dialog.ZeekrDialogCreate
import com.zeekr.dialog.action.ZeekrDialogAction
import com.zeekr.dialog.button.WhichButton
import com.zeekr.dialog.extention.DialogCallback
import com.zeekrlife.connect.core.R
import com.zeekrlife.net.interception.logging.util.logE
import java.lang.ref.WeakReference

object TrafficRestrictionsManager {

    private const val TAG = "TrafficRestrictionsManager"

    @SuppressLint("StaticFieldLeak")
    private var confirmSimpleShow: ZeekrDialogCreate? = null

    private var toPlayVideo: ((Boolean) -> Unit)? = null

    private var valueAnimator: ValueAnimator? = null

    private var mActivity: WeakReference<Activity>? = null

    private val btnSoundManager by lazy { BtnSoundManager.sInstance }

    private val myHandler = Handler(Looper.getMainLooper()) {
        if (it.what == 1) {
            "currentActivity==${mActivity?.get()}".logE(TAG)
            mActivity?.get()?.finish()
        }
        true
    }


    fun initTrafficRestrictionsListener(
        mActivity: WeakReference<Activity>,
        toPlayVideo: ((Boolean) -> Unit)? = null
    ) {
        this.mActivity = mActivity
        this.toPlayVideo = toPlayVideo
        Log.e(TAG, "initTrafficRestrictionsListener() called")
        if (!btnSoundManager.isCanPlayVideo) {
            showNotPGearDialog()
        }
        btnSoundManager.initVideoCanPlayListener {
            if (!it) {
                showNotPGearDialog()
            } else {
                disNotPGGearDialog()
            }

        }
    }


    /**
     * 视频无法播放的时候弹出弹窗
     */
    private fun showNotPGearDialog() {
        Log.d(TAG, "showNotPGearDialog() called with: ")
        confirmSimpleShow?.let {
            if (it.isShowing) {
                Log.d(TAG, "showNotPGearDialog()  isShowing")
                return
            }
        }
        myHandler.postDelayed({
            if (confirmSimpleShow == null) {
                mActivity?.get()?.let {
                    Log.d(TAG, "showNotPGearDialog() called with: init confirmSimpleShow")
                    confirmSimpleShow = ZeekrDialogCreate.Confirm(it)
                        .content("为了您和他人的安全,请不要在驾驶过程中观看视频娱乐内容!")
                        .buttonsVisible(WhichButton.POSITIVE)
                        .positiveButton(
                            null,
                            dialogConfirmDes("5"),
                            object : DialogCallback {
                                override fun invoke(p1: ZeekrDialogAction) {
                                    disNotPGGearDialog()
                                    myHandler.sendEmptyMessageDelayed(1, 500)
                                }
                            }).dismissOnTouchOutside(false).dismissOnBackPressed(false)
//                    if (BuildConfig.PRODUCT_NAME!=DLNAVideoApplication.CS1E){
//                        confirmSimpleShow?.dialogSize(it.resources.getDimensionPixelSize(R.dimen.width_dialog_input), 376)
                        confirmSimpleShow?.dialogSize(534, 376)
//                    }
                }
            }
            if (valueAnimator == null) {
                Log.e(TAG, "init valueAnimator")
                valueAnimator = ValueAnimator.ofInt(5, 0)
                valueAnimator?.apply {
                    this.duration = 5000
                    this.interpolator = LinearInterpolator()
                }
            }
            valueAnimator?.addUpdateListener {
                val animatedValue = it.animatedValue as Int
                Log.e(TAG, "animatedValue:$animatedValue")
                if (animatedValue == 0) {
                    disNotPGGearDialog()
                    myHandler.sendEmptyMessageDelayed(1, 500)
                }
                confirmSimpleShow?.getActionButton(WhichButton.POSITIVE)
                    ?.findViewById<ZeekrButton>(R.id.zeekr_dialog_positive_button)?.text =
                    dialogConfirmDes(animatedValue.toString())
            }

            toPlayVideo?.invoke(false)
            mActivity?.get()?.let {
                if (!it.isFinishing) {
                    confirmSimpleShow?.show()
                    valueAnimator?.start()
                }
            }
        }, 400)

    }

    private fun dialogConfirmDes(millions: String): String {
//        return DLNAVideoApplication.getMyApplication()!!
//            .getString(R.string.video_txt_confirm, millions)
        return "hhhhh($millions)"
    }


    private fun disNotPGGearDialog() {
        Log.e(TAG, "disNotPGearDialog() called")
        myHandler.post {
            valueAnimator?.cancel()
            valueAnimator?.removeAllUpdateListeners()
            valueAnimator?.removeAllListeners()
            valueAnimator = null
            confirmSimpleShow?.dismiss()
            confirmSimpleShow = null
            toPlayVideo?.invoke(true)
        }

    }
}
