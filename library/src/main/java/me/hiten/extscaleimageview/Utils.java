package me.hiten.extscaleimageview;

import android.graphics.drawable.Drawable;
import android.view.View;

public class Utils {

    static boolean drawableHasSize(Drawable drawable){
        if (drawable == null) {
            return false;
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            return false;
        }
        return true;
    }

    static boolean viewHasSize(View view){
        if (view==null){
            return false;
        }
        int width = view.getWidth();
        int height = view.getHeight();

        if (width <= 0 || height <= 0) {
            return false;
        }
        return true;
    }

    public static int getWidht(View view){
        if (view==null){
            return 0;
        }
        return view.getWidth() - view.getPaddingLeft() - view.getPaddingRight();
    }

    public static int getHeight(View view){
        if (view==null){
            return 0;
        }
        return view.getHeight() - view.getPaddingTop() - view.getPaddingBottom();
    }
}
