package com.example.developerslife;

import android.os.Parcel;
import android.os.Parcelable;

class CachedItem implements Parcelable {
    private String mImgUrl;
    private String mDescription;


    public String getDescription() {
        return mDescription;
    }

    public String getImgUrl() {
        return mImgUrl;
    }

    public CachedItem(String pmImgUrl, String pmDescription) {
        mImgUrl = pmImgUrl;
        mDescription = pmDescription;
    }

    protected CachedItem(Parcel in) {
        mImgUrl = in.readString();
        mDescription = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mImgUrl);
        dest.writeString(mDescription);

    }

    public static final Creator<CachedItem> CREATOR = new Creator<CachedItem>() {
        @Override
        public CachedItem createFromParcel(Parcel in) {
            return new CachedItem(in);
        }

        @Override
        public CachedItem[] newArray(int size) {
            return new CachedItem[size];
        }
    };
}
