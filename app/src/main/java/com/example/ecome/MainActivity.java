package com.example.ecome;

import android.net.Uri;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        setUpMidButton();

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

    interface OnDataReadyCallback {
        void onDataReady(List<String[]> data);
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
        ImageButton incomeSheetButton = findViewById(R.id.incomeSheetButton);
        incomeSheetButton.setOnClickListener(v -> {
            // Handles the recycle view from database
            new IncomeSheet().show(getSupportFragmentManager(), "IncomeSheetDialog");
        });

        ImageButton expenseSheetButton = findViewById(R.id.expenseSheetButton);
        expenseSheetButton.setOnClickListener(v -> {
            // Handles the recycle view from database
            new ExpenseSheet().show(getSupportFragmentManager(), "ExpenseSheetDialog");
        });
    }

    private void setUpUpperButtons(){
        DrawerLayout drawerLayout = findViewById(R.id.date_drawer);
        ImageButton burgerButton = findViewById(R.id.burgerButton);
        burgerButton.setOnClickListener(v -> {
            // Handle burger button click
            drawerLayout.openDrawer(GravityCompat.START);
        });
    }

    private void setUpMidButton(){
        ImageButton chartButton = findViewById(R.id.chartButton);
        chartButton.setOnClickListener(v -> {
            // Handle chart button click
//            showChart();
            Intent intent = new Intent(MainActivity.this, Chart.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        ImageButton exportButton = findViewById(R.id.exportButton);
        exportButton.setOnClickListener(v -> {
           getData(data -> exportData(this, data));
        });
    }

    private void showChart(){
        //SHOWS CHART
        ref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //GENERAL BALANCE
//                Double balance = snapshot.child("balance").getValue(Double.class);
//                Double totalIncome = snapshot.child("total_income").getValue(Double.class);
//                Double totalExpense = snapshot.child("total_expense").getValue(Double.class);

                //INCOME
                Double deposits = snapshot.child("deposits").getValue(Double.class);
                Double salary = snapshot.child("salary").getValue(Double.class);
                Double savings = snapshot.child("savings").getValue(Double.class);

                //EXPENSE
                Double bills = snapshot.child("bills").getValue(Double.class);
                Double food = snapshot.child("food").getValue(Double.class);
                Double house = snapshot.child("house").getValue(Double.class);
                Double transport = snapshot.child("transport").getValue(Double.class);
                Double health = snapshot.child("health").getValue(Double.class);
                Double clothes = snapshot.child("clothes").getValue(Double.class);
                Double pets = snapshot.child("pets").getValue(Double.class);
                Double eatingOut = snapshot.child("eating_out").getValue(Double.class);

                //CREATING BAR CHART
                String[] attributes = {
                        "deposits", "salary", "savings", "bills",
                        "food", "house", "transport", "health",
                        "clothes", "pets", "eating_out"
                };

                ArrayList<BarEntry> entries = new ArrayList<>();
                Double[] values = {
                        deposits, salary, savings, bills,
                        food, house, transport, health,
                        clothes, pets, eatingOut
                };

                BarChart barChart = findViewById(R.id.barChart);
                Float[] valuesFloat = new Float[values.length];

                for(int i = 0; i < values.length; i++){
                    valuesFloat[i] = values[i].floatValue();
                }

                for(int i = 0; i < valuesFloat.length; i++){
                    entries.add(new BarEntry((float) i, valuesFloat[i]));
                }

                XAxis xAxis = barChart.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(attributes));

                BarDataSet set = new BarDataSet(entries, "Cash Flow");

                BarData barData = new BarData(set);
                barData.setBarWidth(0.9f);

                barChart.setData(barData);
                barChart.setFitBars(true);
                barChart.getDescription().setText("Cash Flow");
                barChart.animateY(1000);
//              barchart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
            }
        });
    }
    
    private  void getData(OnDataReadyCallback callback){
        List<String[]> data = new ArrayList<>();

        ref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //GENERAL BALANCE
                Double balance = snapshot.child("balance").getValue(Double.class);
                Double totalIncome = snapshot.child("total_income").getValue(Double.class);
                Double totalExpense = snapshot.child("total_expense").getValue(Double.class);

                //INCOME
                Double deposits = snapshot.child("deposits").getValue(Double.class);
                Double salary = snapshot.child("salary").getValue(Double.class);
                Double savings = snapshot.child("savings").getValue(Double.class);

                //EXPENSE
                Double bills = snapshot.child("bills").getValue(Double.class);
                Double food = snapshot.child("food").getValue(Double.class);
                Double house = snapshot.child("house").getValue(Double.class);
                Double transport = snapshot.child("transport").getValue(Double.class);
                Double health = snapshot.child("health").getValue(Double.class);
                Double clothes = snapshot.child("clothes").getValue(Double.class);
                Double pets = snapshot.child("pets").getValue(Double.class);
                Double eatingOut = snapshot.child("eating_out").getValue(Double.class);

                String[] attributes = {
                        "balance", "total_income", "total_expense",
                        "deposits", "salary", "savings", "bills",
                        "food", "house", "transport", "health",
                        "clothes", "pets", "eating_out"
                };
                Double[] values = {
                        balance, totalIncome, totalExpense,
                        deposits, salary, savings, bills,
                        food, house, transport, health,
                        clothes, pets, eatingOut
                };

                String[] valuesString = new String[values.length];

                for(int i = 0; i < values.length; i++){
                    valuesString[i] =  values[i].toString();
                }

                data.add(attributes);
                data.add(valuesString);

                callback.onDataReady(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
            }
        });
    }

    private void exportData(Context context, List<String[]> data){
        StringBuilder csvBuilder = new StringBuilder();
        for (String[] row : data) {
            csvBuilder.append(TextUtils.join(",", row));
            csvBuilder.append("\n");
        }

        try {
            String fileName = "finance_data.csv";

            // Save to Downloads folder (scoped storage - Android 10+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                ContentResolver resolver = context.getContentResolver();
                Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

                if (uri != null) {
                    try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                        outputStream.write(csvBuilder.toString().getBytes());
                        Toast.makeText(context, "CSV exported to Downloads", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                // For older versions
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(dir, fileName);

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(csvBuilder.toString().getBytes());
                    Toast.makeText(context, "CSV exported to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected String getUserID(){
        return userID;
    }
}