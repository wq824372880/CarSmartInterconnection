package com.zeekrlife.carlink.ui.fragment.pinchildfragment

import android.os.Bundle
import android.util.Log
import android.view.*
import com.zeekrlife.carlink.databinding.PinSurfaceLayoutBinding

/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description: 投屏
 *@author: e-Yang.Dong1
 *@date: 2023/5/16 15:49:23
 *@version: V1.0
 */
class PinSurfaceFragment : PinChildBaseFragment<PinSurfaceLayoutBinding>() {

    val TAG = "PinSurfaceFragment"

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): PinSurfaceLayoutBinding {
       return PinSurfaceLayoutBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mBind.surface.viewTreeObserver.addOnGlobalLayoutListener(mGlobalLayoutListener)


    }

    override fun onDestroyView() {
        mBind.surface.viewTreeObserver.removeOnGlobalLayoutListener(mGlobalLayoutListener)
        super.onDestroyView()
    }

    private val mGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener = object :
        ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            mBind.surface.viewTreeObserver.removeOnGlobalLayoutListener(this)
            mBind.surface.holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
                    Log.i(TAG, "surface view created")
                }

                override fun surfaceChanged(
                    surfaceHolder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                    Log.i(TAG, "surface view changed: width = $width, height = $height")
//                    if (mService == null) {
//                        Log.i(TAG, "surfaceChanged: mDemoService is null")
//                        return
//                    }
                    if (surfaceHolder.surface != null && surfaceHolder.surface.isValid
                    ) {
//                        if (HiCarCoreServiceManager.getInstance().updateCarConfig(surfaceHolder.surface, width, height)) {
//                            HiCarCoreServiceManager.getInstance().startProjection()
//                        }
                    } else {
                        Log.i(TAG, "surface holder or surface is null or invalid")
//                        HiCarCoreServiceManager.getInstance().pauseProjection()
                    }
                }

                override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
                    Log.i(TAG, "surface view destroyed")
//                    HiCarCoreServiceManager.getInstance().stopProjection()
                }
            })
        }
    }

}