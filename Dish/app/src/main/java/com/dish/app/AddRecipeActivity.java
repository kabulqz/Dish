package com.dish.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddRecipeActivity extends AppCompatActivity {

    EditText titleInput, usernameInput, instructionsInput;
    Button addButton;

    private static final int PICK_IMAGE_REQUEST = 1;
    private String base64Image = null;
    ImageView previewImage;
    Button selectImageButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_recipe);

        titleInput = findViewById(R.id.inputTitle);
        usernameInput = findViewById(R.id.inputUsername);
        instructionsInput = findViewById(R.id.inputInstructions);
        addButton = findViewById(R.id.addButton);
        previewImage = findViewById(R.id.previewImage);
        selectImageButton = findViewById(R.id.selectImageButton);

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        addButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String username = usernameInput.getText().toString().trim();
            String instructions = instructionsInput.getText().toString().trim();

            if (title.isEmpty() || username.isEmpty() || instructions.isEmpty()) {
                Toast.makeText(this, "Wypełnij wszystkie pola!", Toast.LENGTH_SHORT).show();
                return;
            }

            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            long timestamp = System.currentTimeMillis();
            uploadPostToFirebase(title, username, currentTime, instructions, timestamp);
        });
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                previewImage.setImageBitmap(bitmap);
                previewImage.setVisibility(View.VISIBLE);
                base64Image = bitmapToBase64(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Błąd podczas wczytywania obrazu", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadPostToFirebase(String title, String username, String time, String instructions, long timestamp) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://dish-7d8f1-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference postsRef = database.getReference("posts");

        String postId = postsRef.push().getKey();
        Post post = new Post(title, username, time, instructions, timestamp, base64Image);

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
