package com.example.a2021110464_project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
//import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
//import com.prolificinteractive.materialcalendarview.CalendarDay;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "DdayPreferences";
    private static final String KEY_DDAY_LIST = "ddayList";

    String NOTIFICATION_CHANNEL_ID = "my_channel";

    //private MaterialCalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        createNotificationChannel();

        // MaterialCalendarView 참조
        // calendarView = findViewById(R.id.calendarview);
        //if (calendarView != null) {
        //     calendarView.setSelectedDate(CalendarDay.today());
        // }

        Button timerButton = findViewById(R.id.timerButton);
        timerButton.setOnClickListener(v -> {
            // TimerActivity로 이동하는 메서드 호출
            startTimerActivity();
        });

        Button checkListButton = findViewById(R.id.CheckListButton);
        checkListButton.setOnClickListener(v -> {
            // CheckListActivity로 이동하는 메서드 호출
            startCheckListActivity();
        });

        Button dDayButton = findViewById(R.id.DdayButton);
        dDayButton.setOnClickListener(v -> {
            sendDdayListNotification();
            // DDayActivity로 이동
            startDDayActivity();

        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Channel description");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void sendDdayListNotification() {
        ArrayList<DdayItem> ddayList = loadDdayListFromSharedPreferences();

        StringBuilder notificationText = new StringBuilder();

        for (DdayItem ddayItem : ddayList) {
            notificationText.append(getFormattedDday(ddayItem.getDdayDate())).append(" ").append(ddayItem.getDdayName()).append("\n");
        }

        sendNotification("D-day", notificationText.toString());
    }

    private String getFormattedDday(Date ddayDate) {
        // 현재 날짜를 구합니다.
        Calendar today = Calendar.getInstance();

        // D-day 날짜를 Calendar 객체로 변환합니다.
        Calendar ddayCalendar = Calendar.getInstance();
        ddayCalendar.setTime(ddayDate);

        // 두 날짜가 같은 날짜인지 확인합니다.
        if (isSameDay(today, ddayCalendar)) {
            return "D-Day";
        } else if (today.after(ddayCalendar)) {
            // D-day가 이미 지난 경우
            long diffMillis = today.getTimeInMillis() - ddayCalendar.getTimeInMillis();
            long diffDays = (diffMillis / (24 * 60 * 60 * 1000));

            return "D+" + (diffDays); // 이미 지난 경우 D+숫자 형식으로 표시
        } else {
            // D-day까지 남은 날짜를 구합니다.
            long diffMillis = ddayCalendar.getTimeInMillis() - today.getTimeInMillis();
            long diffDays = (diffMillis / (24 * 60 * 60 * 1000));

            return "D-" + (diffDays + 1); // D-day까지 남은 경우 D-n 형식으로 표시
        }
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private ArrayList<DdayItem> loadDdayListFromSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(KEY_DDAY_LIST, null);

        if (json == null) {
            return new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<DdayItem>>() {}.getType();
            return gson.fromJson(json, type);
        }
    }

    private void sendNotification(String title, String content) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        Intent intent = new Intent(this, DdayActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        notificationBuilder.setSmallIcon(android.R.drawable.ic_dialog_info);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(content);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }


    private void startTimerActivity() {
        Intent intent = new Intent(MainActivity.this, TimerActivity.class);
        long initialTimeInMillis = 10 * 60 * 1000; // 예: 10분
        intent.putExtra("initialTimeInMillis", initialTimeInMillis);
        startActivity(intent);
    }

    // CheckListActivity로 이동하는 메서드
    private void startCheckListActivity() {
        Intent intent = new Intent(MainActivity.this, CheckListActivity.class);
        startActivity(intent);
    }

    // DDayActivity로 이동하는 메서드
    private void startDDayActivity() {
        Intent intent = new Intent(MainActivity.this, DdayActivity.class);
        startActivity(intent);
    }

}
