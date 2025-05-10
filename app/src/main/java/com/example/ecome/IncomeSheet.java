package com.example.ecome;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

public class IncomeSheet extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.income_sheet, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();

        Dialog incomeSheetDialog = getDialog();
        if (incomeSheetDialog != null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            incomeSheetDialog.getWindow().setLayout(width, height);
        }
    }
}
