package com.example.myapplication.activity;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.GameView;

public class MainActivity extends AppCompatActivity {

    public static MainActivity mainActivity;

    private TextView Score;
    public static int score = 0;

    public MainActivity() {
        mainActivity = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Score = findViewById(R.id.Score);
        Button restart = findViewById(R.id.restart);
        restart.setOnClickListener(view -> GameView.startGame());

    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public void clearScore() {
        score = 0;
        showScore();
    }

    public void addScore(int i) {

        score += i;
        showScore();

    }

    public void showScore() {
        Score.setText(String.valueOf(score));
    }

    @Override
    public void onBackPressed() {
        createExitTipDialog();
    }

    private void createExitTipDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("Wanna leave?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    finish();
                })
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

}
