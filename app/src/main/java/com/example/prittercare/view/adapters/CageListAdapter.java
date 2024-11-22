package com.example.prittercare.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prittercare.R;
import com.example.prittercare.view.CageListActivity;
import com.example.prittercare.view.MainActivity;

import java.util.List;

public class CageListAdapter extends RecyclerView.Adapter<CageListAdapter.CageViewHolder> {

    private List<CageListActivity.Cage> cageList;
    private Context context; // Context 추가
    private OnItemLongClickListener longClickListener; // Long Click Listener 추가

    // Long Click Listener 인터페이스
    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    // Constructor에서 Context와 Long Click Listener를 받도록 수정
    public CageListAdapter(Context context, List<CageListActivity.Cage> cageList, OnItemLongClickListener longClickListener) {
        this.context = context;
        this.cageList = cageList;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public CageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cage_list_item, parent, false);
        return new CageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CageViewHolder holder, int position) {
        CageListActivity.Cage cage = cageList.get(position);
        holder.tvCageName.setText(cage.getName());
        holder.ivAnimalImage.setImageResource(cage.getImageResId());

        // 꾹 눌렀을 때 이벤트 처리
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(position);
            }
            return true;
        });

        // 클릭 시 MainActivity로 이동
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("cageName", cage.getName()); // 케이지 이름 전달
            intent.putExtra("imageResId", cage.getImageResId()); // 이미지 리소스 ID 전달
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cageList.size();
    }

    public static class CageViewHolder extends RecyclerView.ViewHolder {
        TextView tvCageName;
        ImageView ivAnimalImage;

        public CageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCageName = itemView.findViewById(R.id.tvCageName);
            ivAnimalImage = itemView.findViewById(R.id.ivAnimalImage);
        }
    }
}
