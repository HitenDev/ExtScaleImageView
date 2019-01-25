package me.hiten.extscaleimageview.demo.ninegrid;

import android.content.Context;
import android.widget.ImageView;

import java.util.List;

public interface ItemImageLongClickListener<T> {
    boolean onItemImageLongClick(Context context, ImageView imageView, int index, List<T> list);
}
