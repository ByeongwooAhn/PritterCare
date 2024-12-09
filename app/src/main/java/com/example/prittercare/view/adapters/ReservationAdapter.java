package com.example.prittercare.view.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.prittercare.R;
import com.example.prittercare.model.data.ReservationData;

import java.util.ArrayList;
import java.util.List;

/**
 * ReservationAdapter 클래스
 * RecyclerView를 위한 어댑터로, 예약 데이터를 RecyclerView에 표시하고 클릭 및 롱클릭 이벤트를 처리합니다.
 */
public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.AlarmViewHolder> {

    // 예약 데이터 리스트
    private List<ReservationData> alarmList;

    // 클릭 및 롱클릭 리스너 인터페이스
    private OnAlarmClickListener clickListener;
    private OnAlarmLongClickListener longClickListener;

    /**
     * 클릭 리스너 인터페이스
     */
    public interface OnAlarmClickListener {
        void onAlarmClick(int position); // 클릭 이벤트 처리 메서드
    }

    /**
     * 롱클릭 리스너 인터페이스
     */
    public interface OnAlarmLongClickListener {
        void onAlarmLongClick(int position); // 롱클릭 이벤트 처리 메서드
    }

    /**
     * 생성자
     *
     * @param alarmList       예약 데이터 리스트
     * @param clickListener   클릭 리스너
     * @param longClickListener 롱클릭 리스너
     */
    public ReservationAdapter(List<ReservationData> alarmList, OnAlarmClickListener clickListener, OnAlarmLongClickListener longClickListener) {
        this.alarmList = (alarmList != null) ? alarmList : new ArrayList<>(); // null 방지
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    /**
     * 데이터를 갱신하는 메서드
     *
     * @param newAlarmList 새로운 예약 데이터 리스트
     */
    public void updateData(List<ReservationData> newAlarmList) {
        this.alarmList = (newAlarmList != null) ? newAlarmList : new ArrayList<>(); // null 방지
        notifyDataSetChanged(); // 데이터 변경 시 RecyclerView 업데이트
    }

    /**
     * ViewHolder를 생성하는 메서드
     *
     * @param parent   부모 ViewGroup
     * @param viewType 뷰 타입
     * @return 생성된 ViewHolder
     */
    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 레이아웃을 inflate하여 ViewHolder 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_list_item, parent, false);
        return new AlarmViewHolder(view);
    }

    /**
     * ViewHolder에 데이터를 바인딩하는 메서드
     *
     * @param holder   ViewHolder
     * @param position 데이터 위치
     */
    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {
        // 현재 예약 데이터를 가져옴
        ReservationData alarm = alarmList.get(position);
        Log.d("AlarmEditActivity", "Saved light level: " + alarm.getLightLevel());

        // 시간 및 날짜 설정
        holder.alarmTime.setText(alarm.getReserveTime());
        holder.alarmDate.setText(alarm.getReserveDate());

        // 예약 이름이 있으면 표시, 없으면 숨김
        if (alarm.getReserveName() != null && !alarm.getReserveName().isEmpty()) {
            holder.alarmName.setText(alarm.getReserveName());
            holder.alarmName.setVisibility(View.VISIBLE);
        } else {
            holder.alarmName.setVisibility(View.GONE);
        }

        // 주기 설정 (일 또는 시간 단위)
        if (alarm.getDayLoop() > 0) {
            holder.alarmCycle.setText(String.format("%d일마다", alarm.getDayLoop()));
            holder.alarmCycle.setVisibility(View.VISIBLE);
        } else if (alarm.getTimeLoop() > 0) {
            holder.alarmCycle.setText(String.format("%d시간마다", alarm.getTimeLoop()));
            holder.alarmCycle.setVisibility(View.VISIBLE);
        } else {
            holder.alarmCycle.setVisibility(View.GONE); // 주기가 없으면 숨김
        }

        // 예약 타입에 따라 아이콘 설정
        switch (alarm.getReserveType()) {
            case "water":
                holder.alarmTypeIcon.setImageResource(R.drawable.ic_water); // 물
                holder.lightLevelText.setVisibility(View.GONE); // 조명 단계 숨김
                break;
            case "food":
                holder.alarmTypeIcon.setImageResource(R.drawable.ic_food); // 음식
                holder.lightLevelText.setVisibility(View.GONE); // 조명 단계 숨김
                break;
            case "light":
                holder.alarmTypeIcon.setImageResource(R.drawable.ic_light);
                if (alarm.getLightLevel() > 0) {
                    holder.lightLevelText.setText(String.format("%d단계", alarm.getLightLevel()));
                    holder.lightLevelText.setVisibility(View.VISIBLE);
                } else {
                    holder.lightLevelText.setVisibility(View.GONE);
                }
                break;
            default:
                holder.alarmTypeIcon.setImageResource(R.drawable.ic_placeholder); // 기본 아이콘
                holder.lightLevelText.setVisibility(View.GONE);
        }

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener(v -> clickListener.onAlarmClick(position));

        // 아이템 롱클릭 리스너 설정
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onAlarmLongClick(position);
            return true; // 롱클릭 이벤트 소비
        });
    }

    /**
     * RecyclerView의 아이템 개수 반환
     *
     * @return 아이템 개수
     */
    @Override
    public int getItemCount() {
        return (alarmList != null) ? alarmList.size() : 0; // null 체크
    }

    /**
     * AlarmViewHolder 클래스
     * 각 아이템의 뷰를 관리하는 ViewHolder 클래스
     */
    public static class AlarmViewHolder extends RecyclerView.ViewHolder {

        // UI 요소 선언
        TextView alarmTime, alarmDate, alarmName, alarmCycle, lightLevelText;
        ImageView alarmTypeIcon;

        public AlarmViewHolder(View itemView) {
            super(itemView);
            // UI 요소 초기화
            alarmTime = itemView.findViewById(R.id.reservationTime);
            alarmDate = itemView.findViewById(R.id.alarmDate);
            alarmName = itemView.findViewById(R.id.reservationName);
            alarmCycle = itemView.findViewById(R.id.alarmCycle); // 주기 텍스트
            alarmTypeIcon = itemView.findViewById(R.id.alarmTypeIcon); // 예약 타입 아이콘
            lightLevelText = itemView.findViewById(R.id.lightLevelText); // lightLevelText 초기화
        }
    }
}
