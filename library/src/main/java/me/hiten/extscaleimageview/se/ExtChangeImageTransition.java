package me.hiten.extscaleimageview.se;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.ViewGroup;

import me.hiten.extscaleimageview.ExtScaleImageView;
import me.hiten.extscaleimageview.R;
import me.hiten.extscaleimageview.ScaleMatrixUtils;
import me.hiten.extscaleimageview.ScaleTypeParcel;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ExtChangeImageTransition extends Transition {


    private static final String PROPNAME_MATRIX = "custom:extChangeImageTransition:matrix";
    private static final String PROPNAME_BOUNDS = "custom:extChangeImageTransition:bounds";

    public ExtChangeImageTransition() {
        addTarget(ExtScaleImageView.class);
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        Object tag = transitionValues.view.getTag(R.id.share_element_tag);
        Log.e("SharedElementLog", "Enter:captureStartValues: " + (tag != null ? tag.toString() : "NULL") + "---" + transitionValues.view.getLeft());
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        Object tag = transitionValues.view.getTag(R.id.share_element_tag);
        Log.e("SharedElementLog", "Enter:captureEndValues: " + (tag != null ? tag.toString() : "NULL") + "---" + transitionValues.view.getLeft());
        captureValues(transitionValues);
    }

    private void captureValues(TransitionValues transitionValue) {
        ExtScaleImageView view = (ExtScaleImageView) transitionValue.view;
        ScaleTypeParcel scaleTypeParcel = (ScaleTypeParcel) view.getTag(R.id.share_element_tag);
        if (scaleTypeParcel == null) {
            scaleTypeParcel = new ScaleTypeParcel(view);
        }
        Matrix matrix = new Matrix();
        if (scaleTypeParcel.extScaleType != null) {
            ScaleMatrixUtils.calculateMatrix(view, matrix, scaleTypeParcel.extScaleType, scaleTypeParcel.cropperPosX, scaleTypeParcel.cropperPosY);
        } else if (scaleTypeParcel.scaleType != null) {
            ScaleMatrixUtils.calculateMatrix(view, matrix, scaleTypeParcel.scaleType);
        }
        transitionValue.values.put(PROPNAME_MATRIX, matrix);

        int left = view.getLeft();
        int top = view.getTop();
        int right = view.getRight();
        int bottom = view.getBottom();

        Rect bounds = new Rect(left, top, right, bottom);
        transitionValue.values.put(PROPNAME_BOUNDS, bounds);
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        ExtScaleImageView endView = (ExtScaleImageView) endValues.view;
        Matrix startMatrix = (Matrix) startValues.values.get(PROPNAME_MATRIX);
        Matrix endMatrix = (Matrix) endValues.values.get(PROPNAME_MATRIX);
        return ScaleMatrixUtils.createMatrixAnimator(endView, startMatrix, endMatrix);
    }
}
