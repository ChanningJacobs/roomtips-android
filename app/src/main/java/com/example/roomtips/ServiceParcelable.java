package com.example.roomtips;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class ServiceParcelable implements Parcelable {
    private CustomServiceObject services;

    public ServiceParcelable(CustomServiceObject s) {
        services = s;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(new Gson().toJson(services));
    }

    public static final Parcelable.Creator<ServiceParcelable> CREATOR = new Parcelable.Creator<ServiceParcelable>() {
        public ServiceParcelable createFromParcel(Parcel in) {
            return new ServiceParcelable(in);
        }

        public ServiceParcelable[] newArray(int size) {
            return new ServiceParcelable[size];
        }
    };
    private ServiceParcelable(Parcel in) {
        this.services = new Gson().fromJson(in.readString(), CustomServiceObject.class);
    }
}
