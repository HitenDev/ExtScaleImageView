package me.hiten.extscaleimageview;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.ImageView;

public class ScaleTypeParcel implements Parcelable {

    private static final String NONE = "NONE";

    public ExtScaleImageView.ExtScaleType extScaleType;
    public ImageView.ScaleType scaleType;
    public float cropperPosX = -1;
    public float cropperPosY = -1;

    public ScaleTypeParcel(ExtScaleImageView extScaleImageView) {
        this.extScaleType = extScaleImageView.getExtScaleType();
        this.scaleType = extScaleImageView.getScaleType();
        PointF extCropperPos = extScaleImageView.getExtCropperPos();
        if (extCropperPos != null) {
            this.cropperPosX = extCropperPos.x;
            this.cropperPosY = extCropperPos.y;
        }
    }

    protected ScaleTypeParcel(Parcel in) {
        String extScaleTypeName = in.readString();
        String scaleTypeName = in.readString();
        this.cropperPosX = in.readFloat();
        this.cropperPosY = in.readFloat();
        if (!TextUtils.isEmpty(extScaleTypeName) && !TextUtils.equals(extScaleTypeName, NONE)) {
            this.extScaleType = ExtScaleImageView.ExtScaleType.valueOf(extScaleTypeName);
        }
        if (!TextUtils.isEmpty(scaleTypeName) && !TextUtils.equals(scaleTypeName, NONE)) {
            this.scaleType = ImageView.ScaleType.valueOf(scaleTypeName);
        }
    }

    public static final Creator<ScaleTypeParcel> CREATOR = new Creator<ScaleTypeParcel>() {
        @Override
        public ScaleTypeParcel createFromParcel(Parcel in) {
            return new ScaleTypeParcel(in);
        }

        @Override
        public ScaleTypeParcel[] newArray(int size) {
            return new ScaleTypeParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(extScaleType != null ? extScaleType.name() : NONE);
        dest.writeString(scaleType != null ? scaleType.name() : NONE);
        dest.writeFloat(cropperPosX);
        dest.writeFloat(cropperPosY);
    }
}
