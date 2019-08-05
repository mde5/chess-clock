package com.example.chessclock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.os.CountDownTimer;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private static final long DEFAULT_START_TIME = 300000;

    private Button clockAButton;
    private Button clockBButton;
    private ImageButton pauseButton;
    private ImageButton settingsButton;
    private ImageButton resetButton;
    private CountDownTimer cA;
    private CountDownTimer cB;
    private long startTimeInMillis = DEFAULT_START_TIME;
    private int increment = 0;
    private long timeLeftA = DEFAULT_START_TIME;
    private long timeLeftB = DEFAULT_START_TIME;
    private boolean isRunningA;
    private boolean isRunningB;
    private boolean isFinishedA;
    private boolean isFinishedB;
    private boolean gamePaused = false;
    private boolean gameAlreadyPaused = false;
    char whoPaused = '\u0000';

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        int minutes = intent.getIntExtra(SettingsActivity.EXTRA_MINUTES, 0);
        increment = intent.getIntExtra(SettingsActivity.EXTRA_INCREMENT, 0);
        startTimeInMillis = minutes * 60000;
        initClocks();

        clockAButton = findViewById(R.id.buttonA);
        clockBButton = findViewById(R.id.buttonB);
        pauseButton = findViewById(R.id.pauseButton);
        settingsButton = findViewById(R.id.settingsButton);
        resetButton = findViewById(R.id.resetButton);

        clockAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameAlreadyPaused)startStopB();
            }
        });

        clockBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameAlreadyPaused)startStopA();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseClocks();
                if(gamePaused && (whoPaused != '\u0000'))
                    pauseButton.setSelected(true);
                else
                    pauseButton.setSelected(false);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsActivity();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetClocks();
            }
        });

        updateTimerA();
        updateTimerB();
    }

    public void initClocks(){
        if(startTimeInMillis > 0) {
            timeLeftA = startTimeInMillis;
            timeLeftB = startTimeInMillis;
        }
        else{
            timeLeftA = DEFAULT_START_TIME;
            timeLeftB = DEFAULT_START_TIME;
        }
    }

    public void startStopB(){
        if (isRunningA)
            stopClockA();
        if(!isRunningB && !isFinishedA)
            startClockB();
    }

    public void startStopA(){
        if(isRunningB)
            stopClockB();
        if(!isRunningA && !isFinishedB)
            startClockA();
    }

    public void startClockB(){
        cB = new CountDownTimer(timeLeftB, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftB = millisUntilFinished;
                updateTimerB();
            }

            @Override
            public void onFinish() {
                clockBButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(237, 56, 36)));
                clockBButton.setTextColor(Color.WHITE);
                isFinishedB = true;
                stopClockB();
            }
        }.start();

        isRunningB = true;
    }

    public void stopClockA(){
        cA.cancel();
        isRunningA = false;
        if(!gamePaused) {
            if(!isFinishedA)
                timeLeftA += (increment * 1000);
            updateTimerA();
        }
    }

    public void startClockA(){
        cA = new CountDownTimer(timeLeftA, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftA = millisUntilFinished;
                updateTimerA();
            }

            @Override
            public void onFinish() {
                clockAButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(237, 56, 36)));
                isFinishedA = true;
                stopClockA();
            }
        }.start();

        isRunningA = true;
    }

    public void stopClockB(){
        cB.cancel();
        isRunningB = false;
        if(!gamePaused) {
            if(!isFinishedB)
                timeLeftB += (increment * 1000);
            updateTimerB();
        }
    }

    public void updateTimerB(){
        int minutes = (int) timeLeftB / 60000;
        int seconds = (int) timeLeftB % 60000 / 1000;
        int milliseconds = (int) timeLeftB % 1000 / 100;
        String timeText = "" + minutes;
        timeText += ":";
        if (seconds < 10)
            timeText += "0";
        timeText += seconds;
        if(minutes < 1 && seconds < 10){
            timeText += ".";
            timeText += milliseconds;
        }

        clockBButton.setText(timeText);
    }
    public void updateTimerA(){
        int minutes = (int) timeLeftA / 60000;
        int seconds = (int) timeLeftA % 60000 / 1000;
        int milliseconds = (int) timeLeftA % 1000 / 100;
        String timeText = "" + minutes;
        timeText += ":";
        if (seconds < 10)
            timeText += "0";
        timeText += seconds;
        if(minutes < 1 && seconds < 10){
            timeText += ".";
            timeText += milliseconds;
        }


        clockAButton.setText(timeText);
    }

    public void pauseClocks(){
            gamePaused = true;
        //stores which clock is running when the game is paused
        if(isRunningA)
            whoPaused = 'A';
        if(isRunningB)
            whoPaused = 'B';

        if(gameAlreadyPaused){
            unPauseClocks();
        }

        //Pauses the game if: 1. game is not already paused 2. A clock is running when the pause button is pressed
        if(whoPaused != '\u0000') {
            if(isRunningA)
                stopClockA();
            if(isRunningB)
                stopClockB();
            gameAlreadyPaused = true;
        }
    }

    public void unPauseClocks(){
        if(whoPaused == 'A')
            startStopA();
        if(whoPaused == 'B')
            startStopB();

        whoPaused = '\u0000';

        gameAlreadyPaused = false;
        gamePaused = false;
    }

    public void resetClocks(){
        if(isRunningA) {
            stopClockA();
            initClocks();
        }
        if(isRunningB) {
            stopClockB();
            initClocks();
        }
        if(isFinishedA || isFinishedB){
            initClocks();
        }
        //Special case: User resets game while game already paused; i.e. above conditions above will fail since neither clock is running or finished;
        if(gameAlreadyPaused){
            initClocks();
            whoPaused = '\u0000';
            unPauseClocks();
        }
        isFinishedA = false;
        isFinishedB = false;
        updateTimerA();
        updateTimerB();
        clockAButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(36, 156, 237)));
        clockBButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(214, 215, 215)));
        clockBButton.setTextColor(Color.BLACK);
        pauseButton.setSelected(false);
    }

    public void openSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}




