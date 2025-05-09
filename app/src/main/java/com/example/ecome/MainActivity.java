package com.example.ecome;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setUpUpperButtons();
    }

    private void setUpUpperButtons(){
        DrawerLayout drawerLayout = findViewById(R.id.date_drawer);
        Button burgerButton = findViewById(R.id.burgerButton);
        burgerButton.setOnClickListener(v -> {
            // Handle burger button click
            drawerLayout.openDrawer(GravityCompat.START);
        });

        Button moreButton = findViewById(R.id.moreButton);
        moreButton.setOnClickListener(V ->{
            // Handle more button click
        });
    }
}