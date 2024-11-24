//package com.zeekrlife.hicar.utils;
//
//import android.util.Log;
//
//import com.aispeech.AIError;
//import com.aispeech.common.AIConstant;
//import com.aispeech.export.listeners.AIWakeupListener;
//import com.aispeech.lite.oneshot.OneshotCache;
//import com.zeekrlife.connect.base.manager.ConnectServiceManager;
//
//public class AISpeechListenerImpl implements AIWakeupListener {
//
//    public static final String TAG = "AISpeechListenerImpl";
//
//    @Override
//    public void onError(AIError error) {
//        Log.e(TAG, "----onError:" + error.getMessage());
//    }
//
//    @Override
//    public void onInit(final int status) {
//        Log.e(TAG, "Init result " + status);
//        if (status == AIConstant.OPT_SUCCESS) {
//
//        } else {
//        }
//    }
//
//
//    @Override
//    public void onWakeup(String recordId, final double confidence, final String wakeupWord) {
//        Log.d(TAG, "wakeup foreground");
//        Log.e(TAG,"\n唤醒成功  wakeupWord = " + wakeupWord + "  confidence = " + confidence
//                + "\n");
//        //在这里启动hiCar语音
//        ConnectServiceManager.getInstance().changeHiCarVoiceState(true,wakeupWord);
//    }
//
//    @Override
//    public void onPreWakeup(String s, double v, String s1) {
//        Log.d(TAG, "onPreWakeup");
//    }
//
//    @Override
//    public void onReadyForSpeech() {
//        Log.d(TAG, "onReadyForSpeech: ");
//    }
//
//
//    @Override
//    public void onResultDataReceived(byte[] buffer, int size, int wakeupType) {
//
//    }
//
//    @Override
//    public void onRawDataReceived(byte[] buffer, int size) {
//
//    }
//
//    @Override
//    public void onVprintCutDataReceived(int i, byte[] bytes, int i1) {
//
//    }
//
//    @Override
//    public void onResultDataReceived(byte[] bytes, int i) {
//
//    }
//
//    @Override
//    public void onRawWakeupDataReceived(byte[] bytes, int i) {
//
//    }
//
//
//    @Override
//    public void onOneshot(String words, OneshotCache<byte[]> buffers) {
//        Log.d(TAG, "[onOneshot] >>> oneshot");
//    }
//
//    @Override
//    public void onNotOneshot(String word) {
//
//        Log.d(TAG, "not oneshot" + word);
//    }
//
//}