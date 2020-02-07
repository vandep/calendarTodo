package com.smile.calendar.module;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * 日程Module
 */
@RealmClass
public class EventModel extends RealmObject{

    public String mName;//标题
    public String mContent;//内容
    public String mPosition; //地点
    public String mStartTime;//开始时间
    public String mEndTime;//结束时间
    public String mColor;//日程背景色
    public String moduleName;//模块名称
    public Date date;//日期
    public String f1;//保留字段
    public String f2;//保留字段
    public String f3;//保留字段
    public int year;//年
    public int month;
    public int day;


    public String id;

    public EventModel() {
        this("", "", "", "", "", 0, 0, 0, String.valueOf(System.currentTimeMillis()));
    }

    public EventModel(String name, String content, String startTime, String endTime, String position, int year, int month, int day, String id) {
        this.mName = name;
        this.mContent = content;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mPosition = position;
        this.year = year;
        this.month = month;
        this.day = day;
        this.id = id;
    }

    public EventModel(String name, String content,  String startTime, String endTime, String position, int year, int month, int day, String id, String moduleName) {
        this(name, content, startTime, endTime, position, year, month, day, id);
        this.moduleName = moduleName;
    }
}
