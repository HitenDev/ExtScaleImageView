package me.hiten.extscaleimageview.demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import me.hiten.extscaleimageview.ExtScaleImageView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ExtScaleImageView extScaleImageView;

    private TextView tvInfo;

    private SeekBar seekBarX;
    private SeekBar seekBarY;

    private TextView seekBarTv;


    private int cUrlIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvInfo = findViewById(R.id.tv_info);
        extScaleImageView = findViewById(R.id.ext_iv);
        extScaleImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        findViewById(R.id.btn_layout_test).setOnClickListener(this);
        seekBarX = findViewById(R.id.seek_bar_x);
        seekBarY = findViewById(R.id.seek_bar_y);
        seekBarTv = findViewById(R.id.seek_bar_ratio);
        listenerSeekBar();
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
    }

    private float seekX;
    private float seekY;

    private void listenerSeekBar(){
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
                seekBarTv.setText("x:"+seekX+" y:"+seekY);
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
    }

    private void showInfo() {
        int width = extScaleImageView.getWidth();
        int height = extScaleImageView.getHeight();
        Drawable drawable = extScaleImageView.getDrawable();
        if (drawable != null) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();

            String text = "控件:" + width + "x" + height + "\n" + "图片:" + intrinsicWidth + "x" + intrinsicHeight;
            tvInfo.setText(text);
        }

    }

    private ValueAnimator valueAnimator;

    private void small() {
        int width = extScaleImageView.getWidth();
        if (width <= 0) {
            return;
        }
        smoothLayout(width,width/3);

    }

    private void big() {
        int width = extScaleImageView.getWidth();
        if (width <= 0) {
            return;
        }
        smoothLayout(width,width*3);
    }

    private void smoothLayout(final int from,final int to){
        if (valueAnimator!=null&&valueAnimator.isRunning()){
            return;
        }
        valueAnimator = ValueAnimator.ofInt(from,to);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                if (from<to){//放大
                    if (animatedValue==to){
                        animatedValue = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                }
                ViewGroup.LayoutParams layoutParams = extScaleImageView.getLayoutParams();
                layoutParams.width = animatedValue;
                extScaleImageView.requestLayout();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showInfo();
                ((ViewGroup) extScaleImageView.getParent()).setClipChildren(from<to);
            }
        });
        valueAnimator.setDuration(400);
        valueAnimator.start();
    }


    private void nextPic() {
        RequestOptions requestOptions = RequestOptions.noTransformation();
        Glide.with(this)
                .load(URL.URLS[cUrlIndex%URL.URLS.length])
                .apply(requestOptions)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        extScaleImageView.setImageDrawable(resource);
                        showInfo();
                    }
                });
        cUrlIndex++;
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
        if (itemId == R.id.menu_type_align_point_crop) {
            int width = extScaleImageView.getLayoutParams().width;
            if (width == ViewGroup.LayoutParams.MATCH_PARENT) {
                small();
            }
            findViewById(R.id.btn_layout_test).setVisibility(View.GONE);
            seekBarX.setVisibility(View.VISIBLE);
            seekBarY.setVisibility(View.VISIBLE);
            seekBarTv.setVisibility(View.VISIBLE);
            extScaleImageView.setSmoothSwitch(false);
            extScaleImageView.setExtScaleType(0,0);
            return true;
        }
        if (itemId !=R.id.menu_next_pic){
            findViewById(R.id.btn_layout_test).setVisibility(View.VISIBLE);
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
        if (view.getId() == R.id.btn_layout_test) {
            int width = extScaleImageView.getLayoutParams().width;
            if (width == ViewGroup.LayoutParams.MATCH_PARENT) {
                small();
            } else {
                big();
            }
        }
    }
}
