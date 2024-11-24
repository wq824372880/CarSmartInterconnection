package com.zeekrlife.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class IHiCarAppInfo implements Parcelable {
    public String mPackageName;
    public String mName;
    public byte[] mIcon;
    public int mType;

    public IHiCarAppInfo() {

    }

    protected IHiCarAppInfo(Parcel in) {
        mPackageName = in.readString();
        mName = in.readString();
        mIcon = in.createByteArray();
        mType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPackageName);
        dest.writeString(mName);
        dest.writeByteArray(mIcon);
        dest.writeInt(mType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IHiCarAppInfo> CREATOR = new Creator<IHiCarAppInfo>() {
        @Override
        public IHiCarAppInfo createFromParcel(Parcel in) {
            return new IHiCarAppInfo(in);
        }

        @Override
        public IHiCarAppInfo[] newArray(int size) {
            return new IHiCarAppInfo[size];
        }
    };
}
