package com.example.bmrcalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    RadioButton rWoman, rMan;
    TextInputEditText etWeight, etHeight, etAge;
    Button btnCalculate;
    TextView tvResultSum, tvSex, tvReset;
    RadioGroup radioGroup;
    SharedPreferences sharedPreferences;

    private static final String KEY_SEX = "sex";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_AGE = "age";
    private static final String KEY_RESULT = "result";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        tvReset = findViewById(R.id.tvReset);
        tvSex = findViewById(R.id.tvSex);
        rWoman = findViewById(R.id.rWoman);
        rMan = findViewById(R.id.rMan);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etAge = findViewById(R.id.etAge);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvResultSum = findViewById(R.id.tvResultSum);
        radioGroup = findViewById(R.id.radioGroup);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        String sex = sharedPreferences.getString(KEY_SEX, "");
        if (!sex.isEmpty()) {
            if (sex.equals("Mężczyzna")) {
                rMan.setChecked(true);
            } else if (sex.equals("Kobieta")) {
                rWoman.setChecked(true);
            }
        }



        View rootView = findViewById(R.id.frameLayout);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();

                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) {

                } else {

                }
            }
        });



        etAge.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });


        etAge.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });



        etWeight.setText(sharedPreferences.getString(KEY_WEIGHT, ""));
        etHeight.setText(sharedPreferences.getString(KEY_HEIGHT, ""));
        etAge.setText(sharedPreferences.getString(KEY_AGE, ""));
        tvResultSum.setText(sharedPreferences.getString(KEY_RESULT, ""));

        if (!sex.isEmpty()) {

            Drawable icon = getResources().getDrawable(R.drawable.ic_baseline_check_circle_24);
            icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
            tvSex.setCompoundDrawables(null, null, icon, null);
            tvSex.setCompoundDrawablePadding(16);
        } else {
            tvSex.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
        }


        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetUserSettings();
                clearSharedPreferences();
                radioGroup.clearCheck();
            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = findViewById(checkedId);

                if (selectedRadioButton != null && selectedRadioButton.isChecked()) {
                    String output = selectedRadioButton.getText().toString();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_SEX, output);
                    editor.apply();

                    Drawable icon = getResources().getDrawable(R.drawable.ic_baseline_check_circle_24);
                    icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                    tvSex.setCompoundDrawables(null, null, icon, null);
                } else {
                    tvSex.setCompoundDrawables(null, null, null, null);
                }
            }
        });


        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (validateInput()) {

                    if (rWoman.isChecked() || rMan.isChecked()) {

                        calculateBMR();
                    }

                }

            }
        });

    }

    private boolean validateInput() {
        boolean isValid = true;
        String weightText = etWeight.getText().toString();
        String heightText = etHeight.getText().toString();
        String ageText = etAge.getText().toString();
        int selectedId = radioGroup.getCheckedRadioButtonId();

        if (weightText.isEmpty()) {
            etWeight.setError("Wprowadź wagę");
            isValid = false;
        } else {
            float weight = Float.parseFloat(weightText);
            if (weight < 20) {
                etWeight.setError("Waga nie może być mniejsza niż 20 kg");
                isValid = false;
            }
        }

        if (heightText.isEmpty()) {
            etHeight.setError("Wprowadź wzrost");
            isValid = false;
        } else {
            float height = Float.parseFloat(heightText);
            if (height < 100) {
                etHeight.setError("Wzrost nie może być mniejszy niż 100 cm");
                isValid = false;
            }
        }

        if (ageText.isEmpty()) {
            etAge.setError("Wprowadź wiek");
            isValid = false;
        } else {
            int age = Integer.parseInt(ageText);
            if (age < 1) {
                etAge.setError("Wiek nie może być mniejszy niż 1");
                isValid = false;

            }
        }

        if (selectedId == -1) {
            isValid = false;

        }

        return isValid;
    }


    private void calculateBMR() {
        String sex = sharedPreferences.getString(KEY_SEX, "");
        float weight = Float.parseFloat(etWeight.getText().toString());
        float height = Float.parseFloat(etHeight.getText().toString());
        int age = Integer.parseInt(etAge.getText().toString());

        float result;
        if (sex.equals("Mężczyzna")) {
            result = (float) (66.5 + (13.75 * weight) + (5.003 * height) - (6.775 * age));
        } else {
            result = (float) (655.1 + (9.563 * weight) + (1.85 * height) - (4.676 * age));
        }

        String resultText = "" + result + "kcal";

        tvResultSum.setAlpha(0f);
        tvResultSum.setText(resultText);
        tvResultSum.setVisibility(View.VISIBLE);
        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(tvResultSum, "alpha", 0f, 1f);
        fadeInAnimator.setDuration(1000);
        fadeInAnimator.start();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_WEIGHT, etWeight.getText().toString());
        editor.putString(KEY_HEIGHT, etHeight.getText().toString());
        editor.putString(KEY_AGE, etAge.getText().toString());
     //   editor.putString(KEY_SEX, sex);
        editor.putString(KEY_RESULT, tvResultSum.getText().toString());
        editor.apply();
    }

    private void resetUserSettings() {
        rWoman.setChecked(false);
        rMan.setChecked(false);
        etWeight.setText("");
        etHeight.setText("");
        etAge.setText("");
        tvResultSum.setText("");
        tvSex.setCompoundDrawables(null, null, null, null);
    }

    private void clearSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    view.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // Schowaj klawiaturę
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
