package com.example.ecome;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class AddExpense extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_expense_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_expense_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setUpButtons();
    }

    private void setUpButtons(){
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->{
            // Handle back button click
            finish();
        });

        Button chooseExpenseCategoryButton = findViewById(R.id.chooseExpenseCategoryButton);
        chooseExpenseCategoryButton.setOnClickListener(v -> {
            // Handle choose expense category button click
            ExpenseCategoryFragment fragment = new ExpenseCategoryFragment();
            Bundle bundle = new Bundle();
            bundle.putString("amount", getAmount());
            fragment.setArguments(bundle);

            loadFragment(fragment);
        });
    }

    private void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.expense_category_frame, fragment)
                .commit();
    }

    protected String getAmount(){
        TextView amountText = findViewById(R.id.expenseAmount);
        return amountText.getText().toString();
    }
}
