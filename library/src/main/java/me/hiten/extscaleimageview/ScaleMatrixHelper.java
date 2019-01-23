package me.hiten.extscaleimageview;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * 矩阵计算辅助类
 */
public class ScaleMatrixHelper {


    private ExtScaleImageView mExtScaleImageView;

    public ScaleMatrixHelper(ExtScaleImageView extScaleImageView) {
        mExtScaleImageView = extScaleImageView;
    }

    public void updateMatrix() {
        ExtScaleImageView.ExtScaleType extScaleType = mExtScaleImageView.getExtScaleType();
        if (extScaleType==null){
            return;
        }
        float cropperPosX = extScaleType.cropperPosX;
        float cropperPosY = extScaleType.cropperPosX;
        if (ExtScaleImageView.ExtScaleType.ALIGN_POINT_CROP.equals(extScaleType)){
            PointF extCropperPos = mExtScaleImageView.getExtCropperPos();
            cropperPosX = extCropperPos.x;
            cropperPosY = extCropperPos.y;
        }
        calculateMatrix(ExtScaleImageView.ExtScaleType.ALIGN_POINT_CROP,cropperPosX,cropperPosY);

    }

    private void calculateMatrix(ExtScaleImageView.ExtScaleType extScaleType, float cropperPosX, float cropperPosY) {
        final int vWidth = mExtScaleImageView.getWidth() - mExtScaleImageView.getPaddingLeft() - mExtScaleImageView.getPaddingLeft();
        final int vHeight = mExtScaleImageView.getHeight() - mExtScaleImageView.getPaddingTop() - mExtScaleImageView.getPaddingBottom();
        if (vWidth<=0||vHeight<=0){
            return;
        }
        Drawable drawable = mExtScaleImageView.getDrawable();
        if (drawable==null){
            return;
        }
        int dWidth = drawable.getIntrinsicWidth();
        int dHeight = drawable.getIntrinsicHeight();
        if (dWidth <= 0 || dHeight <= 0) {
            return;
        }
        if (ExtScaleImageView.ExtScaleType.ALIGN_POINT_CROP.equals(extScaleType)) {
            float scale;
            float dx = 0, dy = 0;

            if (dWidth * vHeight > vWidth * dHeight) {
                scale = (float) vHeight / (float) dHeight;
                dx = (vWidth - dWidth * scale) * cropperPosX;
            } else {
                scale = (float) vWidth / (float) dWidth;
                dy = (vHeight - dHeight * scale) * cropperPosY;
            }

            Matrix imageMatrix = new Matrix();
            imageMatrix.setScale(scale, scale);
            imageMatrix.postTranslate(Math.round(dx), Math.round(dy));
            if (mExtScaleImageView.getScaleType() != ImageView.ScaleType.MATRIX) {
                mExtScaleImageView.setScaleType(ImageView.ScaleType.MATRIX);
            }
            mExtScaleImageView.setImageMatrix(imageMatrix);
        }
    }

}
