package com.zeekrlife.hicar.app.audio;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author hehr
 * 录音机
 */
public class Recorder implements IRecorder {

    public static final String TAG = "Recorder";

    public static final int MICTYPE_1MIC_1CH = 0;
    public static final int MICTYPE_2MIC_2CH = 1;
    public static final int MICTYPE_2MIC_4CH = 2;
    public static final int MICTYPE_4MIC = 3;
    public static final int MICTYPE_4MIC_2CH = 4;


    /**
     * Android 标准录音机api
     */
    private volatile AudioRecord mAudioRecorder;
    /**
     *
     */
    private RecorderListener mListener;

    private ExecutorService mThreadPool = Executors.newSingleThreadExecutor();

    public boolean isRecording() {
        return isRecording;
    }

    private volatile boolean isRecording = false;

    /**
     * 录音机采样间隔,ms
     */
    private int intervalTime = 32;

    private int audioSource = MediaRecorder.AudioSource.VOICE_RECOGNITION;

    private int sampleRate = 16000;

    private int channel = 1;

    private int format = AudioFormat.ENCODING_PCM_16BIT;

    private int micType = 1;

    @SuppressLint("MissingPermission")
    @Override
    public void create(int audioSource, int sampleRate, int channel, int format, int bufferSizeInBytes) {
        this.audioSource = audioSource;
        this.sampleRate = sampleRate;
        this.channel = channel;
        this.format = format;
        mAudioRecorder = new AudioRecord(audioSource, sampleRate, channel, format, bufferSizeInBytes);
        Log.e(TAG, "---mAudioRecorder: " + mAudioRecorder);
    }

    @Override
    public void create(int type) {
        Log.i(TAG, "create: "+type);
        micType = type;
        channel = AudioFormat.CHANNEL_IN_STEREO;// 立体声,2通道数据
        if (type == 1) {// 1mic
            channel = AudioFormat.CHANNEL_IN_MONO;
            create(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                    16000,
                    channel,
                    AudioFormat.ENCODING_PCM_16BIT,
                    calculateReadBufferSize());
        } else if (type == 2) {//2mic
            create(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                    16000,
                    AudioFormat.CHANNEL_IN_STEREO,// 立体声,2通道数据
                    AudioFormat.ENCODING_PCM_16BIT,
                    calculateReadBufferSize());
        } else if (type == 0) {
            //1mic 1ch
            create(MediaRecorder.AudioSource.MIC,
                    16000,
                    AudioFormat.CHANNEL_IN_MONO, // 单通道
                    AudioFormat.ENCODING_PCM_16BIT,
                    AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT));
        } else if (type == MICTYPE_4MIC_2CH) {
            // 4mic 2ch
            channel = 252; // 需根据项目车机配置
            create(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                    16000,
                    channel,
                    AudioFormat.ENCODING_PCM_16BIT,
                    calculateReadBufferSize());

        } else {//4mic
            create(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                    32000,
                    AudioFormat.CHANNEL_IN_MONO,// 需要和客户确认如何获取当前4+2通道的
                    AudioFormat.ENCODING_PCM_16BIT,
                    calculateReadBufferSize());
        }

        if (mAudioRecorder == null) {
            Log.i(TAG, "create mAudioRecorder  fail ");
        } else {
            Log.i(TAG, "create audio status : "+mAudioRecorder.getState());
        }
    }

    private Lock mLock = new ReentrantLock();

    @Override
    public void start(RecorderListener listener) {

        mLock.lock();

        try {
            isRecording = true;
            mListener = listener;
            if (mListener != null) {
                mListener.onRecordStarted();
            }
            mThreadPool.execute(new ReadRunnable());
            if (mAudioRecorder != null) {
                mAudioRecorder.startRecording();
            }
        } finally {
            mLock.unlock();
        }

    }

    @Override
    public void stop() {

        mLock.lock();

        try {
            if (mListener != null) {
                mListener.onRecordStopped();
            }
            isRecording = false;
            if (mAudioRecorder != null) {
                mAudioRecorder.stop();
            }
        } finally {
            mLock.unlock();
        }

    }

    @Override
    public void release() {
        if (mListener != null) {
            mListener.onRecordReleased();
        }
    }

    private class ReadRunnable implements Runnable {

        @Override
        public void run() {
            if (mAudioRecorder == null) {
                create(micType);
            }

//            int size = calculateReadBufferSize();
            int size = 6144;
            Log.d("Recorder", "read buffer size = " + size);
            byte[] buffer = new byte[size];
            while (true) {
                if (!isRecording)
                    break;
                int readSize = mAudioRecorder.read(buffer, 0, size);
//                Log.d("Recorder", "readSize = " + size);
                if (readSize > 0) {
                    byte[] bytes = new byte[readSize];
                    System.arraycopy(buffer, 0, bytes, 0, readSize);
                    if (mListener != null) {
                        mListener.onDataReceived(bytes, readSize);
                    }
                }
            }

        }
    }

    /**
     * 计算读取音频buffer大小
     *
     * @return 推荐读取大小
     */
    private int calculateReadBufferSize() {
        int channelNumber = 1;
        switch (channel) {
            case AudioFormat.CHANNEL_IN_MONO:
                channelNumber = 1;
                break;
            case AudioFormat.CHANNEL_IN_STEREO:
                channelNumber = 2;
                break;
            case 4://双麦2+2
            case 204://比亚迪双麦2+2采集
                channelNumber = 4;
                break;
            case 6://四麦4+2
            case 252://比亚迪双麦4+2采集通道
                channelNumber = 6;
                break;
        }
        return sampleRate * channelNumber * format * intervalTime / 1000;
    }

}
