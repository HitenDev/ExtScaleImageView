package me.hiten.extscaleimageview.demo;

import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeImageTransform;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import me.hiten.extscaleimageview.ExtScaleImageView;
import me.hiten.extscaleimageview.ScaleTypeParcel;
import me.hiten.extscaleimageview.se.ExtChangeImageTransition;
import me.hiten.extscaleimageview.se.SharedElementSnapshot;

public class SimpleDetailActivity extends AppCompatActivity {

    private ExtScaleImageView extScaleImageView;
    private String url;

    private boolean loaded = false;
    private ScaleTypeParcel scaleTypeParcel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_detail);
        Intent intent = getIntent();
        if (intent != null) {
            url = intent.getStringExtra("url");
            scaleTypeParcel = intent.getParcelableExtra("scale_type_parcel");
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Transition sharedElementEnterTransition = getWindow().getSharedElementEnterTransition();
            if (sharedElementEnterTransition instanceof TransitionSet) {
                int transitionCount = ((TransitionSet) sharedElementEnterTransition).getTransitionCount();
                for (int i = 0; i < transitionCount; i++) {
                    Transition transitionAt = ((TransitionSet) sharedElementEnterTransition).getTransitionAt(i);
                    if (transitionAt instanceof ChangeImageTransform || transitionAt instanceof ExtChangeImageTransition) {
                        ((TransitionSet) sharedElementEnterTransition).removeTransition(transitionAt);
                        i--;
                    }
                }
                if (scaleTypeParcel != null) {
                    ((TransitionSet) sharedElementEnterTransition).addTransition(new ExtChangeImageTransition());
                }
            }
            setEnterSharedElementCallback(new SharedElementCallback() {

                ScaleTypeParcel scaleTypeParcel;

                @Override
                public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                    super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);
                    for (View view : sharedElements) {
                        if (view instanceof ExtScaleImageView) {
                            view.setTag(R.id.share_element_tag, scaleTypeParcel);
                        }
                    }
                }

                @Override
                public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                    super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                    for (View view : sharedElements) {
                        if (view instanceof ExtScaleImageView) {
                            view.setTag(R.id.share_element_tag, null);
                        }
                    }
                }

                @Override
                public View onCreateSnapshotView(Context context, Parcelable snapshot) {
                    if (snapshot instanceof SharedElementSnapshot) {
                        Parcelable snap = ((SharedElementSnapshot) snapshot).snapshot;
                        Parcelable extra = ((SharedElementSnapshot) snapshot).extra;
                        if (extra instanceof ScaleTypeParcel) {
                            scaleTypeParcel = (ScaleTypeParcel) extra;
                        }
                        return super.onCreateSnapshotView(context, snap);
                    }
                    return super.onCreateSnapshotView(context, snapshot);
                }

            });
        }
        extScaleImageView = findViewById(R.id.ext_iv);
        ViewCompat.setTransitionName(extScaleImageView, url);
        extScaleImageView.setSmoothSwitch(false);
        extScaleImageView.setExtScaleType(ExtScaleImageView.ExtScaleType.FIT_WIDTH_CENTER_TOP_HEIGHT);
        supportPostponeEnterTransition();
        RequestOptions requestOptions = RequestOptions.noTransformation();
        extScaleImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                extScaleImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (loaded) {
                    supportStartPostponedEnterTransition();
                }
                return false;
            }
        });
        Glide.with(this).load(url).apply(requestOptions).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                if (extScaleImageView.getWidth() > 0 && extScaleImageView.getHeight() > 0) {
                    supportStartPostponedEnterTransition();
                }
                {
                    loaded = true;
                }
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                if (extScaleImageView.getWidth() > 0 && extScaleImageView.getHeight() > 0) {
                    supportStartPostponedEnterTransition();
                } else {
                    loaded = true;
                }
                return false;
            }
        }).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Drawable> transition) {
                extScaleImageView.setImageDrawable(resource);
            }
        });
        extScaleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
