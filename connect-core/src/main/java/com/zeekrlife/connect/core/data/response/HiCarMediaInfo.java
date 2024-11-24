package com.zeekrlife.connect.core.data.response;

import java.util.Arrays;

public class HiCarMediaInfo {

    public MediaData MediaData;
    public class MediaData{

        String name;
        String Artist;
        long TotalTime;
        long ElapsedTime;
        String AlbumName;
        String AlbumArtURL;
        long Status;
        byte[]AppIcon;
        String AppName;
        String AppPackageName;
        byte[] AlbumArtIcon;
        boolean IsAudioFromCar;

        @Override
        public String toString() {
            return "MediaData{" +
                    "name='" + name + '\'' +
                    ", Artist='" + Artist + '\'' +
                    ", TotalTime=" + TotalTime +
                    ", ElapsedTime=" + ElapsedTime +
                    ", AlbumName='" + AlbumName + '\'' +
                    ", AlbumArtURL='" + AlbumArtURL + '\'' +
                    ", Status=" + Status +
                    ", AppName='" + AppName + '\'' +
                    ", AppPackageName='" + AppPackageName + '\'' +
                    ", IsAudioFromCar=" + IsAudioFromCar +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "HiCarMediaInfo{" +
                "MediaData=" + MediaData +
                '}';
    }
}
