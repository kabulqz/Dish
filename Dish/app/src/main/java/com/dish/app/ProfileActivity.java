package com.dish.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// ProfileActivity.java
public class ProfileActivity extends AppCompatActivity {
    private TextView postsCountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logoutUser());
        postsCountText = findViewById(R.id.profilePostsCount);
        loadUserPosts();
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        finish();
    }

    private void loadUserPosts() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dish-7d8f1-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference postsRef = database.getReference("posts");
        
        LinearLayout scrollableContainer = findViewById(R.id.scrollableContent);
        LayoutInflater inflater = LayoutInflater.from(this);

        // Filtruj posty tylko dla obecnego użytkownika
        Query query = postsRef.orderByChild("username").equalTo(user.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scrollableContainer.removeAllViews();
                List<Post> userPosts = new ArrayList<>();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) userPosts.add(post);
                }

                Collections.sort(userPosts, Comparator.comparingLong(p -> p.timestamp));
                Collections.reverse(userPosts);
                postsCountText.setText(userPosts.size() + " posts");

                // Użyj tej samej metody co w MainActivity do wyświetlania postów
                displayPosts(userPosts, scrollableContainer, inflater);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Błąd: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayPosts(List<Post> posts, LinearLayout container, LayoutInflater inflater) {
        for (Post post : posts) {
            View postView = inflater.inflate(R.layout.post_item, container, false);

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
                    Bitmap bitmap = BitmapFactory.decodeByteArray(
                            Base64.decode(post.imageBase64, Base64.DEFAULT),
                            0, 
                            Base64.decode(post.imageBase64, Base64.DEFAULT).length);
                    recipeImage.setImageBitmap(bitmap);
                    recipeImage.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    recipeImage.setVisibility(View.GONE);
                }
            } else {
                recipeImage.setVisibility(View.GONE);
            }

            container.addView(postView);
        }
    }
}