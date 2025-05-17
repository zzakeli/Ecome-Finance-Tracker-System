package com.example.ecome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

import java.util.ArrayList;

public class Chart extends AppCompatActivity {

    private DatabaseReference ref = Database.fireDB.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.chart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chart), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        showChart();
    }

    private void showChart(){
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String userID = prefs.getString("userID", null);
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
}
