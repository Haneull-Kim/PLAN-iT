package com.example.a2021110464_project;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.Date;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Locale;

public class DdayActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_DDAY = 1;
    private static final String PREF_NAME = "DdayPreferences";
    private static final String KEY_DDAY_LIST = "ddayList";
    private static final String CHANNEL_ID = "mychannel";

    private ArrayList<DdayItem> ddayList;
    private DdayAdapter ddayAdapter;
    private ListView ddayListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dday);



        ddayList = loadDdayListFromSharedPreferences();
        ddayAdapter = new DdayAdapter(this, ddayList);
        ddayListView = findViewById(R.id.ddayListView);
        ddayListView.setAdapter(ddayAdapter);

        // 권한을 확인하고 권한이 없는 경우 권한을 요청합니다.
        // checkNotificationPermission();

        // D-day를 확인하고 알림을 보냅니다.
        // checkDdaysAndSendNotifications();

        Button addDdayButton = findViewById(R.id.addDdayButton);
        addDdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDdayAddActivity();
            }
        });

        // 리스트뷰에 롱클릭 리스너 추가
        ddayListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteConfirmationDialog(position);
                return true;
            }
        });


    }




    private void startDdayAddActivity() {
        Intent intent = new Intent(this, DdayAddActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_DDAY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_DDAY && resultCode == RESULT_OK && data != null) {
            DdayItem newDday = data.getParcelableExtra("newDday");

            if (newDday != null) {
                ddayList.add(newDday);
                ddayAdapter.notifyDataSetChanged();
                saveDdayListToSharedPreferences(ddayList);
                Toast.makeText(this, "D-day가 추가되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete D-day");
        builder.setMessage("이 D-day를 삭제하시겠습니까?")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDday(position);
                        Toast.makeText(DdayActivity.this, "D-day가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    private void deleteDday(int position) {
        ddayList.remove(position);
        ddayAdapter.notifyDataSetChanged();
        saveDdayListToSharedPreferences(ddayList);
    }


    private ArrayList<DdayItem> loadDdayListFromSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(KEY_DDAY_LIST, null);

        if (json == null) {
            return new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<DdayItem>>() {
            }.getType();
            return gson.fromJson(json, type);
        }
    }

    private void saveDdayListToSharedPreferences(ArrayList<DdayItem> ddayList) {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(ddayList);
        editor.putString(KEY_DDAY_LIST, json);
        editor.apply();
    }

}
