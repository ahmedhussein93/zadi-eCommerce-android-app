package com.cafateria.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cafateria.R;
import com.cafateria.helper.AppSettings;
import com.cafateria.helper.Global;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DatePickerTimePickerActivity extends AppCompatActivity implements View.OnClickListener {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int seconds;

    TextView done, back;
    private TimePicker timePicker;
    private DatePicker datePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker_time_picker);

        AppSettings.setLang(this, AppSettings.getLang(this));

        done = (TextView) findViewById(R.id.done);
        done.setOnClickListener(this);
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(this);
        // Get current calendar date and time.
        final Calendar currCalendar = Calendar.getInstance();

        // Set the timezone which you want to display time.
        currCalendar.setTimeZone(TimeZone.getDefault());

        year = currCalendar.get(Calendar.YEAR);
        month = currCalendar.get(Calendar.MONTH);
        day = currCalendar.get(Calendar.DAY_OF_MONTH);
        hour = currCalendar.get(Calendar.HOUR_OF_DAY);
        minute = currCalendar.get(Calendar.MINUTE);
        seconds = currCalendar.get(Calendar.SECOND);


        // Get time picker object.
        timePicker = (TimePicker) findViewById(R.id.timePickerExample);
        timePicker.setCurrentHour(this.hour + 1);
        timePicker.setCurrentMinute(this.minute);

        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                DatePickerTimePickerActivity.this.hour = hour;
                DatePickerTimePickerActivity.this.minute = minute;

                if (day == currCalendar.get(Calendar.DAY_OF_MONTH) && currCalendar.get(Calendar.HOUR) >= hour) {
                    timePicker.setCurrentHour(currCalendar.get(Calendar.HOUR) + 1);
                }
            }
        });


        // Get date picker object.
        datePicker = (DatePicker) findViewById(R.id.datePickerExample);
        datePicker.setMinDate(System.currentTimeMillis() + (60 * 60 * 1000));
        datePicker.setMaxDate(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                DatePickerTimePickerActivity.this.year = year;
                DatePickerTimePickerActivity.this.month = month;
                DatePickerTimePickerActivity.this.day = day;
                if (day == currCalendar.get(Calendar.DAY_OF_MONTH)) {
                    timePicker.setCurrentHour(currCalendar.get(Calendar.HOUR) + 1);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.done) {

            Calendar calendar = new GregorianCalendar(year, month + 1, day, hour, minute);
            long time = calendar.getTimeInMillis();

            Intent data = new Intent();
            data.putExtra("time", time);
            data.putExtra("year", year);
            data.putExtra("month", month + 1);
            data.putExtra("day", day);
            data.putExtra("hour", hour);
            data.putExtra("minute", minute);
            //data.putExtra("year",year);
            //setResult(RESULT_OK, data);
            if (getParent() == null) {
                setResult(Activity.RESULT_OK, data);
            } else {
                getParent().setResult(Activity.RESULT_OK, data);
            }
        }
        finish();
    }

    @Override
    protected void onResume() {
        Global.CURRENT_ACTIVITY = this;
        super.onResume();
    }

    @Override
    protected void onPause() {
        Global.CURRENT_ACTIVITY = null;
        super.onPause();
    }
}