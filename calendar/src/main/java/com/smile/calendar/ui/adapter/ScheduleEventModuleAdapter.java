package com.smile.calendar.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.smile.calendar.R;
import com.smile.calendar.module.EventModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xwf on 2017/7/19
 * 日程列表adapter适配器
 */
public class ScheduleEventModuleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<EventModel> mSchedules;

    public ScheduleEventModuleAdapter(Context context) {
        mContext = context;
        mSchedules = new ArrayList<>();
    }

    public void setMSchedule(List<EventModel> events) {
        mSchedules = events;
        if (mSchedules == null || mSchedules.size() == 0) {
            mSchedules = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    public List<EventModel> getData() {
        return mSchedules;
    }

    public void clear() {
        mSchedules = null;
        mSchedules = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ScheduleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_schedule, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ScheduleViewHolder) {
            final EventModel schedule = mSchedules.get(position);
            final ScheduleViewHolder viewHolder = (ScheduleViewHolder) holder;
            viewHolder.tvScheduleTitle.setText(schedule.mName);
            viewHolder.tvScheduleTime.setText(schedule.mStartTime + "~" + schedule.mEndTime);
        }
    }

    @Override
    public int getItemCount() {
        return mSchedules == null ? 0 : mSchedules.size();
    }

    protected class ScheduleViewHolder extends RecyclerView.ViewHolder {

        protected View vScheduleHintBlock;
        protected TextView tvScheduleState;
        protected TextView tvScheduleTitle;
        protected TextView tvScheduleTime;

        public ScheduleViewHolder(View itemView) {
            super(itemView);
            vScheduleHintBlock = itemView.findViewById(R.id.vScheduleHintBlock);
            tvScheduleState = (TextView) itemView.findViewById(R.id.tvScheduleState);
            tvScheduleTitle = (TextView) itemView.findViewById(R.id.tvScheduleTitle);
            tvScheduleTime = (TextView) itemView.findViewById(R.id.tvScheduleTime);
        }

    }

}
