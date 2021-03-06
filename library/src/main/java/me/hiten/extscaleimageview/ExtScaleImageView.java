package me.hiten.extscaleimageview;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * 扩展ScaleType的ImageView
 */
public class ExtScaleImageView extends android.support.v7.widget.AppCompatImageView {

    /**
     * 扩展ScaleType
     */
    private ExtScaleType mExtScaleType;

    /**
     * 平滑切换style
     */
    private boolean mSmoothSwitch = false;

    /**
     * 是否开启获取原始ScaleType
     */
    private boolean mGetScaleType = false;

    /**
     * Point控制点
     */
    private PointF mCropperPos = new PointF(-1f, -1f);

    ScaleMatrixHelper mScaleMatrixHelper;

    private SmoothSwitchHelper mSmoothSwitchHelper;

    public ExtScaleImageView(Context context) {
        this(context, null);
    }

    public ExtScaleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtScaleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleMatrixHelper = new ScaleMatrixHelper(this);
        super.setScaleType(ScaleType.MATRIX);
    }

    /**
     * 开始/关闭切换动画效果
     * @param smoothSwitch true/false
     */
    public void setSmoothSwitch(boolean smoothSwitch) {
        this.mSmoothSwitch = smoothSwitch;
    }

    public boolean isSmoothSwitch() {
        return mSmoothSwitch;
    }

    public void ensureSmoothSwitchHelper() {
        if (mSmoothSwitchHelper == null) {
            mSmoothSwitchHelper = new SmoothSwitchHelper(this);
        }
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType == null || ScaleType.MATRIX == scaleType) {
            return;
        }
        //将ScaleType转成ExtScaleType
        setExtScaleType(ExtScaleType.valueOf(scaleType.name()));
    }

    @Override
    public ScaleType getScaleType() {
        if (mGetScaleType && mExtScaleType != null) {
            if (mExtScaleType.scaleType != null) {
                return mExtScaleType.scaleType;
            }
            if (mExtScaleType.cropperPosX == 0.5 && mExtScaleType.cropperPosY == 0.5) {
                return ScaleType.CENTER_CROP;
            }
        }

        return super.getScaleType();
    }

    public void setExtScaleType(ExtScaleType extScaleType) {
        if (extScaleType == null) {
            this.mExtScaleType = null;
            return;
        }
        if (mSmoothSwitch) {
            ensureSmoothSwitchHelper();
            if (mSmoothSwitchHelper.consume(extScaleType, extScaleType.cropperPosX, extScaleType.cropperPosY)) {
                return;
            }
        }
        this.mExtScaleType = extScaleType;
        if (!ScaleType.MATRIX.equals(getScaleType())) {
            super.setScaleType(ScaleType.MATRIX);
        }
        updateMatrix();
    }

    public ExtScaleType getExtScaleType() {
        return mExtScaleType;
    }

    public PointF getExtCropperPos() {
        return mCropperPos;
    }

    public void setExtScaleType(@FloatRange(from = 0f, to = 1.0f) float cropperPosX, @FloatRange(from = 0f, to = 1.0f) float cropperPosY) {
        if (mSmoothSwitch) {
            if (mSmoothSwitchHelper.consume(ExtScaleType.ALIGN_POINT_CROP, cropperPosX, cropperPosY)) {
                return;
            }
        }
        this.mExtScaleType = ExtScaleType.ALIGN_POINT_CROP;
        this.mCropperPos.x = Math.min(1.0f, Math.max(cropperPosX, 0f));
        this.mCropperPos.y = Math.min(1.0f, Math.max(cropperPosY, 0f));
        if (!ScaleType.MATRIX.equals(getScaleType())) {
            super.setScaleType(ScaleType.MATRIX);
        }
        updateMatrix();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateMatrix();
    }

    private void updateMatrix() {
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        if (getDrawable() == null) {
            return;
        }
        if (mScaleMatrixHelper != null) {
            mScaleMatrixHelper.updateMatrix();
        }
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        if (mSmoothSwitchHelper != null) {
            mSmoothSwitchHelper.clean();
        }
        super.setImageDrawable(drawable);
        updateMatrix();
    }

    private static final String SAVE_KEY_SUPER_STATE = "superState";
    private static final String SAVE_KEY_EXT_SCALE_TYPE_NAME = "extScaleTypeName";
    private static final String SAVE_KEY_CROPPER_POS_X = "cropperPosX";
    private static final String SAVE_KEY_CROPPER_POS_Y = "cropperPosY";

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable(SAVE_KEY_SUPER_STATE, parcelable);
        if (mExtScaleType != null) {
            bundle.putString(SAVE_KEY_EXT_SCALE_TYPE_NAME, mExtScaleType.name());
        } else {
            bundle.putString(SAVE_KEY_EXT_SCALE_TYPE_NAME, "NULL");
        }
        if (mCropperPos != null) {
            bundle.putFloat(SAVE_KEY_CROPPER_POS_X, mCropperPos.x);
            bundle.putFloat(SAVE_KEY_CROPPER_POS_Y, mCropperPos.y);
        } else {
            bundle.putFloat(SAVE_KEY_CROPPER_POS_X, -1);
            bundle.putFloat(SAVE_KEY_CROPPER_POS_Y, -1);
        }
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            String extScaleTypeName = bundle.getString(SAVE_KEY_EXT_SCALE_TYPE_NAME, null);
            if (!TextUtils.isEmpty(extScaleTypeName)) {
                mExtScaleType = ExtScaleType.valueOf(extScaleTypeName);
            }
            float cropperPosX = bundle.getFloat(SAVE_KEY_CROPPER_POS_X, -1f);
            float cropperPosY = bundle.getFloat(SAVE_KEY_CROPPER_POS_Y, -1f);
            mCropperPos = new PointF(cropperPosX, cropperPosY);
            state = bundle.getParcelable(SAVE_KEY_SUPER_STATE);
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mSmoothSwitchHelper != null) {
            mSmoothSwitchHelper.clean();
        }
    }

    /**
     * 扩展ScaleType
     */
    public enum ExtScaleType {

        /**
         * 根据输入的点进行对齐和剪裁，取值范围[0.0-1.0]
         */
        ALIGN_POINT_CROP(-1.0f, -1.0f),

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
        ALIGN_RIGHT_CROP(1.0f, 0.5f),

        /**
         * 根据宽确定缩放比，缩放后的高度如果小于等于控件高度，居中显示，否则顶部对齐剪裁
         */
        FIT_WIDTH_CENTER_TOP_HEIGHT(-2.0f, -2.0f),

        /*对Android默认的ScaleType进行包装*/
        FIT_XY(ScaleType.FIT_XY),
        FIT_START(ScaleType.FIT_START),
        FIT_CENTER(ScaleType.FIT_CENTER),
        FIT_END(ScaleType.FIT_END),
        CENTER(ScaleType.CENTER),
        CENTER_CROP(ScaleType.CENTER_CROP),
        CENTER_INSIDE(ScaleType.CENTER_INSIDE);

        ExtScaleType(float cropperPosX, float cropperPosY) {
            this.cropperPosX = cropperPosX;
            this.cropperPosY = cropperPosY;
        }

        boolean available() {
            return !this.equals(ALIGN_POINT_CROP) || (cropperPosX >= 0 && cropperPosY >= 0);
        }


        ExtScaleType(ScaleType scaleType) {
            this.scaleType = scaleType;
        }

        ScaleType scaleType;
        float cropperPosX;
        float cropperPosY;

    }
}
