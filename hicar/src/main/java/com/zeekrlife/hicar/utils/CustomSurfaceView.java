package com.zeekrlife.hicar.utils;

import android.content.Context;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.huawei.hicarsdk.HiSightSurfaceView;
import com.zeekrlife.connect.base.manager.ConnectServiceManager;

public class CustomSurfaceView extends HiSightSurfaceView {
    public CustomSurfaceView(Context context) {
        super(context);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            ConnectServiceManager.getInstance().onTouchEvent(event);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
