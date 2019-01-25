package me.hiten.extscaleimageview.se;

import android.os.Parcel;
import android.os.Parcelable;

public class SharedElementSnapshot implements Parcelable {

    public Parcelable snapshot;
    public Parcelable extra;

    public SharedElementSnapshot(Parcelable snapshot, Parcelable extra) {
        this.snapshot = snapshot;
        this.extra = extra;
    }

    protected SharedElementSnapshot(Parcel in) {
        in.readParcelable(Parcelable.class.getClassLoader());
        in.readParcelable(getClass().getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(snapshot, flags);
        dest.writeParcelable(extra, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SharedElementSnapshot> CREATOR = new Creator<SharedElementSnapshot>() {
        @Override
        public SharedElementSnapshot createFromParcel(Parcel in) {
            return new SharedElementSnapshot(in);
        }

        @Override
        public SharedElementSnapshot[] newArray(int size) {
            return new SharedElementSnapshot[size];
        }
    };
}
