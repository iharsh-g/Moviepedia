package com.example.android.popularmovies3;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class AboutActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ImageView iv1, iv2;

        iv1 = findViewById(R.id.iv_github);
        iv2 = findViewById(R.id.iv_browser);

        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/iharsh-g"));
                startActivity(intent);
            }
        });

        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/iharsh-g/popular_movies"));
                startActivity(intent);
            }
        });

        getSupportActionBar().setTitle("About");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSharedPreferences = getSharedPreferences("prefSettings", MODE_PRIVATE);
        if(mSharedPreferences.getBoolean("enableAnimations", true)) {
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}