package me.hiten.extscaleimageview.demo;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import me.hiten.extscaleimageview.ExtScaleImageView;


public class MainActivity extends AppCompatActivity {

    private ExtScaleImageView extScaleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        extScaleImageView = findViewById(R.id.ext_iv);
        extScaleImageView.setExtScaleType(ExtScaleImageView.ExtScaleType.ALIGN_LEFT_CROP);
        RequestOptions requestOptions = RequestOptions.noTransformation();
        Glide.with(this)
                .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1548264714880&di=8f77752198a5fad278a2183977afb509&imgtype=0&src=http%3A%2F%2Fimg.kutoo8.com%2Fupload%2Fimage%2F95351812%2F001%2520%25283%2529_960x540.jpg")
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("MainActivity", "onLoadFailed: "+e.toString() );
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.e("MainActivity", "onLoadFailed: "+resource.toString() );
                        return false;
                    }
                })
                .into(extScaleImageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu,menu);
        menu.setGroupCheckable(Menu.NONE,true,true);
        menu.findItem(R.id.menu_type_align_left_crop).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        item.setChecked(true);
        final int itemId = item.getItemId();
        switch (itemId){
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
        }
        return true;
    }
}
