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

        View homeButton = findViewById(R.id.homeButton);
        View searchButton = findViewById(R.id.searchButton);
        View addNewRecipeButton = findViewById(R.id.addNewRecipeButton);
        View profileButton = findViewById(R.id.profileButton);

        View[] buttons;
        buttons = new View[]{homeButton, searchButton, addNewRecipeButton, profileButton};
        for(View button : buttons) {
            button.setOnClickListener(view ->{
                for(View other : buttons) {
                    int color = (other == view) ? Color.parseColor("#000000") : Color.parseColor("#343A40");
                    other.setBackgroundTintList(ColorStateList.valueOf(color));
                }

                if(view.getId() == R.id.homeButton) {
                    Toast.makeText(this, "Home Button Clicked", Toast.LENGTH_SHORT).show();
                } else if(view.getId() == R.id.searchButton) {
                    Toast.makeText(this, "Search Button Clicked", Toast.LENGTH_SHORT).show();
                } else if(view.getId() == R.id.addNewRecipeButton) {
                    Toast.makeText(this, "Add New Recipe Button Clicked", Toast.LENGTH_SHORT).show();
                } else if(view.getId() == R.id.profileButton) {
                    Toast.makeText(this, "Profile Button Clicked", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }
}