package com.kimhakjin.timecheck;

import android.os.Parcel;
import android.os.Parcelable;

public class Info implements Parcelable {

    private String startTime;
    private String endTime;
    private String doTime;
    private String memo;

    public Info(String startTime, String endTime, String doTime, String memo){
        this.startTime = startTime;
        this.endTime = endTime;
        this.doTime = doTime;
        this.memo = memo;
    }

    protected Info(Parcel in) {
        startTime = in.readString();
        endTime = in.readString();
        doTime = in.readString();
        memo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(doTime);
        dest.writeString(memo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Info> CREATOR = new Creator<Info>() {
        @Override
        public Info createFromParcel(Parcel in) {
            return new Info(in);
        }

        @Override
        public Info[] newArray(int size) {
            return new Info[size];
        }
    };

    public String getStartTime(){
        return startTime;
    }

    public String getEndTime(){
        return endTime;
    }

    public String getDoTime(){
        return doTime;
    }

    public String getMemo(){
        return memo;
    }

    public void setStartTime(String startTime){
        this.startTime = startTime;
    }

    public void setEndTime(String endTime){
        this.endTime = endTime;
    }

    public void setDoTime(String doTime){
        this.doTime = doTime;
    }

    public void setMemo(String memo){
        this.memo = memo;
    }
}
