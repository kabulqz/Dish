package com.dish.app;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View homeButton = findViewById(R.id.homeButton);
        View searchButton = findViewById(R.id.searchButton);
        View addNewRecipeButton = findViewById(R.id.addNewRecipeButton);
        View profileButton = findViewById(R.id.profileButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Home Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Search Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        addNewRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Add New Recipe Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Profile Clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }
}