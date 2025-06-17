package com.dish.app;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int[][] containerAndIndicatorIds = {
            {R.id.homeButton, R.id.homeIndicator},
            {R.id.searchButton, R.id.searchIndicator},
            {R.id.addNewRecipeButton, R.id.addNewRecipeIndicator},
            {R.id.profileButton, R.id.profileIndicator}
    };
    private View homeButton;

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        homeButton = findViewById(R.id.homeButton);
        setNavButtons();

        Toast.makeText(MainActivity.this, "Próba połączenia z Firebase...", Toast.LENGTH_SHORT).show();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dish-7d8f1-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = database.getReference("testConnection");
        ref.setValue("connected");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(MainActivity.this, "Połączono z Firebase!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Błąd: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.backgroundSpace), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.topMargin = systemBars.top + 15;
            v.setLayoutParams(params);

            return insets;
        });
        applyVerticalInsets(findViewById(R.id.navigationBar), 15);

        loadPostsFromFirebase();
    }

    private Bitmap base64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void loadPostsFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dish-7d8f1-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference postsRef = database.getReference("posts");

        LinearLayout scrollableContainer = findViewById(R.id.scrollableContent);
        LayoutInflater inflater = LayoutInflater.from(this);

        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scrollableContainer.removeAllViews();
                List<Post> postList = new ArrayList<>();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) postList.add(post);
                }

                Collections.sort(postList, Comparator.comparingLong(p -> p.timestamp)); // lub .reversed() jeśli od najnowszego
                Collections.reverse(postList); // od najnowszego do najstarszego

                for (Post post : postList) {
                    View postView = inflater.inflate(R.layout.post_item, scrollableContainer, false);

                    TextView title = postView.findViewById(R.id.postRecipeTitle);
                    TextView username = postView.findViewById(R.id.postUsername);
                    TextView time = postView.findViewById(R.id.postTime);
                    ImageView recipeImage = postView.findViewById(R.id.postRecipeImage);
                    TextView instructions = postView.findViewById(R.id.postRecipeInstructions);

                    title.setText(post.title);
                    username.setText(post.username);
                    time.setText(post.time);
                    instructions.setText(post.instructions);

                    if (post.imageBase64 != null && !post.imageBase64.trim().isEmpty()) {
                        try {
                            Bitmap bitmap = base64ToBitmap(post.imageBase64);
                            recipeImage.setImageBitmap(bitmap);
                            recipeImage.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            recipeImage.setVisibility(View.GONE);
                        }
                    } else {
                        recipeImage.setVisibility(View.GONE);
                    }

                    scrollableContainer.addView(postView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Błąd: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        if (homeButton != null) {
            homeButton.performClick(); // simulate click to refresh posts
        }
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

                if (ids[0] == R.id.addNewRecipeButton) {
                    Intent intent = new Intent(MainActivity.this, AddRecipeActivity.class);
                    startActivity(intent);
                }
                if (ids[0] == R.id.profileButton) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                }

                if (ids[0] == R.id.homeButton) {
                    loadPostsFromFirebase();
                }

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