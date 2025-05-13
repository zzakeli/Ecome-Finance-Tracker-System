package com.example.ecome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ExpenseCategoryFragment extends Fragment {

    private DatabaseReference ref = Database.fireDB.getReference("users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.expense_category_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String amount = getArguments().getString("amount");

        ImageButton billsCategoryButton = view.findViewById(R.id.billsCategory);
        billsCategoryButton.setOnClickListener(v -> {
            String category = "bills";
            addExpense(category, amount);
        });

        ImageButton foodCategoryButton = view.findViewById(R.id.foodCategory);
        foodCategoryButton.setOnClickListener(v -> {
            String category = "food";
            addExpense(category, amount);
        });

        ImageButton houseCategoryButton = view.findViewById(R.id.houseCategory);
        houseCategoryButton.setOnClickListener(v -> {
            String category = "house";
            addExpense(category, amount);
        });

        ImageButton transportCategoryButton = view.findViewById(R.id.transportCategory);
        transportCategoryButton.setOnClickListener(v -> {
            String category = "transport";
            addExpense(category, amount);
        });

        ImageButton healthCategoryButton = view.findViewById(R.id.healthCategory);
        healthCategoryButton.setOnClickListener(v -> {
            String category = "health";
            addExpense(category, amount);
        });

        ImageButton clothesCategoryButton = view.findViewById(R.id.clothesCategory);
        clothesCategoryButton.setOnClickListener(v -> {
            String category = "clothes";
            addExpense(category, amount);
        });

        ImageButton petsCategoryButton = view.findViewById(R.id.petsCategory);
        petsCategoryButton.setOnClickListener(v -> {
            String category = "pets";
            addExpense(category, amount);
        });

        ImageButton eatingOutCategoryButton = view.findViewById(R.id.eatingOutCategory);
        eatingOutCategoryButton.setOnClickListener(v -> {
            String category = "eating_out";
            addExpense(category, amount);
        });

        ImageButton carCategoryButton = view.findViewById(R.id.carCategory);
        carCategoryButton.setOnClickListener(v -> {
            String category = "car";
            addExpense(category, amount);
        });
    }

    private void addExpense(String category, String amount){
        SharedPreferences prefs = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String userID = prefs.getString("userID", null);

        ref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double totalExpense = snapshot.child("total_expense").getValue(Double.class);
                totalExpense += Double.parseDouble(amount);
                ref.child(userID).child("total_expense").setValue(totalExpense);

                Double balance = snapshot.child("balance").getValue(Double.class);
                balance -= Double.parseDouble(amount);
                ref.child(userID).child("balance").setValue(balance);

                Double categoryDouble = snapshot.child(category).getValue(Double.class);
                categoryDouble += Double.parseDouble(amount);
                ref.child(userID).child(category).setValue(categoryDouble);

                Map<String, Object> updates = new HashMap<>();
                updates.put("total_expense", totalExpense);
                updates.put("balance", balance);
                updates.put(category, categoryDouble);

                ref.child(userID).updateChildren(updates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("refresh", true);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
            }
        });
    }
}
