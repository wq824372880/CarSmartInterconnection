package com.zeekrlife.connect.core;


import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.util.Log;

import com.ecarx.xui.adaptapi.diminteraction.DimInteraction;
import com.ecarx.xui.adaptapi.diminteraction.IPhoneCallInteraction;
import com.zeekrlife.connect.core.data.repository.HiCarRequestCode;

/**
 * Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *
 * @description:
 * @author: Yueming.Zhao
 * @date: 2023/7/3 15:59:00
 * @version: V1.0
 */
public class HiCarDimManager {

    public static final String TAG = "HiCarDimManagers";

    public Context mContext;
    public DimInteraction mDimInteraction;
    private IPhoneCallInteraction mIPhoneCallInteraction;

    private volatile static HiCarDimManager sInstance;

    public HiCarDimManager(Context context) {
        this.mContext = context;
        checkDimInteraction(context);
    }

    public static HiCarDimManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new HiCarDimManager(context);
        }
        return sInstance;
    }


    /**
     * hicar来电时 调用 发送仲裁信号给方控
     */
    public void updateDimInfo(){

        checkDimInteraction(mContext);

        if (mIPhoneCallInteraction != null) {
            /**
             * hicar 不需要在方控上显示通话元数据， 这里暂时返回空对象，仅通知dim做方控仲裁
             */
            mIPhoneCallInteraction.updateCallInfo(new IPhoneCallInteraction.ICallInfo() {
                @Override
                public Bitmap getAvatar() {
                    return null;
                }

                @Override
                public int getActiveCallId() {
                    return 0;
                }

                @Override
                public boolean isMicOnVehicleMuted() {
                    return false;
                }

                @Override
                public boolean isRingtoneMuted() {
                    return false;
                }

                @Override
                public boolean isHandFree() {
                    return false;
                }

                @Override
                public int getCallId() {
                    return 0;
                }

                @Override
                public String getContactName() {
                    return null;
                }

                @Override
                public String getContactNumber() {
                    return null;
                }

                @Override
                public int getCallStatus() {
                    return 0;
                }

                @Override
                public long getCallDuration() {
                    return 0;
                }

                @Override
                public int getCallCount() {
                    return 0;
                }

                @Override
                public int getIndex() {
                    return 0;
                }
            });

        }
    }


    /**
     * 检查dimInteraction是否初始化
     *
     * @param context
     */
    private void checkDimInteraction(Context context) {
        Log.e(TAG, "checkDimInteraction() ");
        try {
            if (mDimInteraction == null || mIPhoneCallInteraction == null ) {
                Log.e(TAG, "init checkDimInteraction() ");
                if (mDimInteraction == null) {
                    mDimInteraction = DimInteraction.create(context);
                }
                if (mDimInteraction != null) {
                    if (mIPhoneCallInteraction == null) {
                        mIPhoneCallInteraction = mDimInteraction.getPhoneCallInteraction();
                        if (mIPhoneCallInteraction != null) {
                            Log.e(TAG, "---registerPhoneCallback---");
                            mIPhoneCallInteraction.registerPhoneCallback(iPhoneCallInteractionCallback);
                        }
                    }
                } else {
                    throw new IllegalStateException("DimInteraction.create() failed");
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public IPhoneCallInteraction.IPhoneCallInteractionCallback iPhoneCallInteractionCallback = new IPhoneCallInteraction.IPhoneCallInteractionCallback() {
        @Override
        public void onAnswerCall(String s) {
            Log.e(TAG, "---onAnswerCall----" + s);
            try {
                ConnectServiceImpl.getInstance().sendKeyEvent(HiCarRequestCode.KEYCODE_CALL,HiCarRequestCode.CALL_STATE_OFFHOOK);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAnswerAndHoldCall(String s) {
            Log.e(TAG, "---onAnswerAndHoldCall----" + s);
        }

        @Override
        public void onAnswerAndEndCall(String s) {
            Log.e(TAG, "---onAnswerAndEndCall----" + s);
        }

        @Override
        public void onEndCall(String s) {
            Log.e(TAG, "---onEndCall----" + s);

            try {
                ConnectServiceImpl.getInstance().sendKeyEvent(HiCarRequestCode.KEYCODE_ENDCALL,HiCarRequestCode.CALL_STATE_IDLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCallInfoUpdateRequired() {
            Log.e(TAG, "---onCallInfoUpdateRequired----");
        }

        @Override
        public void onRequestConnectedMobileDeviceInfo() {
            Log.e(TAG, "---onRequestConnectedMobileDeviceInfo----");
        }

        @Override
        public void onSwitchCall() {
            Log.e(TAG, "---onSwitchCall----");
        }

        @Override
        public void onSwitchMicMode(int i) {
            Log.e(TAG, "---onSwitchMicMode----");
        }

        @Override
        public void onSwitchChannel(int i) {
            Log.e(TAG, "---onSwitchChannel----");
        }

        @Override
        public void placeCall(String s) {
            Log.e(TAG, "---placeCall----");
        }

        @Override
        public void onSwitchRingtoneMuteMode(int i) {
            Log.e(TAG, "---onSwitchRingtoneMuteMode----");
        }

        @Override
        public void onIgnoreCall(String s) {
            Log.e(TAG, "---onIgnoreCall----");
        }
    };
}
