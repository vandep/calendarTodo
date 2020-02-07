package com.smile.calendar.ui;

import android.os.Bundle;

import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.smile.calendar.R;
import com.smile.calendar.apiclient.Event;
import com.smile.calendar.apiclient.MyJsonService;
import com.smile.calendar.manager.CalendarManager;
import com.smile.calendar.module.EventModel;
import com.smile.calendar.view.CollapseCalendarView;


import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * 月视图
 * Created by SmileXie on 2017/5/8.
 */

public class MonthScheduleActivity extends AppCompatActivity implements Callback<List<Event>> {

    private LocalDate selectedDate;//当前选择的日期
    private CalendarManager mManager;
    private CollapseCalendarView calendarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        CollapseCalendarView.withMonthSchedule = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_schedule);
        calendarView = (CollapseCalendarView) findViewById(R.id.calendar);
        initCalendarListener();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        calendarView.showChinaDay(true);
    }


    private void initCalendarListener() {
        selectedDate = LocalDate.now();
        mManager = new CalendarManager(LocalDate.now(),
                CalendarManager.State.MONTH, LocalDate.now().withYear(100),
                LocalDate.now().plusYears(60));
        //月份切换监听器
        mManager.setMonthChangeListener(new CalendarManager.OnMonthChangeListener() {

            @Override
            public void monthChange(String month, LocalDate mSelected) {
                setTitle(month);
                RestAdapter retrofit = new RestAdapter.Builder()
                        .setEndpoint("https://api.myjson.com/bins")
                        .build();
                MyJsonService service = retrofit.create(MyJsonService.class);
                service.listEvents(MonthScheduleActivity.this);

            }

            @Override
            public void weekChange(String week, LocalDate mSelected) {//周切换

            }
        });
        calendarView.init(mManager);
        /**
         * 日期选中监听器
         */
        calendarView.setDateSelectListener(new CollapseCalendarView.OnDateSelect() {

            @Override
            public void onDateSelected(LocalDate date) {
                selectedDate = date;
                Toast.makeText(MonthScheduleActivity.this, "selected date = " + selectedDate.toString(), Toast.LENGTH_SHORT);
            }
        });

    }

    @Override
    public void success(List<Event> events, Response response) {
        if (events != null) {
            List<EventModel> eventList = new ArrayList<>();
            for (Event event : events) {
                eventList.add(new EventModel(event.getName(), "", event.getStartTime(), event.getEndTime(), "", selectedDate.getYear(), selectedDate.getMonthOfYear(), event.getDayOfMonth(), String.valueOf(System.currentTimeMillis())));
            }
            calendarView.setEvent(eventList);
        }
        //刷新数据
    }

    @Override
    public void failure(RetrofitError error) {
        error.printStackTrace();
        Toast.makeText(this, R.string.async_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
