package com.example.prittercare.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.prittercare.R;
import com.example.prittercare.model.data.ReservationData;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.AlarmViewHolder> {

    private List<ReservationData> alarmList;
    private OnAlarmClickListener clickListener;
    private OnAlarmLongClickListener longClickListener;

    public interface OnAlarmClickListener {
        void onAlarmClick(int position);
    }

    public interface OnAlarmLongClickListener {
        void onAlarmLongClick(int position);
    }

    public ReservationAdapter(List<ReservationData> alarmList, OnAlarmClickListener clickListener, OnAlarmLongClickListener longClickListener) {
        this.alarmList = alarmList;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_list_item, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {
        ReservationData alarm = alarmList.get(position);

        holder.alarmTime.setText(alarm.getReserveTime());
        holder.alarmDate.setText(alarm.getReserveDate());

        if (alarm.getReserveName() != null && !alarm.getReserveName().isEmpty()) {
            holder.alarmName.setText(alarm.getReserveName());
            holder.alarmName.setVisibility(View.VISIBLE);
        } else {
            holder.alarmName.setVisibility(View.GONE);
        }

        // 주기 설정 표시
        if (alarm.getDayLoop() > 0) {
            holder.alarmCycle.setText(String.format("%d일마다", alarm.getDayLoop()));
            holder.alarmCycle.setVisibility(View.VISIBLE);
        } else if (alarm.getTimeLoop() > 0) {
            holder.alarmCycle.setText(String.format("%d시간마다", alarm.getTimeLoop()));
            holder.alarmCycle.setVisibility(View.VISIBLE);
        } else {
            holder.alarmCycle.setVisibility(View.GONE); // 주기가 없으면 숨김
        }

        // 예약 종류에 따른 이미지 설정
        switch (alarm.getReserveType()) {
            case "water":
                holder.alarmTypeIcon.setImageResource(R.drawable.ic_water);
                break;
            case "food":
                holder.alarmTypeIcon.setImageResource(R.drawable.ic_food);
                break;
            case "light":
                holder.alarmTypeIcon.setImageResource(R.drawable.ic_light);
                break;
            default:
                holder.alarmTypeIcon.setImageResource(R.drawable.ic_placeholder);
        }

        holder.itemView.setOnClickListener(v -> clickListener.onAlarmClick(position));
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onAlarmLongClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView alarmTime, alarmDate, alarmName, alarmCycle; // alarmCycle 추가
        ImageView alarmTypeIcon;

        public AlarmViewHolder(View itemView) {
            super(itemView);
            alarmTime = itemView.findViewById(R.id.reservationTime);
            alarmDate = itemView.findViewById(R.id.alarmDate);
            alarmName = itemView.findViewById(R.id.reservationName);
            alarmCycle = itemView.findViewById(R.id.alarmCycle); // 추가된 주기 텍스트
            alarmTypeIcon = itemView.findViewById(R.id.alarmTypeIcon);
        }
    }
}
