package com.dish.app;

import android.os.Bundle;
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

            uploadPostToFirebase(title, username, "teraz", instructions);
        });
    }

    private void uploadPostToFirebase(String title, String username, String time, String instructions) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference postsRef = database.getReference("posts");

        String postId = postsRef.push().getKey();
        Post post = new Post(title, username, time, instructions);

        if (postId != null) {
            Toast.makeText(AddRecipeActivity.this, "Próba dodania przepisu...", Toast.LENGTH_SHORT).show();
            DatabaseReference postRef = database.getReference("posts").child(postId);
            postRef.setValue(post); // Zapisujemy dane

            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                private DatabaseError error;

                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(AddRecipeActivity.this, "Dodano przepis!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddRecipeActivity.this, "Błąd: nie udało się dodać przepisu.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AddRecipeActivity.this, "Błąd: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
