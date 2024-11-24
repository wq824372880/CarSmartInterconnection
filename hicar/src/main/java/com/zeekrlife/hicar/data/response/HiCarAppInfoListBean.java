package com.zeekrlife.hicar.data.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.zeekrlife.aidl.IHiCarAppInfo;

import java.util.List;

public class HiCarAppInfoListBean implements Parcelable {
    public List<IHiCarAppInfo> infoList;

    protected HiCarAppInfoListBean(Parcel in) {
        infoList = in.createTypedArrayList(IHiCarAppInfo.CREATOR);
    }

    public HiCarAppInfoListBean() {

    }

    public void setData(List<IHiCarAppInfo> infoList){
        this.infoList = infoList;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(infoList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HiCarAppInfoListBean> CREATOR = new Creator<HiCarAppInfoListBean>() {
        @Override
        public HiCarAppInfoListBean createFromParcel(Parcel in) {
            return new HiCarAppInfoListBean(in);
        }

        @Override
        public HiCarAppInfoListBean[] newArray(int size) {
            return new HiCarAppInfoListBean[size];
        }
    };
}
