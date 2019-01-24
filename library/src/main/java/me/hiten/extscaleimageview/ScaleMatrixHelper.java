package me.hiten.extscaleimageview;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

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


    /**
     * 根据ExtScaleType计算矩阵
     *
     * @param inMatrix     接收结果的矩阵
     * @param extScaleType ScaleType
     * @param cropperPosX  cropperPosX
     * @param cropperPosY  cropperPosY
     */
    void calculateMatrix(Matrix inMatrix, ExtScaleImageView.ExtScaleType extScaleType, float cropperPosX, float cropperPosY) {
        if (inMatrix == null) {
            return;
        }
        if (extScaleType == null) {
            return;
        }

        if (!Utils.viewHasSize(mExtScaleImageView)) {
            return;
        }

        Drawable drawable = mExtScaleImageView.getDrawable();
        if (!Utils.drawableHasSize(drawable)) {
            return;
        }

        if (!(ExtScaleImageView.ExtScaleType.ALIGN_POINT_CROP == extScaleType) && !(ExtScaleImageView.ExtScaleType.FIT_WIDTH_CENTER_TOP_HEIGHT == extScaleType)) {
            cropperPosX = extScaleType.cropperPosX;
            cropperPosY = extScaleType.cropperPosY;
            extScaleType = ExtScaleImageView.ExtScaleType.ALIGN_POINT_CROP;
        }


        final int vWidth = Utils.getWidht(mExtScaleImageView);
        final int vHeight = Utils.getHeight(mExtScaleImageView);

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
            int ddHeight = (int) (dHeight * scale+0.5f);
            if (ddHeight<vHeight) {
                inMatrix.postTranslate(0,
                        (vHeight - dHeight*scale) / 2f+0.5f);
            }
        }
    }


    /**
     * 根据ScaleType计算矩阵
     *
     * @param inMatrix  接收结果的矩阵
     * @param scaleType ScaleType
     */
    void calculateMatrix(Matrix inMatrix, ImageView.ScaleType scaleType) {
        Drawable drawable = mExtScaleImageView.getDrawable();
        if (!Utils.drawableHasSize(drawable)) {
            return;
        }
        if (!Utils.viewHasSize(mExtScaleImageView)) {
            return;
        }

        final float viewWidth = Utils.getWidht(mExtScaleImageView);
        final float viewHeight = Utils.getHeight(mExtScaleImageView);
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

    private void calculateMatrix() {
        if (mTempMatrix==null){
            mTempMatrix = new Matrix();
        }
        mTempMatrix.reset();
        calculateMatrix(mTempMatrix, mExtScaleImageView.getExtScaleType(), mExtScaleImageView.getExtCropperPos().x, mExtScaleImageView.getExtCropperPos().y);
        mExtScaleImageView.setImageMatrix(mTempMatrix);
    }

}
