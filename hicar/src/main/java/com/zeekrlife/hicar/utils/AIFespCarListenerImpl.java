//package com.zeekrlife.hicar.utils;
//
//import android.util.Log;
//
//import com.aispeech.AIError;
//import com.aispeech.common.AIConstant;
//import com.aispeech.export.listeners.AIFespCarListener;
//import com.aispeech.lite.oneshot.OneshotCache;
//
//public class AIFespCarListenerImpl implements AIFespCarListener {
//
//    public static final String TAG = "AIFespCarListenerImpl";
//        @Override
//        public void onInit(int status) {
//            Log.e(TAG, "Init result " + status);
//            if (status == AIConstant.OPT_SUCCESS) {
//            } else {
//            }
//
//        }
//
//        @Override
//        public void onError(AIError error) {
//            Log.e(TAG,"错误:" + error.getError());
//        }
//
//        @Override
//        public void onWakeup(String recordId, double confidence, String wakeupWord) {
//            Log.e(TAG, "唤醒成功 confidence=" + confidence + " wakeupWord = " + wakeupWord);
//        }
//
//        @Override
//        public void onDoaResult(int doa) {
//            Log.e(TAG, "唤醒的角度:" + doa);
//            String locate = "";
//            switch (doa) {
//                case 1:
//                    locate = "主驾";
//                    break;
//                case 2:
//                    locate = "副驾";
//                    break;
//                case 3:
//                    locate = "左后";
//                    break;
//                case 4:
//                    locate = "右后";
//                    break;
//            }
//        }
//
//        @Override
//        public void onDoaResult(int doa, int type) {
//            //主动获取doa
//            Log.e(TAG, "doa = " + doa + " >>> type = " + type);
//            if (type == AIConstant.DOA.TYPE_QUERY) {
//
//            } else if (type == AIConstant.DOA.TYPE_WAKEUP) {
//                //唤醒doa
//            }
//        }
//
//        @Override
//        public void onReadyForSpeech() {
//            Log.e(TAG, "onReadyForSpeech");
//        }
//
//        @Override
//        public void onRawDataReceived(byte[] buffer, int size) {
//
//        }
//
//        @Override
//        public void onVprintCutDataReceived(int dataType, byte[] data, int i1) {
//            if (dataType == AIConstant.AIENGINE_MESSAGE_TYPE_JSON) {
//                String wakeupStr = new String(data);
//                Log.e(TAG, "vprint cut info: " + wakeupStr);
//            }
//        }
//
//        @Override
//        public void onOneshot(String word, OneshotCache<byte[]> buffer) {
//            Log.e(TAG, "onOneshot word = " + word);
//        }
//
//        @Override
//        public void onNotOneshot(String word) {
//            Log.e(TAG, "onNotOneshot word = " + word);
//        }
//
//        @Override
//        public void onResultDataReceived(byte[] dataVad, int size, int wakeupType) {
////            Log.i(TAG, "size " + size + " wakeupType " + wakeupType);
////            mCloudASREngine.feedData(dataVad, size);
//        }
//
//        @Override
//        public void onResultDataReceived(byte[] vad, byte[] asr) {
////            Log.i(TAG, "onResultDataReceived " + asr.length + " vad " + vad.length);
//            // 根据驾驶模式抛出的音频 全车则是抛出的混音
//        }
//
//        @Override
//        public void onResultDataReceived(byte[] buffer, boolean isUseDoubleVad) {
////            Log.i(TAG,"onResultDataReceived,isUseDoubleVad:" + isUseDoubleVad);
//        }
//    }