package me.hiten.extscaleimageview;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.widget.ImageView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ScaleMatrixUtils {

    public static void calculateMatrix(ExtScaleImageView extScaleImageView, Matrix inMatrix, ExtScaleImageView.ExtScaleType extScaleType, float cropperPosX, float cropperPosY) {
        if (inMatrix == null) {
            return;
        }
        if (extScaleType == null) {
            return;
        }

        if (!Utils.viewHasSize(extScaleImageView)) {
            return;
        }

        Drawable drawable = extScaleImageView.getDrawable();
        if (!Utils.drawableHasSize(drawable)) {
            return;
        }

        if (extScaleType.scaleType != null) {
            calculateMatrix(extScaleImageView, inMatrix, extScaleType.scaleType);
            return;
        }

        if (!(ExtScaleImageView.ExtScaleType.ALIGN_POINT_CROP == extScaleType) && !(ExtScaleImageView.ExtScaleType.FIT_WIDTH_CENTER_TOP_HEIGHT == extScaleType)) {
            cropperPosX = extScaleType.cropperPosX;
            cropperPosY = extScaleType.cropperPosY;
            extScaleType = ExtScaleImageView.ExtScaleType.ALIGN_POINT_CROP;
        }


        final int vWidth = Utils.getWidht(extScaleImageView);
        final int vHeight = Utils.getHeight(extScaleImageView);

        int dWidth = drawable.getIntrinsicWidth();
        int dHeight = drawable.getIntrinsicHeight();
        inMatrix.reset();
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

            inMatrix.setScale(scale, scale);
            inMatrix.postTranslate(Math.round(dx), Math.round(dy));
        } else if (ExtScaleImageView.ExtScaleType.FIT_WIDTH_CENTER_TOP_HEIGHT == extScaleType) {
            float scale;
            scale = (float) vWidth / (float) dWidth;
            inMatrix.setScale(scale, scale);
            int ddHeight = (int) (dHeight * scale + 0.5f);
            if (ddHeight < vHeight) {
                inMatrix.postTranslate(0,
                        (vHeight - dHeight * scale) / 2f + 0.5f);
            }
        }
    }

    public static void calculateMatrix(ExtScaleImageView extScaleImageView, Matrix inMatrix, ImageView.ScaleType scaleType) {
        Drawable drawable = extScaleImageView.getDrawable();
        if (!Utils.drawableHasSize(drawable)) {
            return;
        }
        if (!Utils.viewHasSize(extScaleImageView)) {
            return;
        }

        final float viewWidth = Utils.getWidht(extScaleImageView);
        final float viewHeight = Utils.getHeight(extScaleImageView);
        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        inMatrix.reset();
        final float widthScale = viewWidth / drawableWidth;
        final float heightScale = viewHeight / drawableHeight;
        if (scaleType == ImageView.ScaleType.CENTER) {
            inMatrix.postTranslate((viewWidth - drawableWidth) / 2F,
                    (viewHeight - drawableHeight) / 2F);

        } else if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            float scale = Math.max(widthScale, heightScale);
            inMatrix.postScale(scale, scale);
            inMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else if (scaleType == ImageView.ScaleType.CENTER_INSIDE) {
            float scale = Math.min(1.0f, Math.min(widthScale, heightScale));
            inMatrix.postScale(scale, scale);
            inMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2F,
                    (viewHeight - drawableHeight * scale) / 2F);

        } else {
            RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);
            RectF mTempDst = new RectF(0, 0, viewWidth, viewHeight);

            switch (scaleType) {
                case FIT_CENTER:
                    inMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.CENTER);
                    break;
                case FIT_START:
                    inMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.START);
                    break;
                case FIT_END:
                    inMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.END);
                    break;
                case FIT_XY:
                    inMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.FILL);
                    break;
                default:
                    break;
            }
        }
    }


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

    public static ObjectAnimator createMatrixAnimator(final ImageView imageView, Matrix startMatrix,
                                                      final Matrix endMatrix) {
        return ObjectAnimator.ofObject(imageView, ANIMATED_TRANSFORM_PROPERTY,
                new MatrixEvaluator(), startMatrix, endMatrix);
    }

    public static class MatrixEvaluator implements TypeEvaluator<Matrix> {

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
