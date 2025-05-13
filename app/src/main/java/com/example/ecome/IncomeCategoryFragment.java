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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class IncomeCategoryFragment extends Fragment {

    private DatabaseReference ref = Database.fireDB.getReference("users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.income_category_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String amount = getArguments().getString("amount");

        ImageButton depositsCategoryButton = view.findViewById(R.id.depositsCategory);
        depositsCategoryButton.setOnClickListener(v -> {
            String category = "deposits";
            addIncome(category, amount);
        });

        ImageButton salaryCategoryButton = view.findViewById(R.id.salaryCategory);
        salaryCategoryButton.setOnClickListener(v -> {
            String category = "salary";
            addIncome(category, amount);
        });

        ImageButton savingsCategoryButton = view.findViewById(R.id.savingsCategory);
        savingsCategoryButton.setOnClickListener(v -> {
            String category = "savings";
            addIncome(category, amount);
        });
    }

    private void addIncome(String category, String amount){
        SharedPreferences prefs = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String userID = prefs.getString("userID", null);

        ref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double totalIncome = snapshot.child("total_income").getValue(Double.class);
                totalIncome += Double.parseDouble(amount);
                ref.child(userID).child("total_income").setValue(totalIncome);

                Double balance = snapshot.child("balance").getValue(Double.class);
                balance += Double.parseDouble(amount);
                ref.child(userID).child("balance").setValue(balance);

                Double categoryDouble = snapshot.child(category).getValue(Double.class);
                categoryDouble += Double.parseDouble(amount);
                ref.child(userID).child(category).setValue(categoryDouble);

                Map<String, Object> updates = new HashMap<>();
                updates.put("total_income", totalIncome);
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
