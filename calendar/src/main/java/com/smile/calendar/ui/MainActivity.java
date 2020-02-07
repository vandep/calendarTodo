package com.smile.calendar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.smile.calendar.R;


/**
 * 用于展示日程事件
 * Website: https://github.com/xiewenfeng
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonBasic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BasicSampleActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonAsynchronous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AsynchronousActivity.class);
                startActivity(intent);
            }
        });

        //添加日程
        findViewById(R.id.buttonAddSchedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditCalendarActivity.class);
                startActivity(intent);
            }
        });

        //读取手机系统日程,向日程中添加事件
        findViewById(R.id.buttonSyncSystemSchedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddSystemCalendarEventActivity.class);
                startActivity(intent);
            }
        });

        //展示系统日历事件
        findViewById(R.id.buttonSyncSystemEvent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SyncSystemEventActivity.class);
                startActivity(intent);
            }
        });

    }

}
