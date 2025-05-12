package com.example.ecome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference ref = Database.fireDB.getReference("users");
    private String userID;

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
        setUpSheets();
        setUpMainButton();

        createNewUser();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (getIntent().getBooleanExtra("refresh", false)) {
            retrieveData();
        }
    }

    private void createNewUser(){
        Map<String, Object> account = new HashMap<>();
        String[] attributes = {
                "balance", "total_income",
                "total_expense", "deposits",
                "salary", "savings", "bills",
                "food", "house", "transport",
                "health", "clothes", "pets",
                "eating_out", "car"
        };

        for(int i = 0; i < attributes.length; i++){
            account.put(attributes[i], 0.00);
        }

        DatabaseReference newUserRef = ref.push();
        userID = newUserRef.getKey();
        newUserRef.setValue(account);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        prefs.edit().putString("userID", userID).apply();

        retrieveData();
    }

    protected void retrieveData(){
        System.out.println(userID);
        ref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double balance = snapshot.child("balance").getValue(Double.class);
                Double totalIncome = snapshot.child("total_income").getValue(Double.class);
                Double totalExpense = snapshot.child("total_expense").getValue(Double.class);

                TextView balanceText = findViewById(R.id.balanceText);
                TextView totalIncomeText = findViewById(R.id.totalIncomeText);
                TextView totalExpenseText = findViewById(R.id.totalExpenseText);

                Double[] mainAttributes = {balance, totalIncome, totalExpense};
                TextView[] textViews = {balanceText, totalIncomeText, totalExpenseText};
                for(int i = 0; i < mainAttributes.length; i++){
                    StringBuilder sb = new StringBuilder();
                    sb.append("P").append(String.format("%.2f",mainAttributes[i]));
                    textViews[i].setText(sb.toString());
                    System.out.println(textViews[i].getText().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
            }
        });
    }

    private void setUpMainButton(){
        Button expenseButton = findViewById(R.id.expenseButton);
        expenseButton.setOnClickListener(v ->{
            // Handle expense button click
            Intent intent = new Intent(MainActivity.this, AddExpense.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        Button incomeButton = findViewById(R.id.incomeButton);
        incomeButton.setOnClickListener(v ->{
            // Handle income button click
            Intent intent = new Intent(MainActivity.this, AddIncome.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    private void setUpSheets(){
        Button incomeSheetButton = findViewById(R.id.incomeSheetButton);
        incomeSheetButton.setOnClickListener(v -> {
            // Handles the recycle view from database
            new IncomeSheet().show(getSupportFragmentManager(), "IncomeSheetDialog");
        });

        Button expenseSheetButton = findViewById(R.id.expenseSheetButton);
        expenseSheetButton.setOnClickListener(v -> {
            // Handles the recycle view from database
            new ExpenseSheet().show(getSupportFragmentManager(), "ExpenseSheetDialog");
        });
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

    protected String getUserID(){
        return userID;
    }
}