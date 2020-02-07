package com.smile.calendar.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.smile.calendar.R;
import com.smile.calendar.module.EventModel;
import com.smile.calendar.util.CalendarUtils;
import com.smile.calendar.util.RealmHelper;
import com.smile.calendar.util.TimeUtil;

import org.joda.time.LocalDate;

import java.lang.ref.WeakReference;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 向本地日程表中添加日程
 */
public class AddEditCalendarActivity extends BaseActivity implements OnDateSetListener, View.OnClickListener {
    public static final String BUNDLE_KEY_EVENT_ID = "key_bundle_id";
    public static final String BUNDLE_KEY_IS_SYSTEM = "key_is_system";
    private TimePickerDialog mDialogAll;
    private int flag;
    private int startYear, startMonth, startDay, endYear, endMonth, endDay;
    private String startTime, endTime;
    private MyHandler handler;
    private String eventId;
    private boolean canEdit = true;
    private EditText titleEt, positionEt, contentEt;
    private TextView start_time, end_time;
    private boolean isSystemCalendar = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        initView();
        initTimePickDialog();
        initData();
        handler = new MyHandler(this);
    }

    private void initView() {
        titleEt = findViewById(R.id.title_et);
        positionEt = findViewById(R.id.position_et);
        contentEt = findViewById(R.id.content_et);
        start_time = findViewById(R.id.start_time);
        end_time = findViewById(R.id.end_time);
        back_btn = findViewById(R.id.back_btn);
        titleTv = findViewById(R.id.title_tv);
        rightTV = findViewById(R.id.titlebar_right_tv);
        setToolBar();
        setTitle(getString(R.string.add_schedule));
        setRightTitle(getString(R.string.save));
        findViewById(R.id.start_time_layout).setOnClickListener(this);
        findViewById(R.id.end_time_layout).setOnClickListener(this);
    }

    /**
     * 传入需要修改的event
     */
    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            eventId = bundle.getString(BUNDLE_KEY_EVENT_ID);
            isSystemCalendar = bundle.getBoolean(BUNDLE_KEY_IS_SYSTEM, false);
            if (!TextUtils.isEmpty(eventId)) {
                List<EventModel> eventModels = RealmHelper.getRealmHelperInstance().queryById(eventId);
                if (eventModels != null && eventModels.size() > 0) {
                    EventModel eventModel = eventModels.get(0);
                    titleEt.setText(eventModel.mName);
                    positionEt.setText(eventModel.mPosition);
                    StringBuilder startTime = new StringBuilder();
                    startTime.append(eventModel.year).append("-").append(eventModel.month).append("-").append(eventModel.day)
                            .append(" ").append(eventModel.mStartTime);
                    start_time.setText(startTime.toString());

                    EventModel eventModel_end = eventModels.get(eventModels.size() - 1);
                    StringBuilder endTime = new StringBuilder();
                    endTime.append(eventModel_end.year).append("-").append(eventModel_end.month).append("-").append(eventModel_end.day)
                            .append(" ").append(eventModel_end.mEndTime);
                    end_time.setText(endTime.toString());
                    try {
                        if (TimeUtil.compare_current(endTime.toString())) {
                            titleEt.setEnabled(false);
                            positionEt.setEnabled(false);
                            start_time.setEnabled(false);
                            end_time.setEnabled(false);
//                            setRightTitle("");
                            canEdit = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }

    private void initTimePickDialog() {
        long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId(getString(R.string.cancel))
                .setSureStringId(getString(R.string.confirm))
                .setTitleStringId("请选择")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(true)
                .setMinMillseconds(System.currentTimeMillis())
                .setMaxMillseconds(System.currentTimeMillis() + tenYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();
    }

    /**
     * 保存日程
     */
    @Override
    protected void rightClick() {
        if (canEdit) {
            try {
                if (TimeUtil.compare_date(startTime, endTime)) {
                    showShortToast(getString(R.string.start_time_cannot_big_end_time));
                } else {
                    startTime = startTime.split(" ")[1];
                    endTime = endTime.split(" ")[1];
                    String title = titleEt.getText().toString().trim();
                    String position = positionEt.getText().toString().trim();
                    String content = contentEt.getText().toString().trim();
                    String id = TextUtils.isEmpty(eventId) ? String.valueOf(System.currentTimeMillis()) : eventId;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Realm realm = Realm.getDefaultInstance();
                            if (!TextUtils.isEmpty(eventId)) {
                                RealmResults<EventModel> eventModels = realm.where(EventModel.class).equalTo("id", eventId).findAll();
                                realm.executeTransaction(realm1 -> eventModels.deleteAllFromRealm());
                            }
                            if (startYear == endYear) {//年相同
                                if (startMonth == endMonth) {//月相同
                                    if (startDay == endDay) {//日相同
                                        EventModel eventModel = new EventModel(title, content, startTime, endTime, position, startYear, startMonth, startDay, eventId);
                                        realm.executeTransaction(realm1 -> realm1.copyToRealm(eventModel));
                                    } else {//同年同月不同日
                                        for (int i = startDay; i <= endDay; i++) {
                                            EventModel eventModel = new EventModel(title, content, "00:00", "23:59", position, startYear, startMonth, i, eventId);
                                            eventModel.mStartTime = (i == startDay ? startTime : "00:00");
                                            eventModel.mEndTime = (i == endDay ? endTime : "23:59");
                                            realm.executeTransaction(realm1 -> realm1.copyToRealm(eventModel));
                                        }
                                    }
                                } else {//同年不同月
                                    for (int i = startMonth; i < endMonth; i++) {//开始那月到倒数第二个月
                                        int monthDays = CalendarUtils.getMonthDays(startYear, i - 1);//计算每月天数
                                        int j = i == startMonth ? startDay : 1;//如果是开始月，则从开始月的当天算
                                        for (; j <= monthDays; j++) {
                                            startTime = (i == startMonth && j == startDay) ? startTime : "00:00";//如果是开始月开始日，时间要从开始时间算
                                            EventModel eventModel = new EventModel(title, content, startTime, "23:59", position, startYear, i, j, eventId);
                                            realm.executeTransaction(realm1 -> realm1.copyToRealm(eventModel));
                                        }
                                    }
                                    for (int i = 1; i <= endDay; i++) {//对于最后一个月的天事件插入
                                        EventModel eventModel = new EventModel(title, content, "00:00", "23:59", position, startYear, endMonth, i, eventId);
                                        eventModel.mEndTime = (i == endDay ? endTime : "23:59");//最后一第以写的时间为准
                                        realm.executeTransaction(realm1 -> realm1.copyToRealm(eventModel));
                                    }
                                }
                            } else {//不同年
                                for (int cm = startMonth; cm <= 12; cm++) {//当前年
                                    int cd = startMonth == cm ? startDay : 1;
                                    int monthDays = CalendarUtils.getMonthDays(startYear, cm - 1);
                                    for (; cd < monthDays; cd++) {
                                        startTime = (cd == startDay && cm == startMonth) ? startTime : "00:00";
                                        EventModel eventModel = new EventModel(title, content, startTime, "23:59", position, startYear, cm, cd, eventId);
                                        realm.executeTransaction(realm1 -> realm1.copyToRealm(eventModel));
                                    }
                                }

                                for (int cm = 1; cm <= endMonth; cm++) {//最后一年
                                    int monthDays = (endMonth == cm) ? endDay : CalendarUtils.getMonthDays(startYear, cm - 1);
                                    for (int cd = 1; cd < monthDays; cd++) {
                                        endTime = (cd == endDay && cm == endMonth) ? endTime : "23:59";
                                        EventModel eventModel = new EventModel(title, content, "00:00", endTime, position, endYear, cm, cd, eventId);
                                        realm.executeTransaction(realm1 -> realm1.copyToRealm(eventModel));
                                    }
                                }

                                if (endYear - startYear > 1) {//中间年份
                                    for (int y = startYear + 1; y < endYear; y++) {
                                        for (int cm = 1; cm <= 12; cm++) {//最后一年
                                            int monthDays = CalendarUtils.getMonthDays(y, cm - 1);
                                            for (int cd = 1; cd < monthDays; cd++) {
                                                EventModel eventModel = new EventModel(title, content, "00:00", "23:59", position, y, cm, cd, eventId);
                                                realm.executeTransaction(realm1 -> realm1.copyToRealm(eventModel));
                                            }
                                        }


                                    }
                                }
                            }
                            handler.sendEmptyMessage(0);
                        }
                    }).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        if (flag == 0) {
            startTime = TimeUtil.StrToDate(millseconds);
            startTime = startTime.substring(0, startTime.length() - 3);
            start_time.setText(startTime);
            LocalDate datetimeDate = new LocalDate(millseconds);
            startYear = datetimeDate.getYear();
            startMonth = datetimeDate.getMonthOfYear();
            startDay = datetimeDate.getDayOfMonth();
        } else {
            endTime = TimeUtil.StrToDate(millseconds);
            endTime = endTime.substring(0, endTime.length() - 3);
            end_time.setText(endTime);
            LocalDate datetimeDate = new LocalDate(millseconds);
            endYear = datetimeDate.getYear();
            endMonth = datetimeDate.getMonthOfYear();
            endDay = datetimeDate.getDayOfMonth();
        }
    }

    private void timeClick(int flag) {
        this.flag = flag;
        mDialogAll.show(getSupportFragmentManager(), "all");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.start_time_layout) {
            timeClick(0);
        } else if (id == R.id.end_time_layout) {
            timeClick(1);
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<AddEditCalendarActivity> addDeleteActivityWeakReference;

        public MyHandler(AddEditCalendarActivity addDeleteActivity) {
            addDeleteActivityWeakReference = new WeakReference<AddEditCalendarActivity>(addDeleteActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            AddEditCalendarActivity addDeleteActivity = addDeleteActivityWeakReference.get();
            super.handleMessage(msg);
            if (msg.what == 0) {
                Intent intent = new Intent();
                addDeleteActivity.setResult(Activity.RESULT_OK, intent);
                addDeleteActivity.finish();
            }
        }
    }
}
