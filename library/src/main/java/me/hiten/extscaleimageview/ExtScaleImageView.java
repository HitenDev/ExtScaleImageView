package me.hiten.extscaleimageview;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * 扩展ScaleType的ImageView
 */
public class ExtScaleImageView extends android.support.v7.widget.AppCompatImageView {


    private ExtScaleType mExtScaleType;

    private PointF mCropperPos = new PointF(-1f, -1f);

    private ScaleMatrixHelper mScaleMatrixHelper;

    public ExtScaleImageView(Context context) {
        this(context, null);
    }

    public ExtScaleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtScaleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleMatrixHelper = new ScaleMatrixHelper(this);
    }


    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != null && !ScaleType.MATRIX.equals(scaleType)) {
            setExtScaleType(null);
        }
        super.setScaleType(scaleType);
    }



    public void setExtScaleType(ExtScaleType extScaleType) {
        this.mExtScaleType = extScaleType;
        if (extScaleType == null) {
            return;
        }
        setScaleType(ScaleType.MATRIX);
        updateMatrix();
    }

    public ExtScaleType getExtScaleType() {
        return mExtScaleType;
    }

    public PointF getExtCropperPos() {
        return mCropperPos;
    }

    public void setExtScaleType(@FloatRange(from = 0f, to = 1.0f) float cropperPosX, @FloatRange(from = 0f, to = 1.0f) float cropperPosY) {
        this.mExtScaleType = ExtScaleType.ALIGN_POINT_CROP;
        this.mCropperPos.x = Math.min(1.0f, Math.max(cropperPosX, 0f));
        this.mCropperPos.y = Math.min(1.0f, Math.max(cropperPosY, 0f));
        setScaleType(ScaleType.MATRIX);
        updateMatrix();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateMatrix();
    }

    private void updateMatrix(){
        if (getWidth()<=0||getHeight()<=0){
            return;
        }
        if (getDrawable()==null){
            return;
        }
        if (mScaleMatrixHelper!=null){
            mScaleMatrixHelper.updateMatrix();
        }
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        updateMatrix();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", parcelable);
        if (mExtScaleType != null) {
            bundle.putFloat("extScaleTypeX", mExtScaleType.cropperPosX);
            bundle.putFloat("extScaleTypeY", mExtScaleType.cropperPosY);
        }
        if (mCropperPos != null) {
            bundle.putFloat("cropperPosX", mCropperPos.x);
            bundle.putFloat("cropperPosY", mCropperPos.y);
        }
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            float extScaleTypeX = bundle.getFloat("extScaleTypeX", -99f);
            float extScaleTypeY = bundle.getFloat("extScaleTypeY", -99f);
            mExtScaleType = ExtScaleType.valueOf(extScaleTypeX, extScaleTypeY);

            float cropperPosX = bundle.getFloat("cropperPosX", -1f);
            float cropperPosY = bundle.getFloat("cropperPosY", -1f);
            mCropperPos = new PointF(cropperPosX,cropperPosY);
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

    /**
     * 扩展ScaleType
     */
    public enum ExtScaleType {

        /**
         * 根据输入的点进行对齐和剪裁，取值范围[0.0-1.0]
         */
        ALIGN_POINT_CROP(-1f, -1f),

        /**
         * 顶部对齐，左右两边缩放并填充宽，底部剪裁
         */
        ALIGN_TOP_CROP(0.5f, 0f),

        /**
         * 底部对齐，左右两边缩放并填充宽，顶部剪裁
         */
        ALIGN_BOTTOM_CROP(0.5f, 1f),

        /**
         * 左边对齐，上下两边缩放并填充高，右边剪裁
         */
        ALIGN_LEFT_CROP(0f, 0.5f),

        /**
         * 右边对齐，上下两边缩放并填充高，左边剪裁
         */
        ALIGN_RIGHT_CROP(1f, 0.5f);

        ExtScaleType(float cropperPosX, float cropperPosY) {
            this.cropperPosX = cropperPosX;
            this.cropperPosY = cropperPosY;
        }

        /**
         * 通过参数得到枚举类型
         * @param cropperPosX x crop param
         * @param cropperPosY y crop param
         * @return 枚举
         */
        static ExtScaleType valueOf(float cropperPosX, float cropperPosY) {
            ExtScaleType[] extScaleTypes = ExtScaleType.values();
            for (ExtScaleType extScaleType : extScaleTypes) {
                if (extScaleType.valueEquals(cropperPosX, cropperPosY)) {
                    return extScaleType;
                }
            }
            return null;
        }

        boolean valueEquals(float cropperPosX, float cropperPosY) {
            return this.cropperPosX == cropperPosX && this.cropperPosY == cropperPosY;
        }


        float cropperPosX;
        float cropperPosY;

    }
}
