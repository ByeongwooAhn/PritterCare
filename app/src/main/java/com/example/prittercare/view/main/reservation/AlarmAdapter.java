package com.example.prittercare.view.main.reservation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.prittercare.R;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private List<Alarm> alarmList;
    private OnAlarmClickListener clickListener;
    private OnAlarmLongClickListener longClickListener;

    public interface OnAlarmClickListener {
        void onAlarmClick(int position);
    }

    public interface OnAlarmLongClickListener {
        void onAlarmLongClick(int position);
    }

    public AlarmAdapter(List<Alarm> alarmList, OnAlarmClickListener clickListener, OnAlarmLongClickListener longClickListener) {
        this.alarmList = alarmList;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {
        Alarm alarm = alarmList.get(position);
        holder.alarmTime.setText(alarm.getTime());
        holder.alarmDate.setText(alarm.getDate());
        holder.alarmToggle.setChecked(alarm.isEnabled());

        if (alarm.getName() != null && !alarm.getName().isEmpty()) {
            holder.alarmName.setText(alarm.getName());
            holder.alarmName.setVisibility(View.VISIBLE);
        } else {
            holder.alarmName.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> clickListener.onAlarmClick(position));
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onAlarmLongClick(position);
            return true;
        });

        holder.alarmToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            alarm.setEnabled(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView alarmTime, alarmDate, alarmName;
        Switch alarmToggle;

        public AlarmViewHolder(View itemView) {
            super(itemView);
            alarmTime = itemView.findViewById(R.id.alarmTime);
            alarmDate = itemView.findViewById(R.id.alarmDate);
            alarmToggle = itemView.findViewById(R.id.alarmToggle);
            alarmName = itemView.findViewById(R.id.alarmName); // 알람 이름 추가
        }
    }
}
