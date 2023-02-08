package com.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class Quiz extends AppCompatActivity {

    String[] questions;
    int[] answers;
    List<Integer> list = new ArrayList<>();

    String[][] option = {
            {"balance","jumping rope","marathon","skiing"}  ,
            {"three","one","two","four"}  ,
            {"10, 20, 50, 100 m","25, 35, 45, 65 m","100, 200,400 m","50, 100, 350, 400 m"}  ,
            {"2-3 hours","30-45 minutes","1-2 hours","30-60 minutes"}  ,
            {"short distance running","long distance running","middle distance running","running in dashes"}  ,
            {"horse racing","running","jumping","maintenance"}  ,
            {"1-2 m","2.5-3 m","10-15 m","6-8 m"}  ,
            {"bobsleigh","weightlifting","badminton","boxing"}  ,
            {"Charles de Gaulle","Pierre de Coubertin","Lee Hee-beom","Vanderlei Cordeiro"}  ,
            {"25","10","30","20"}  ,
            {"Australia","Mexico","America","Portugal"}  ,
            {"start, push, swing, finish","takeoff, push, flight, landing","race, flight, run-up, deceleration","start, push, race, finish"}  ,
            {"five","ten","eight","six"}  ,
            {"4","3","2","1"}  ,
            {"long distance running","middle distance running","running with dashes","short distance running"}  ,
            {"Onomastos","Zeus","Spyridon Louis","Koreb from Elida"}  ,
            {"figure skating, jumping rope","sprint, throwing, jumping","rope climbing, marathon","skiing, running"}  ,
            {"reduction of motor activity","excess nutrition","lack of vitamin and mineral complex in the body","strengthening of motor reaction"}  ,
            {"flexibility of the torso","muscle training","correct foot position, even breathing","a certain body posture"}  ,
            {"the constant presence of the body in a certain position","lifting weights","poor physical fitness","muscle weakness"}  ,
            {"4x5 m","9x18 m","10x15 m","8x12 m"}  ,
            {"shuttle run","short distance running","long distance running","barrier running"}  ,
            {"keeping the ball with your hands","throwing the ball under the ring","throwing the ball into the basket with your foot","throwing the ball into the ring"}  ,
            {"endurance","dexterity","motor skills","speed"}  ,
            {"long-term disability","periodic deterioration of physical indicators","temporary loss of working capacity","permanent functional failure of the body"}  ,
            {"10 seconds","15 seconds","18 seconds","24 seconds"}  ,
            {"raw soil","smooth treadmill","slippery alley","sandy trail"}  ,
            {"4","3","5","2"}  ,
            {"6","5","7","8"}  ,
            {"bacteriosis","vitamin deficiency","acidosis","helminthiasis"}  ,
            {"Olympic Judges","winners of the Olympiad","population of Olympia","participants of the school Olympiad in physical education"}  ,
            {"long","short","crosses","average"}
    };

    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewRight;
    private TextView textViewOpinion0;
    private TextView textViewOpinion1;
    private TextView textViewOpinion2;
    private TextView textViewOpinion3;
    private ArrayList<TextView> options = new ArrayList<>();
    private boolean gameOver = false;

    private int max;
    private int getRightAnswerPosition;
    private int answeredQuestion = 0;
    private int countOfQuestions = 15;
    private int countOfRightAnswers = 0;
    private int clicked;
    private String score;

    private static String EXTRA_RESULT = "result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        initViews();
        options.add(textViewOpinion0);
        options.add(textViewOpinion1);
        options.add(textViewOpinion2);
        options.add(textViewOpinion3);
        questions = getResources().getStringArray(R.array.questions);
        answers = getResources().getIntArray(R.array.answers);
        score = String.format("%s / %s", answeredQuestion, countOfQuestions);
        textViewScore.setText(score);
        textViewRight.setText(String.valueOf(countOfRightAnswers));
        generateQuestion();
        textViewOpinion0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = 0;
                playGame();
            }
        });
        textViewOpinion1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = 1;
                playGame();
            }
        });
        textViewOpinion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = 2;
                playGame();
            }
        });
        textViewOpinion3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = 3;
                playGame();
            }
        });

    }

    private void playGame() {
        if(textViewOpinion0.isPressed() || textViewOpinion1.isPressed() ||textViewOpinion2.isPressed() ||textViewOpinion3.isPressed()){
            if (clicked == getRightAnswerPosition) {
                countOfRightAnswers++;
                textViewRight.setText(String.valueOf(countOfRightAnswers));
                Toast.makeText(this, "Right", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Incorrectly", Toast.LENGTH_SHORT).show();
            }
        }
        answeredQuestion++;
        score = String.format("%s / %s", answeredQuestion, countOfQuestions);
        textViewScore.setText(score);
        if (answeredQuestion == 15){
            gameOver();
        }else {
            generateQuestion();
        }
    }

    private void generateQuestion() {
        boolean repeat=false;
        int a = (int) (Math.random() * questions.length);
        Log.d("asas",String.valueOf(a));
        for (Integer lists: list){
            if(lists == a){
                repeat = true;
            }
        }
        if (!repeat) {
            list.add(a);
            textViewQuestion.setText(questions[a]);
            getRightAnswerPosition = answers[a];
            textViewOpinion0.setText(option[a][0]);
            textViewOpinion1.setText(option[a][1]);
            textViewOpinion2.setText(option[a][2]);
            textViewOpinion3.setText(option[a][3]);
        } else {
            generateQuestion();
        }
    }

    private void gameOver(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int max = preferences.getInt("max", 0);
        if (countOfRightAnswers >= max) {
            preferences.edit().putInt("max", countOfRightAnswers).apply();
        }
        Intent intent = ScoreActivity.newIntent(Quiz.this, countOfRightAnswers);
        intent.putExtra(EXTRA_RESULT, countOfRightAnswers);
        startActivity(intent);
        finish();
    }

    private void initViews() {
        textViewOpinion0 = findViewById(R.id.textViewOpinion0);
        textViewOpinion1 = findViewById(R.id.textViewOpinion1);
        textViewOpinion2 = findViewById(R.id.textViewOpinion2);
        textViewOpinion3 = findViewById(R.id.textViewOpinion3);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewRight = findViewById(R.id.textViewRight);
        textViewScore = findViewById(R.id.textViewScore);
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, Quiz.class);
        return intent;
    }
}