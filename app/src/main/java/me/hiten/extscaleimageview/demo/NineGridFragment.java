package me.hiten.extscaleimageview.demo;

import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import me.hiten.extscaleimageview.ExtScaleImageView;
import me.hiten.extscaleimageview.ScaleTypeParcel;
import me.hiten.extscaleimageview.demo.ninegrid.ItemImageClickListener;
import me.hiten.extscaleimageview.demo.ninegrid.NineGridImageView;
import me.hiten.extscaleimageview.demo.ninegrid.NineGridImageViewAdapter;
import me.hiten.extscaleimageview.se.SharedElementSnapshot;

public class NineGridFragment extends Fragment {


    private NineGridImageView<ItemBean> mNineGridImageView;

    private NineAdapter mNineAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nine_grid, container, false);
        mNineGridImageView = rootView.findViewById(R.id.iv_nine_grid);
        mNineAdapter = new NineAdapter();
        mNineGridImageView.setAdapter(mNineAdapter);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadJson();
        mNineGridImageView.setItemImageClickListener(new ItemImageClickListener<ItemBean>() {
            @Override
            public void onItemImageClick(Context context, ImageView imageView, int index, List<ItemBean> list) {
                Intent intent = new Intent(getActivity(), SimpleDetailActivity.class);
                intent.putExtra("url", list.get(index).smallPicUrl);
                intent.putExtra("scale_type_parcel", new ScaleTypeParcel((ExtScaleImageView) imageView));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().setExitSharedElementCallback(new SharedElementCallback() {
                        @Override
                        public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
                            if (sharedElement instanceof ExtScaleImageView) {
                                ScaleTypeParcel scaleTypeParcel = new ScaleTypeParcel((ExtScaleImageView) sharedElement);
                                Parcelable snapshot = super.onCaptureSharedElementSnapshot(sharedElement, viewToGlobalMatrix, screenBounds);
                                return new SharedElementSnapshot(snapshot, scaleTypeParcel);
                            }

                            return super.onCaptureSharedElementSnapshot(sharedElement, viewToGlobalMatrix, screenBounds);
                        }

                    });
                }
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), imageView, list.get(index).smallPicUrl);
                ActivityCompat.startActivity(getActivity(), intent, activityOptionsCompat.toBundle());
            }
        });
    }

    private void loadJson() {
        try {
            InputStream inputStream = getResources().getAssets().open("nine.json");
            List<ItemBean> itemBeanList = new Gson().fromJson(new InputStreamReader(inputStream), new TypeToken<List<ItemBean>>() {
            }.getType());
            mNineGridImageView.setImagesData(itemBeanList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class NineAdapter extends NineGridImageViewAdapter<ItemBean> {

        @Override
        protected void onDisplayImage(Context context, final ImageView imageView, final ItemBean item) {
            ((ExtScaleImageView) imageView).setExtScaleType(item.cropperPosX, item.cropperPosY);
            RequestOptions requestOptions = RequestOptions.noTransformation();
            Glide.with(imageView).load(item.smallPicUrl).apply(requestOptions).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    ViewCompat.setTransitionName(imageView, item.smallPicUrl);
                    return false;
                }
            }).into(imageView);
        }

        @Override
        protected ImageView generateImageView(Context context) {
            ExtScaleImageView imageView = new ExtScaleImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }
    }

    static class ItemBean {
        public float cropperPosX;
        public float cropperPosY;
        public String smallPicUrl;
        public String picUrl;
        public int width;
        public int height;
    }
}
