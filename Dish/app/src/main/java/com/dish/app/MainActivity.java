package com.dish.app;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    int[][] containerAndIndicatorIds = {
            {R.id.homeButton, R.id.homeIndicator},
            {R.id.searchButton, R.id.searchIndicator},
            {R.id.addNewRecipeButton, R.id.addNewRecipeIndicator},
            {R.id.profileButton, R.id.profileIndicator}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View navigationBar = findViewById(R.id.navigationBar);

        LayoutTransition transition = new LayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);

        LinearLayout homeButtonContainer = findViewById(R.id.homeButtonContainer);
        homeButtonContainer.setLayoutTransition(transition);
        LinearLayout searchButtonContainer = findViewById(R.id.searchButtonContainer);
        searchButtonContainer.setLayoutTransition(transition);
        LinearLayout addNewRecipeButtonContainer = findViewById(R.id.addNewRecipeButtonContainer);
        addNewRecipeButtonContainer.setLayoutTransition(transition);
        LinearLayout profileButtonContainer = findViewById(R.id.profileButtonContainer);
        profileButtonContainer.setLayoutTransition(transition);

        for(int[] ids : containerAndIndicatorIds)
        {
            View button = findViewById(ids[0]);
            View indicator = findViewById(ids[1]);

            button .setOnClickListener(view -> {

                button.animate()
                        .alpha(0.7f)
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(250)
                        .withEndAction(() -> {
                            button.animate()
                                    .alpha(1.0f)
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(250)
                                    .start();
                        })
                        .start();

                navigationBar.animate()
                        .scaleX(1.025f)
                        .scaleY(1.025f)
                        .setDuration(200)
                        .setInterpolator(new OvershootInterpolator())
                        .withEndAction(() -> {
                            navigationBar.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(200)
                                    .start();
                        })
                        .start();

                for(int[] otherIds : containerAndIndicatorIds) {
                    View otherButton = findViewById(otherIds[0]);
                    View otherIndicator = findViewById(otherIds[1]);

                    boolean isActive = (otherButton == view);
                    int color = isActive ? Color.parseColor("#000000") : Color.parseColor("#343A40");
                    otherButton.setBackgroundTintList(ColorStateList.valueOf(color));

                    if (isActive) {
                        otherIndicator.setVisibility(View.VISIBLE);
                        otherIndicator.setAlpha(0f);
                        otherIndicator.setTranslationY(10f);
                        otherIndicator.animate()
                                .alpha(1f)
                                .translationY(0f)
                                .setDuration(450)
                                .setInterpolator(new DecelerateInterpolator())
                                .start();
                    } else {
                        otherIndicator.animate()
                                .alpha(0f)
                                .translationY(10f)
                                .setDuration(250)
                                .setInterpolator(new AccelerateInterpolator())
                                .withEndAction(() -> {
                                    otherIndicator.setVisibility(View.GONE);
                                    otherIndicator.setAlpha(0f);
                                    otherIndicator.setTranslationY(1000f); // move indicator out of device bounds
                                })
                                .start();
                    }
                }
            });
        }
    }
}