package com.example.a2021110464_project;

import static com.applandeo.materialcalendarview.utils.DateUtils.isToday;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DdayAddActivity extends AppCompatActivity {

    private EditText ddayNameEditText;
    private Button selectDateButton;
    private TextView selectedDateTextView;
    private Button addDdayButton;

    private Calendar selectedDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dday_add);

        ddayNameEditText = findViewById(R.id.ddayNameEditText);
        selectDateButton = findViewById(R.id.selectDateButton);
        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        addDdayButton = findViewById(R.id.addDdayButton);

        selectedDate = Calendar.getInstance();

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        addDdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDday();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, monthOfYear);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateSelectedDate();
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void updateSelectedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(selectedDate.getTime());
        selectedDateTextView.setText(formattedDate);
    }

    private void addDday() {
        String ddayName = ddayNameEditText.getText().toString();

        if (!ddayName.isEmpty()) {
            Intent resultIntent = new Intent();
            DdayItem newDday = new DdayItem(ddayName, selectedDate.getTime());
            resultIntent.putExtra("newDday", newDday);

            // Check if the selected date is today
            if (isToday(selectedDate)) {
                // Call sendNotification method to show the notification
                //((DdayActivity) getParent()).sendNotification(ddayName, selectedDate.getTime());
            }

            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}

class DdayItem implements Parcelable {

    private String ddayName;
    private Date ddayDate;

    public DdayItem(String ddayName, Date ddayDate) {
        this.ddayName = ddayName;
        this.ddayDate = ddayDate;
    }

    protected DdayItem(Parcel in) {
        ddayName = in.readString();
        long tmpDdayDate = in.readLong();
        ddayDate = tmpDdayDate != -1 ? new Date(tmpDdayDate) : null;
    }

    public static final Creator<DdayItem> CREATOR = new Creator<DdayItem>() {
        @Override
        public DdayItem createFromParcel(Parcel in) {
            return new DdayItem(in);
        }

        @Override
        public DdayItem[] newArray(int size) {
            return new DdayItem[size];
        }
    };

    public String getDdayName() {
        return ddayName;
    }

    public Date getDdayDate() {
        return ddayDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ddayName);
        dest.writeLong(ddayDate != null ? ddayDate.getTime() : -1);
    }
}
