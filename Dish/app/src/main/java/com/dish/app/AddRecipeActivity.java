package com.dish.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddRecipeActivity extends AppCompatActivity {

    EditText titleInput, usernameInput, instructionsInput;
    Button addButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_recipe);

        titleInput = findViewById(R.id.inputTitle);
        usernameInput = findViewById(R.id.inputUsername);
        instructionsInput = findViewById(R.id.inputInstructions);
        addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String username = usernameInput.getText().toString().trim();
            String instructions = instructionsInput.getText().toString().trim();

            if (title.isEmpty() || username.isEmpty() || instructions.isEmpty()) {
                Toast.makeText(this, "Wypełnij wszystkie pola!", Toast.LENGTH_SHORT).show();
                return;
            }

            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            uploadPostToFirebase(title, username, currentTime, instructions);
        });
    }

    private void uploadPostToFirebase(String title, String username, String time, String instructions) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dish-7d8f1-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference postsRef = database.getReference("posts");

        String postId = postsRef.push().getKey();
        Post post = new Post(title, username, time, instructions);

        if (postId != null) {
            Toast.makeText(AddRecipeActivity.this, "Próba dodania przepisu...", Toast.LENGTH_SHORT).show();
            DatabaseReference newPostRef = database.getReference("posts").child(postId);

            newPostRef.setValue(post, new DatabaseReference.CompletionListener() {
               @Override public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                   if (error != null) {
                       Toast.makeText(AddRecipeActivity.this, "Błąd podczas dodawania przepisu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                       Log.e("Firebase", "Data could not be saved: " + error.getMessage(), error.toException()); // Log the error
                   } else {
                       Toast.makeText(AddRecipeActivity.this, "Dodano przepis!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the dialog after successful upload
                   }
               }
            });
        } else {
            Toast.makeText(AddRecipeActivity.this, "Błąd: Nie udało się wygenerować klucza ID.", Toast.LENGTH_SHORT).show();
        }
    }
}
