package com.example.chessclock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private EditText editTimeInput;
    private int minutes;
    private int increment;
    private EditText editIncrementInput;
    private Button buttonSet;

    public static final String EXTRA_MINUTES = "com.example.chessclock.EXTRA_MINUTES";
    public static final String EXTRA_INCREMENT = "com.example.chessclock.EXTRA_INCREMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editTimeInput = findViewById(R.id.editTime);
        editIncrementInput = findViewById(R.id.editIncrement);
        buttonSet = findViewById(R.id.buttonSet);

        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = editTimeInput.getText().toString();
                if(time.length() == 0) {
                    Toast.makeText(SettingsActivity.this, "Time cannot be zero", Toast.LENGTH_SHORT).show();
                    return;
                }
                int minutesInput = Integer.parseInt(time);
                if(minutesInput == 0){
                    Toast.makeText(SettingsActivity.this, "Time must be greater than zero", Toast.LENGTH_SHORT).show();
                    return;
                }
                setTime(minutesInput);
                String inc = editIncrementInput.getText().toString();
                if(inc.length() == 0) {
                    setIncrement(0);
                    return;
                }
                int incrementInput = Integer.parseInt(inc);
                if(incrementInput == 0){
                    setIncrement(0);
                }
                setIncrement(incrementInput);
                openMainActivity();
            }
        });

    }

    private void setTime(int m){
        minutes = m;
    }
    private void setIncrement(int inc){
        increment = inc;
    }

    public void openMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_MINUTES, minutes);
        intent.putExtra(EXTRA_INCREMENT, increment);
        startActivity(intent);
    }
}
