package me.hiten.extscaleimageview.demo;

import android.app.SharedElementCallback;
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
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import me.hiten.extscaleimageview.ExtScaleImageView;
import me.hiten.extscaleimageview.ScaleTypeParcel;
import me.hiten.extscaleimageview.se.SharedElementSnapshot;


public class ExtImageViewActivity extends AppCompatActivity implements View.OnClickListener {

    private ExtScaleImageView extScaleImageView;

    private TextView tvInfo;

    private CheckBox checkBox;

    private SeekBar seekBarX;
    private SeekBar seekBarY;

    private TextView seekBarTv;


    private int cUrlIndex = -1;

    private static boolean sFirstShow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ext_imageview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("扩展ScaleType");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        tvInfo = findViewById(R.id.tv_info);
        extScaleImageView = findViewById(R.id.ext_iv);
        extScaleImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        seekBarX = findViewById(R.id.seek_bar_x);
        seekBarY = findViewById(R.id.seek_bar_y);
        seekBarTv = findViewById(R.id.seek_bar_ratio);
        checkBox = findViewById(R.id.ckb_clip);
        extScaleImageView.setOnClickListener(this);
        listener();
        nextPic();
        extScaleImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Drawable drawable = extScaleImageView.getDrawable();
                if (drawable != null && drawable.getIntrinsicWidth() > 0 && drawable.getIntrinsicHeight() > 0) {
                    extScaleImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                showInfo();
            }
        });
        if (sFirstShow) {
            extScaleImageView.post(new Runnable() {
                @Override
                public void run() {
                    openOptionsMenu();
                    sFirstShow = false;
                }
            });
        }
    }

    private float seekX;
    private float seekY;

    private void listener() {
        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float max = seekBar.getMax();
                float ratio = progress / max;
                if (seekBar==seekBarX){
                    seekX = ratio;
                }else {
                    seekY = ratio;
                }
                extScaleImageView.setExtScaleType(seekX,seekY);
                seekBarTv.setText("cropX:" + seekX + " cropY:" + seekY);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        seekBarX.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBarY.setOnSeekBarChangeListener(onSeekBarChangeListener);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((ViewGroup) extScaleImageView.getParent()).setClipChildren(!isChecked);
            }
        });
    }

    private void showInfo() {
        int width = extScaleImageView.getWidth();
        int height = extScaleImageView.getHeight();
        Drawable drawable = extScaleImageView.getDrawable();
        if (drawable != null) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();

            String text = "控件尺寸:" + width + "x" + height + "\n" + "图片尺寸:" + intrinsicWidth + "x" + intrinsicHeight;
            tvInfo.setText(text);
        }

    }

    private String getUrl() {
        return URL.URLS[cUrlIndex % URL.URLS.length];
    }

    private void nextPic() {
        cUrlIndex++;
        final String url = getUrl();
        RequestOptions requestOptions = RequestOptions.noTransformation();
        Glide.with(this)
                .load(url)
                .apply(requestOptions)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        extScaleImageView.setImageDrawable(resource);
                        ViewCompat.setTransitionName(extScaleImageView, url);
                        showInfo();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.setGroupCheckable(R.id.menu_group_scale_type, true, true);
        menu.findItem(R.id.menu_type_center_crop).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.isCheckable()) {
            item.setChecked(true);
        }
        final int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (itemId == R.id.menu_type_align_point_crop) {
            seekBarX.setVisibility(View.VISIBLE);
            seekBarY.setVisibility(View.VISIBLE);
            seekBarTv.setVisibility(View.VISIBLE);
            extScaleImageView.setSmoothSwitch(false);
            extScaleImageView.setExtScaleType(0,0);
            return true;
        }
        if (itemId !=R.id.menu_next_pic){
            seekBarX.setVisibility(View.GONE);
            seekBarY.setVisibility(View.GONE);
            seekBarTv.setVisibility(View.GONE);
            extScaleImageView.setSmoothSwitch(true);
        }
        switch (itemId) {
            case R.id.menu_next_pic:
                nextPic();
                seekBarX.setProgress(0);
                seekBarY.setProgress(0);
                break;

            case R.id.menu_type_align_bottom_crop:
                extScaleImageView.setExtScaleType(ExtScaleImageView.ExtScaleType.ALIGN_BOTTOM_CROP);
                break;
            case R.id.menu_type_align_top_crop:
                extScaleImageView.setExtScaleType(ExtScaleImageView.ExtScaleType.ALIGN_TOP_CROP);
                break;
            case R.id.menu_type_align_left_crop:
                extScaleImageView.setExtScaleType(ExtScaleImageView.ExtScaleType.ALIGN_LEFT_CROP);
                break;
            case R.id.menu_type_align_right_crop:
                extScaleImageView.setExtScaleType(ExtScaleImageView.ExtScaleType.ALIGN_RIGHT_CROP);
                break;
            case R.id.menu_type_center_crop:
                extScaleImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                break;
            case R.id.menu_type_center:
                extScaleImageView.setScaleType(ImageView.ScaleType.CENTER);
                break;
            case R.id.menu_type_center_inside:
                extScaleImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                break;
            case R.id.menu_type_fit_xy:
                extScaleImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case R.id.menu_type_fit_center:
                extScaleImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                break;
            case R.id.menu_type_fit_end:
                extScaleImageView.setScaleType(ImageView.ScaleType.FIT_END);
                break;
            case R.id.menu_type_fit_start:
                extScaleImageView.setScaleType(ImageView.ScaleType.FIT_START);
                break;
            case R.id.menu_type_fit_width_center_top_height:
                extScaleImageView.setExtScaleType(ExtScaleImageView.ExtScaleType.FIT_WIDTH_CENTER_TOP_HEIGHT);
                break;

        }
        return true;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.ext_iv) {
            Intent intent = new Intent(this, SimpleDetailActivity.class);
            intent.putExtra("url", getUrl());
            intent.putExtra("scale_type_parcel", new ScaleTypeParcel(extScaleImageView));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setExitSharedElementCallback(new SharedElementCallback() {
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
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, extScaleImageView, getUrl());
            ActivityCompat.startActivity(this, intent, activityOptionsCompat.toBundle());
        }
    }
}
