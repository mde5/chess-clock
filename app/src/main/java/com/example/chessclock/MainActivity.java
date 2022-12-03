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

    private ChessClock clockA = new ChessClock(DEFAULT_START_TIME);
    private ChessClock clockB = new ChessClock(DEFAULT_START_TIME);

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
        int seconds = intent.getIntExtra(SettingsActivity.EXTRA_SECONDS, 0);
        increment = intent.getIntExtra(SettingsActivity.EXTRA_INCREMENT, 0);
        startTimeInMillis = minutes * 60000 + (seconds * 1000);
        initClocks();

        clockAButton = findViewById(R.id.buttonA);
        clockBButton = findViewById(R.id.buttonB);
        pauseButton = findViewById(R.id.pauseButton);
        settingsButton = findViewById(R.id.settingsButton);
        resetButton = findViewById(R.id.resetButton);

        clockAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameAlreadyPaused)startStop(clockA, clockB, clockAButton, clockBButton);
            }
        });

        clockBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameAlreadyPaused)startStop(clockB, clockA, clockBButton, clockAButton);
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

        updateTimer(clockA, clockAButton);
        updateTimer(clockB, clockBButton);
    }

    public void initClocks(){
        if(startTimeInMillis > 0) {
            clockA.setTimeOnClock(startTimeInMillis);
            clockB.setTimeOnClock(startTimeInMillis);
        }
        else{
            clockA.setTimeOnClock(DEFAULT_START_TIME);
            clockB.setTimeOnClock(DEFAULT_START_TIME);
        }
    }

    public void startStop(ChessClock c1, ChessClock c2, Button button1, Button button2){
        if (c1.isRunning())
            stopClock(c1, button1);
        if(!c2.isRunning() && !c1.isFinished())
            startClock(c2, button2);
    }

    public void startClock(final ChessClock chessClock, final Button button){
        if(chessClock.equals(clockA)) {
            cA = new CountDownTimer(chessClock.getTimeOnClock(), 10) {
                @Override
                public void onTick(long millisUntilFinished) {
                    chessClock.setTimeOnClock(millisUntilFinished);
                    updateTimer(chessClock, button);
                }

                @Override
                public void onFinish() {
                    button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(237, 56, 36)));
                    button.setTextColor(Color.WHITE);
                    chessClock.setFinished(true);
                    stopClock(chessClock, button);
                }
            }.start();
        }
        else if(chessClock.equals(clockB)) {
            cB = new CountDownTimer(chessClock.getTimeOnClock(), 10) {
                @Override
                public void onTick(long millisUntilFinished) {
                    chessClock.setTimeOnClock(millisUntilFinished);
                    updateTimer(chessClock, button);
                }

                @Override
                public void onFinish() {
                    button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(237, 56, 36)));
                    button.setTextColor(Color.WHITE);
                    chessClock.setFinished(true);
                    stopClock(chessClock, button);
                }
            }.start();
        }
            chessClock.setRunning(true);
    }

    public void stopClock(ChessClock chessClock, Button button){
        if(chessClock.equals(clockA)){
           cA.cancel();
        }
        if(chessClock.equals(clockB)){
            cB.cancel();
        }
        chessClock.setRunning(false);
        if(!gamePaused) {
            if(!chessClock.isFinished())
                chessClock.setTimeOnClock(chessClock.getTimeOnClock() + (increment * 1000));
            updateTimer(chessClock, button);
        }
    }

    public void updateTimer(ChessClock chessClock, Button button){
        int minutes = (int) chessClock.getTimeOnClock() / 60000;
        int seconds = (int) chessClock.getTimeOnClock() % 60000 / 1000;
        int milliseconds = (int) chessClock.getTimeOnClock() % 1000 / 100;
        String timeText = "" + minutes;
        timeText += ":";
        if (seconds < 10)
            timeText += "0";
        timeText += seconds;
        if(minutes < 1 && seconds < 10){
            timeText += ".";
            timeText += milliseconds;
        }

        button.setText(timeText);
    }

    public void pauseClocks(){
        gamePaused = true;
        //stores which clock is running when the game is paused
        if(clockA.isRunning())
            whoPaused = 'A';
        if(clockB.isRunning())
            whoPaused = 'B';

        if(gameAlreadyPaused){
            unPauseClocks();
        }
        //Pauses the game if: 1. game is not already paused 2. A clock is running when the pause button is pressed
        if(whoPaused != '\u0000') {
            if(clockA.isRunning())
                stopClock(clockA, clockAButton);
            if(clockB.isRunning())
                stopClock(clockB, clockBButton);
            gameAlreadyPaused = true;
        }
    }

    public void unPauseClocks(){
        if(whoPaused == 'A')
            startStop(clockB, clockA, clockBButton, clockAButton);
        if(whoPaused == 'B')
            startStop(clockA, clockB, clockAButton, clockBButton);

        whoPaused = '\u0000';

        gameAlreadyPaused = false;
        gamePaused = false;
    }

    public void resetClocks(){
        if(clockA.isRunning()) {
            stopClock(clockA, clockAButton);
            initClocks();
        }
        if(clockB.isRunning()) {
            stopClock(clockB, clockBButton);
            initClocks();
        }
        if(clockA.isFinished() || clockB.isFinished()){
            initClocks();
        }
        //Special case: User resets game while game already paused; i.e. above conditions above will fail since neither clock is running or finished;
        if(gameAlreadyPaused){
            initClocks();
            whoPaused = '\u0000';
            unPauseClocks();
        }
        clockA.setFinished(false);
        clockB.setFinished(false);
        updateTimer(clockA, clockAButton);
        updateTimer(clockB, clockBButton);
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




