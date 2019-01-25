package me.hiten.extscaleimageview;

import android.graphics.Matrix;

/**
 * 矩阵计算辅助类
 */
class ScaleMatrixHelper {

    private ExtScaleImageView mExtScaleImageView;

    private Matrix mTempMatrix;

    ScaleMatrixHelper(ExtScaleImageView extScaleImageView) {
        mExtScaleImageView = extScaleImageView;
    }

    void updateMatrix() {
        ExtScaleImageView.ExtScaleType extScaleType = mExtScaleImageView.getExtScaleType();
        if (extScaleType == null) {
            return;
        }
        calculateMatrix();
    }


    private void calculateMatrix() {
        if (mTempMatrix==null){
            mTempMatrix = new Matrix();
        }
        mTempMatrix.reset();
        ScaleMatrixUtils.calculateMatrix(mExtScaleImageView, mTempMatrix, mExtScaleImageView.getExtScaleType(), mExtScaleImageView.getExtCropperPos().x, mExtScaleImageView.getExtCropperPos().y);
        mExtScaleImageView.setImageMatrix(mTempMatrix);
    }

}
