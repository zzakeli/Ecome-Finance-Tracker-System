package com.example.ecome;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ExpenseSheet extends DialogFragment {


    private DatabaseReference ref = Database.fireDB.getReference("users");
    private LinearLayout expenseSheetContainer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.expense_sheet, container, false);

        expenseSheetContainer = view.findViewById(R.id.expenseSheetContainer);
        loadExpenseRecord();

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();

        Dialog expenseSheetDialog = getDialog();
        if (expenseSheetDialog != null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            expenseSheetDialog.getWindow().setLayout(width, height);
        }
    }

    private void loadExpenseRecord(){
        SharedPreferences prefs = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String userID = prefs.getString("userID", null);

        ref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String[] expenseCategory = {
                        "bills", "food", "house",
                        "transport", "health", "clothes",
                        "pets", "eating_out", "car"};
                for(int i = 0; i < expenseCategory.length; i++){
                    Double categoryValue = snapshot.child(expenseCategory[i]).getValue(Double.class);
                    if(categoryValue != 0.00 && categoryValue != null){
                        LinearLayout record = getRecord();
                        record.addView(getCategoryLabel(expenseCategory[i]));
                        record.addView(getCategoryAmount(categoryValue));
                        expenseSheetContainer.addView(record);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
            }
        });
    }

    private LinearLayout getRecord(){
        LinearLayout record = new LinearLayout(getContext());

        // STANDARD SIZING CONVERSION FROM DP TO PX
        int heightInDp = 50;
        float scale = getResources().getDisplayMetrics().density;
        int heightInPx = (int) (heightInDp * scale + 0.5f);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightInPx);

        int marginInDp = 10;
        int marginInPx = (int) (marginInDp * scale + 0.5f);
        params.setMargins(0,0,0,marginInPx);

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.choose_category_red);
        record.setBackground(drawable);
        record.setOrientation(LinearLayout.HORIZONTAL);

        record.setLayoutParams(params);
        return record;
    }
    private TextView getCategoryLabel(String expenseCategory) {
        TextView categoryLabel = new TextView(getContext());

        categoryLabel.setText(Character.toString(expenseCategory.charAt(0)).toUpperCase() + expenseCategory.substring(1, expenseCategory.length()).replace("_",  " "));

        int widthInDp = 210;
        float scale = getResources().getDisplayMetrics().density;
        int widthInPx = (int) (widthInDp * scale + 0.5f);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthInPx,ViewGroup.LayoutParams.WRAP_CONTENT);

        params.gravity = Gravity.CENTER_VERTICAL;
        int marginInDp = 10;
        int marginInPx = (int) (marginInDp * scale + 0.5f);
        params.setMargins(marginInPx,0,0,0);

        categoryLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        categoryLabel.setTextColor(Color.parseColor("#FFFFFFFF"));

        categoryLabel.setLayoutParams(params);
        return categoryLabel;
    }

    private TextView getCategoryAmount(Double categoryValue) {
        TextView categoryAmount = new TextView(getContext());

        categoryAmount.setText("P" + Double.toString(categoryValue));

        categoryAmount.setGravity(Gravity.END);
        categoryAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        categoryAmount.setTextColor(Color.parseColor("#FFFFFFFF"));

        int heightInDp = 30;
        int widthInDp = 100;
        float scale = getResources().getDisplayMetrics().density;
        int heightInPx = (int) (heightInDp * scale + 0.5f);
        int widthInPx = (int) (widthInDp * scale + 0.5f);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthInPx,heightInPx);
        params.gravity = Gravity.CENTER_VERTICAL;

        categoryAmount.setLayoutParams(params);
        return categoryAmount;
    }
}
