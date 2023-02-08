package com.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {
    private TextView textViewResult;
    private Button buttonStartNewGame;

    private static String EXTRA_RESULT = "result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        textViewResult = findViewById(R.id.textViewResult);
        buttonStartNewGame = findViewById(R.id.buttonStartNewGame);
        int result = (int) getIntent().getIntExtra(EXTRA_RESULT, 0);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int max = preferences.getInt("max",0);
        String score = String.format("Your result: %s\nMaximum result: %s", result, max);
        textViewResult.setText(score);
        buttonStartNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Quiz.newIntent(ScoreActivity.this);
                startActivity(intent);
                finish();
            }
        });
    }

    public static Intent newIntent(Context context, int result) {
        Intent intent = new Intent(context, ScoreActivity.class);
        intent.putExtra(EXTRA_RESULT, result);
        return intent;
    }

}