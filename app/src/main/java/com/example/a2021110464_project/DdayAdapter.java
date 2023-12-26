package com.example.a2021110464_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DdayAdapter extends ArrayAdapter<DdayItem> {

    public DdayAdapter(@NonNull Context context, ArrayList<DdayItem> ddayList) {
        super(context, 0, ddayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        DdayItem ddayItem = getItem(position);

        if (ddayItem != null) {
            TextView textView = convertView.findViewById(android.R.id.text1);
            String formattedDday = getFormattedDday(ddayItem.getDdayDate());
            String displayText = String.format("%s        %s           %s",
                    formattedDday, ddayItem.getDdayName(), getFormattedDate(ddayItem.getDdayDate()));
            textView.setText(displayText);
        }

        return convertView;
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

            return "D+" + (diffDays ); // 이미 지난 경우 D+숫자 형식으로 표시
        } else {
            // D-day까지 남은 날짜를 구합니다.
            long diffMillis = ddayCalendar.getTimeInMillis() - today.getTimeInMillis();
            long diffDays = (diffMillis / (24 * 60 * 60 * 1000));

            return "D-" + (diffDays + 1); // D-day까지 남은 경우 D-n 형식으로 표시
        }
    }


    // 두 날짜가 같은 날짜인지 확인하는 메서드
    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }


    private String getFormattedDate(Date date) {
        // 날짜를 "yyyy-MM-dd" 형식으로 반환합니다.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date);
    }
}