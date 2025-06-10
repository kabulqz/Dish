package com.dish.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
            postsRef.child(postId).setValue(post).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Dodano przepis!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Błąd dodawania.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
