package me.hiten.extscaleimageview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Matrix;
/**
 * 切换滑动辅助类
 */
class SmoothSwitchHelper {


    private static final int DURATION = 400;

    private ScaleTypeRecord mLastScaleTypeRecord = new ScaleTypeRecord();

    private ExtScaleImageView mExtScaleImageView;

    private ObjectAnimator matrixAnimator;

    private Matrix mTempStartMatrix;
    private Matrix mTempEndMatrix;

    SmoothSwitchHelper(ExtScaleImageView extScaleImageView) {
        this.mExtScaleImageView = extScaleImageView;
        this.mTempStartMatrix = new Matrix();
        this.mTempEndMatrix = new Matrix();
    }


    /**
     * 是否消费掉setExtScaleType方法
     * @param extScaleType extScaleType
     * @param cropperPosX cropperPosX
     * @param cropperPosY cropperPosY
     * @return true代表消费false反之
     */
    boolean consume(ExtScaleImageView.ExtScaleType extScaleType, float cropperPosX, float cropperPosY) {
        if (extScaleType == null) {
            return false;
        }
        if (mLastScaleTypeRecord != null && extScaleType == mLastScaleTypeRecord.extScaleType && mLastScaleTypeRecord.cropperPosX == cropperPosX && mLastScaleTypeRecord.cropperPosY == cropperPosY) {
            return false;
        }
        if (matrixAnimator != null && matrixAnimator.isRunning()) {
            return true;
        }
        if (!Utils.drawableHasSize(mExtScaleImageView.getDrawable())){
            return false;
        }

        if (!Utils.viewHasSize(mExtScaleImageView)){
            return false;
        }

        mTempStartMatrix.set(mExtScaleImageView.getImageMatrix());
        if (mTempStartMatrix.isIdentity()) {
            ScaleMatrixUtils.calculateMatrix(mExtScaleImageView, mTempStartMatrix, mExtScaleImageView.getScaleType());
        }
        mTempEndMatrix.reset();
        ScaleMatrixUtils.calculateMatrix(mExtScaleImageView, mTempEndMatrix, extScaleType, cropperPosX, cropperPosY);


        if (mTempStartMatrix.equals(mTempEndMatrix)) {
            return false;
        }

        mLastScaleTypeRecord.reset();
        mLastScaleTypeRecord.extScaleType = extScaleType;
        mLastScaleTypeRecord.cropperPosX = cropperPosX;
        mLastScaleTypeRecord.cropperPosY = cropperPosY;

        startMatrixAnimator(mTempStartMatrix, mTempEndMatrix);

        return true;
    }

    /**
     * 执行动画
     * @param imageMatrix 起始矩阵
     * @param targetMatrix 结束矩阵
     */
    private void startMatrixAnimator(Matrix imageMatrix, Matrix targetMatrix) {
        matrixAnimator = ScaleMatrixUtils.createMatrixAnimator(mExtScaleImageView, imageMatrix, targetMatrix);
        matrixAnimator.setDuration(DURATION);
        matrixAnimator.start();
        matrixAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mLastScaleTypeRecord != null && mLastScaleTypeRecord.available()) {
                    if (mLastScaleTypeRecord.extScaleType != ExtScaleImageView.ExtScaleType.ALIGN_POINT_CROP) {
                        mExtScaleImageView.setExtScaleType(mLastScaleTypeRecord.extScaleType);
                    } else {
                        mExtScaleImageView.setExtScaleType(mLastScaleTypeRecord.cropperPosX, mLastScaleTypeRecord.cropperPosY);
                    }
                }
            }
        });
    }

    /**
     * 清除动画和记录
     */
    void clean() {
        if (matrixAnimator != null) {
            matrixAnimator.removeAllListeners();
            if (matrixAnimator.isRunning()) {
                matrixAnimator.cancel();
            }
        }
        if (mLastScaleTypeRecord != null) {
            mLastScaleTypeRecord.reset();
        }
    }


    private class ScaleTypeRecord {
        private ExtScaleImageView.ExtScaleType extScaleType;
        private float cropperPosX = -1f;
        private float cropperPosY = -1f;

        void reset() {
            extScaleType = null;
            cropperPosX = -1f;
            cropperPosY = -1f;
        }

        boolean available() {
            return extScaleType != null && extScaleType.available();
        }

    }

}
