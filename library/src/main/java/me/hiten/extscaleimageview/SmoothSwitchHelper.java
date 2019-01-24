package me.hiten.extscaleimageview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.graphics.Matrix;
import android.util.Property;
import android.widget.ImageView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 切换滑动辅助类
 */
class SmoothSwitchHelper {

    private ScaleTypeRecord mLastScaleTypeRecord = new ScaleTypeRecord();

    private ExtScaleImageView mExtScaleImageView;
    private ObjectAnimator matrixAnimator;

    private Matrix mTempStartMatrix;
    private Matrix mTempEndMatrix;

    static Method method;

    static {
        try {
            method = ImageView.class.getDeclaredMethod("animateTransform", Matrix.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static Property<ImageView, Matrix> ANIMATED_TRANSFORM_PROPERTY
            = new Property<ImageView, Matrix>(Matrix.class, "animatedTransform") {
        @Override
        public void set(ImageView object, Matrix value) {
            try {
                if (method != null) {
                    method.invoke(object, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Matrix get(ImageView object) {
            return null;
        }
    };

    SmoothSwitchHelper(ExtScaleImageView extScaleImageView) {
        this.mExtScaleImageView = extScaleImageView;
        this.mTempStartMatrix = new Matrix();
        this.mTempEndMatrix = new Matrix();
    }

    /**
     * 是否消费掉setScaleType方法
     * @param scaleType scaleType
     * @return true代表消费false反之
     */
    boolean consume(ImageView.ScaleType scaleType) {

        if (scaleType == null || scaleType == ImageView.ScaleType.MATRIX) {
            return false;
        }

        if (mLastScaleTypeRecord != null && mLastScaleTypeRecord.scaleType == scaleType) {
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
            mExtScaleImageView.mScaleMatrixHelper.calculateMatrix(mTempStartMatrix, mExtScaleImageView.getScaleType());
        }
        mTempEndMatrix.reset();
        mExtScaleImageView.mScaleMatrixHelper.calculateMatrix(mTempEndMatrix, scaleType);

        if (mTempStartMatrix.equals(mTempEndMatrix)) {
            return false;
        }

        mLastScaleTypeRecord.reset();
        mLastScaleTypeRecord.scaleType = scaleType;

        startMatrixAnimator(mTempStartMatrix, mTempEndMatrix);
        return true;
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
            mExtScaleImageView.mScaleMatrixHelper.calculateMatrix(mTempStartMatrix, mExtScaleImageView.getScaleType());
        }
        mTempEndMatrix.reset();
        mExtScaleImageView.mScaleMatrixHelper.calculateMatrix(mTempEndMatrix, extScaleType, cropperPosX, cropperPosY);


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
        matrixAnimator = createMatrixAnimator(mExtScaleImageView, imageMatrix, targetMatrix);
        matrixAnimator.setDuration(400);
        matrixAnimator.start();
        matrixAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mLastScaleTypeRecord.available()) {
                    ImageView.ScaleType scaleType = mLastScaleTypeRecord.scaleType;
                    if (scaleType != null) {
                        mExtScaleImageView.setScaleType(scaleType);
                    } else if (mLastScaleTypeRecord.extScaleType != null) {
                        if (mLastScaleTypeRecord.extScaleType!=ExtScaleImageView.ExtScaleType.ALIGN_POINT_CROP) {
                            mExtScaleImageView.setExtScaleType(mLastScaleTypeRecord.extScaleType);
                        } else {
                            mExtScaleImageView.setExtScaleType(mLastScaleTypeRecord.cropperPosX, mLastScaleTypeRecord.cropperPosY);
                        }
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
        private ImageView.ScaleType scaleType;
        private ExtScaleImageView.ExtScaleType extScaleType;
        private float cropperPosX = -1f;
        private float cropperPosY = -1f;

        void reset() {
            scaleType = null;
            extScaleType = null;
            cropperPosX = -1f;
            cropperPosY = -1f;
        }

        boolean available() {
            return scaleType != null || extScaleType != null && extScaleType.available();
        }

    }


    private ObjectAnimator createMatrixAnimator(final ImageView imageView, Matrix startMatrix,
                                                final Matrix endMatrix) {
        return ObjectAnimator.ofObject(imageView, ANIMATED_TRANSFORM_PROPERTY,
                new MatrixEvaluator(), startMatrix, endMatrix);
    }

    static class MatrixEvaluator implements TypeEvaluator<Matrix> {

        float[] mTempStartValues = new float[9];

        float[] mTempEndValues = new float[9];

        Matrix mTempMatrix = new Matrix();

        @Override
        public Matrix evaluate(float fraction, Matrix startValue, Matrix endValue) {
            startValue.getValues(mTempStartValues);
            endValue.getValues(mTempEndValues);
            for (int i = 0; i < 9; i++) {
                float diff = mTempEndValues[i] - mTempStartValues[i];
                mTempEndValues[i] = mTempStartValues[i] + (fraction * diff);
            }
            mTempMatrix.setValues(mTempEndValues);
            return mTempMatrix;
        }
    }
}
