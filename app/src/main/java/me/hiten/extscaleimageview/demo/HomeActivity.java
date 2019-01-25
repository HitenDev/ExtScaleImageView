package me.hiten.extscaleimageview.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_ext_iv) {
            startActivity(new Intent(this, ExtImageViewActivity.class));
        } else if (id == R.id.btn_nine_grid) {
            startActivity(new Intent(this, NineGridActivity.class));
        }
    }
}
