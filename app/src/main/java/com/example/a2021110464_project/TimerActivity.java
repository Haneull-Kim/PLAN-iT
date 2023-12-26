package com.example.a2021110464_project;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.TimerTask;

public class TimerActivity extends AppCompatActivity {

    private TextView timerTextView;
    private Button startButton, stopButton, resetButton;
    private Handler handler;
    private java.util.Timer timer;
    private long initialTimeInMillis;
    private long elapsedTimeInMillis;
    private boolean isTimerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        timerTextView = findViewById(R.id.timerTextView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        resetButton = findViewById(R.id.resetButton);

        handler = new Handler();

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        // MainActivity로부터 전달된 시간 값을 받습니다.
        initialTimeInMillis = getIntent().getLongExtra("initialTimeInMillis", 0);

        // 시작 버튼 클릭 이벤트 처리
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTimerRunning) {
                    startTimer();
                }
            }
        });

        // 중지 버튼 클릭 이벤트 처리
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });

        // 리셋 버튼 클릭 이벤트 처리
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }

    // 뒤로가기 버튼 클릭 시 호출되는 메서드
    public void goBack() {
        finish(); // 현재 액티비티 종료
    }

    // 시작 버튼 클릭 시 호출되는 메서드
    private void startTimer() {
        isTimerRunning = true;
        timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                elapsedTimeInMillis += 1000; // 1초씩 증가
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateTimerText();
                    }
                });
            }
        }, 0, 1000); // 1초마다 실행
    }

    // 중지 버튼 클릭 시 호출되는 메서드
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            isTimerRunning = false;
        }
    }

    // 리셋 버튼 클릭 시 호출되는 메서드
    private void resetTimer() {
        stopTimer();
        elapsedTimeInMillis = 0;
        updateTimerText();
    }

    // UI 업데이트를 수행하는 메서드
    private void updateTimerText() {
        timerTextView.setText(formatTime(elapsedTimeInMillis));
    }

    // 밀리초를 시간 형식으로 변환하는 유틸리티 메서드
    private String formatTime(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
