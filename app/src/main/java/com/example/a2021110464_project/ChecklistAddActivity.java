package com.example.a2021110464_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ChecklistAddActivity extends AppCompatActivity {

    private EditText checklistItemEditText;
    private Button addChecklistItemButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_add);

        checklistItemEditText = findViewById(R.id.checklistItemEditText);
        addChecklistItemButton = findViewById(R.id.addChecklistItemButton);

        // 추가 버튼 클릭 이벤트 설정
        addChecklistItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChecklistItem();
            }
        });
    }

    // 추가 버튼 클릭 시 호출되는 메서드
    public void onAddChecklistItemButtonClick(View view) {
        addChecklistItem();
    }

    // 입력된 할 일을 결과로 반환하고 현재 액티비티 종료
    private void addChecklistItem() {
        String newItem = checklistItemEditText.getText().toString();

        if (!newItem.isEmpty()) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("newChecklistItem", newItem);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}
