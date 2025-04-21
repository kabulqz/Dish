package com.dish.app;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private View homeButton, searchButton, addNewRecipeButton, profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        int[][] containerAndIndicatorIds = {
                {R.id.homeButton, R.id.homeIndicator},
                {R.id.searchButton, R.id.searchIndicator},
                {R.id.addNewRecipeButton, R.id.addNewRecipeIndicator},
                {R.id.profileButton, R.id.profileIndicator}
        };

        for(int[] ids : containerAndIndicatorIds)
        {
            View button = findViewById(ids[0]);
            View indicator = findViewById(ids[1]);

            button .setOnClickListener(view -> {
                for(int[] otherIds : containerAndIndicatorIds) {
                    View otherButton = findViewById(otherIds[0]);
                    View otherIndicator = findViewById(otherIds[1]);

                    boolean isActive = (otherButton == view);
                    int color = isActive ? Color.parseColor("#000000") : Color.parseColor("#343A40");
                    otherButton.setBackgroundTintList(ColorStateList.valueOf(color));
                    otherIndicator.setVisibility(isActive ? View.VISIBLE : View.GONE);

                    if (isActive) {
                        otherIndicator.setVisibility(View.VISIBLE);
                        otherIndicator.setAlpha(0f);
                        otherIndicator.setTranslationY(10f);
                        otherIndicator.animate()
                                .alpha(1f)
                                .translationY(0f)
                                .setDuration(300)
                                .start();
                    } else {
                        otherIndicator.animate()
                                .alpha(0f)
                                .translationY(10f)
                                .setDuration(200)
                                .withEndAction(() -> otherIndicator.setVisibility(View.GONE))
                                .start();
                    }
                }

                button.animate()
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(200)
                        .withEndAction(() -> {
                            button.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(200)
                                    .start();
                        })
                        .start();

            });
        }
    }
}