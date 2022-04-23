package com.example.android.popularmovies3;

import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class MainIntro extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Enable/disable skip button */
        setButtonBackVisible(true);
        setButtonBackFunction(BUTTON_BACK_FUNCTION_SKIP);

        /* Enable/disable finish button */
        setButtonNextVisible(true);
        setButtonNextFunction(BUTTON_NEXT_FUNCTION_NEXT_FINISH);

        addSlide(new SimpleSlide.Builder()
                .layout(R.layout.intro_layout)
                .title(R.string.app_name)
                .description(R.string.intro1)
                .image(R.drawable.app_icon)
                .background(R.color.app_background_color)
                .build());

        addSlide(new SimpleSlide.Builder()
                .layout(R.layout.intro_layout)
                .title(R.string.sorting)
                .description(R.string.intro2)
                .image(R.drawable.app_icon)
                .background(R.color.app_background_color)
                .build());

        addSlide(new SimpleSlide.Builder()
                .layout(R.layout.intro_layout)
                .title(R.string.details)
                .description(R.string.intro3)
                .image(R.drawable.app_icon)
                .background(R.color.app_background_color)
                .build());

        addSlide(new SimpleSlide.Builder()
                .layout(R.layout.intro_layout)
                .title(R.string.fav)
                .description(R.string.intro5)
                .image(R.drawable.app_icon)
                .background(R.color.app_background_color)
                .build());

        addSlide(new SimpleSlide.Builder()
                .layout(R.layout.intro_layout)
                .title(R.string.thats_all)
                .description(R.string.intro_last)
                .image(R.drawable.app_icon)
                .background(R.color.app_background_color)
                .build());
    }
}
