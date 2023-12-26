package com.example.a2021110464_project;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CheckListActivity extends AppCompatActivity {

    private ListView checklistListView;
    private Button addChecklistButton;
    private ArrayAdapter<String> checklistAdapter;
    private ArrayList<String> checklistItems;
    private TextView todayDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        // XML에서 정의한 뷰들을 변수에 연결
        checklistListView = findViewById(R.id.checklistListView);
        addChecklistButton = findViewById(R.id.addChecklistButton);
        todayDateTextView = findViewById(R.id.DateTextView);

        // 뒤로가기 버튼에 리스너 설정
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(); // 뒤로가기 메서드 호출
            }
        });

        // 현재 날짜에 해당하는 체크리스트 아이템들을 불러와 리스트뷰에 표시
        checklistItems = loadChecklistItems(getFormattedDate());
        checklistAdapter = new ArrayAdapter<>(this, R.layout.checklist_item_layout, R.id.checklistItemTextView, checklistItems);
        checklistListView.setAdapter(checklistAdapter);

        // 리스트뷰의 각 아이템 클릭 이벤트 설정
        checklistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 아이템 클릭 시 처리할 내용 작성 (현재는 주석처리)
                // Handle item click if needed
            }
        });

        // 리스트뷰의 각 아이템을 길게 눌렀을 때 이벤트 설정
        checklistListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position); // 아이템 삭제 다이얼로그 표시
                return true;
            }
        });

        // 체크리스트 추가 버튼 클릭 시, ChecklistAddActivity를 시작
        addChecklistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckListActivity.this, ChecklistAddActivity.class);
                startActivityForResult(intent, 2); // ChecklistAddActivity 호출
            }
        });

        // 날짜를 표시하는 텍스트뷰 클릭 시, 날짜 선택 다이얼로그 표시
        todayDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // 현재 날짜로 업데이트
        updateDate(getFormattedDate());
    }

    // 날짜 업데이트 메서드
    private void updateDate(String selectedDate) {
        todayDateTextView.setText(selectedDate);
        checklistItems = loadChecklistItems(selectedDate);
        checklistAdapter.clear();
        checklistAdapter.addAll(checklistItems);
        checklistAdapter.notifyDataSetChanged();
    }

    // 날짜 선택 다이얼로그 표시 메서드
    private void showDatePickerDialog() {
        // 현재 날짜로 초기화된 DatePickerDialog 생성
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // 선택된 날짜로 업데이트
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, monthOfYear);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                        String selectedDateString = dateFormat.format(selectedDate.getTime());

                        updateDate(selectedDateString);
                    }
                },
                year, month, day
        );

        datePickerDialog.show(); // DatePickerDialog 표시
    }

    // 날짜에 해당하는 체크리스트 아이템들을 불러오는 메서드
    private ArrayList<String> loadChecklistItems(String date) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        String savedItems = sharedPreferences.getString(date, "");
        ArrayList<String> items = new ArrayList<>();
        if (!savedItems.isEmpty()) {
            // 저장된 문자열을 파싱하여 리스트에 추가
            String[] itemArray = savedItems.substring(1, savedItems.length() - 1).split(", ");
            for (String item : itemArray) {
                items.add(item);
            }
        }
        return items;
    }

    // 체크리스트 아이템들을 저장하는 메서드
    private void saveChecklistItems(String date, ArrayList<String> items) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(date, items.toString());
        editor.apply();
    }

    // 아이템 삭제 다이얼로그 표시 메서드
    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete CheckList");
        builder.setMessage("이 체크리스트를 삭제하시겠습니까?");
        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 아이템 삭제
                deleteChecklistItem(position);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    // 아이템 삭제 메서드
    private void deleteChecklistItem(int position) {
        if (checklistItems.size() > 1) {
            Log.d("DeleteChecklist", "Before deletion: " + checklistItems.toString());

            // 리스트뷰에서 해당 아이템 삭제
            checklistAdapter.remove(checklistItems.get(position));

            // 데이터 갱신 후 저장
            checklistItems.remove(position);
            saveChecklistItems(todayDateTextView.getText().toString(), checklistItems);

            Log.d("DeleteChecklist", "After deletion: " + checklistItems.toString());

            // 리스트뷰 갱신
            checklistAdapter.notifyDataSetChanged();

            updateDate(todayDateTextView.getText().toString());
        } else {
            // 최소 한 개의 아이템은 남겨두어야 하는 메시지 등을 처리할 수 있습니다.
            Toast.makeText(this, "최소 한 개의 아이템은 남겨두어야 합니다.", Toast.LENGTH_SHORT).show();
        }
    }




    // 다른 액티비티에서 결과를 받는 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // ChecklistAddActivity에서 새로운 아이템을 추가한 경우
        if (requestCode == 2 && resultCode == RESULT_OK) {
            String newChecklistItem = data.getStringExtra("newChecklistItem");
            addChecklistItem(newChecklistItem);
        }
    }

    // 체크리스트 아이템을 추가하는 메서드
    private void addChecklistItem(String checklistItem) {
        checklistItems.add(checklistItem);
        checklistAdapter.notifyDataSetChanged();
        saveChecklistItems(todayDateTextView.getText().toString(), checklistItems);
        updateDate(todayDateTextView.getText().toString());
    }

    // 뒤로가기 메서드
    private void goBack() {
        finish();
    }

    // 현재 날짜를 형식에 맞춰 반환하는 메서드
    private String getFormattedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Calendar currentDate = Calendar.getInstance();
        return dateFormat.format(currentDate.getTime());
    }
}
