package com.dish.app;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
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

        setNavButtons();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.backgroundSpace), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.topMargin = systemBars.top + 15;
            v.setLayoutParams(params);

            return insets;
        });
        applyVerticalInsets(findViewById(R.id.navigationBar), 15);

        LinearLayout scrollableContainer = findViewById(R.id.scrollableContent);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < 5; i++) {
            View postView = inflater.inflate(R.layout.post_item, scrollableContainer, false);

            TextView title = postView.findViewById(R.id.postRecipeTitle);
            TextView username = postView.findViewById(R.id.postUsername);
            TextView time = postView.findViewById(R.id.postTime);

            title.setText("Makaron Carbonara " + (i + 1));
            username.setText("User " + (i + 1));
            time.setText((i + 1) + " hours ago");

            scrollableContainer.addView(postView);
        }
    }

    private void applyVerticalInsets(View view, int extraMarginDp) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.topMargin = systemBars.top + extraMarginDp;
            params.bottomMargin = systemBars.bottom + extraMarginDp;
            v.setLayoutParams(params);

            return insets;
        });
    }

    private void setNavButtons() {
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

            button .setOnClickListener(view -> {
                button.animate()
                        .alpha(0.6f)
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(300)
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
                        .setDuration(250)
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
                        otherIndicator.setAlpha(0f);
                        otherIndicator.setTranslationY(10f);
                        otherIndicator.setVisibility(View.VISIBLE);
                        otherIndicator.animate()
                                .alpha(1f)
                                .translationY(0f)
                                .setDuration(400)
                                .setInterpolator(new AccelerateInterpolator())
                                .start();
                    } else {
                        otherIndicator.animate()
                                .alpha(0f)
                                .translationY(10f)
                                .setDuration(250)
                                .setInterpolator(new DecelerateInterpolator())
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