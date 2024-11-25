package com.example.prittercare.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prittercare.R;
import com.example.prittercare.model.CageData;

import java.util.List;

public class CageListAdapter extends RecyclerView.Adapter<CageListAdapter.exampleCageViewHolder> {

    private List<CageData> cageList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(CageData cage);
    }

    public CageListAdapter(List<CageData> cageList) {
        this.cageList = cageList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public exampleCageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cage_list_item, parent, false);
        return new exampleCageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull exampleCageViewHolder holder, int position) {
        CageData cage = cageList.get(position);

        // 케이지 이름 설정
        holder.tvCageName.setText(cage.getCageName());

        // 이미지 리소스 설정
        String imageResId = cage.getAnimalType();
        int imageResource = getAnimalImageResource(imageResId);
        if (imageResource != 0) {
            holder.ivAnimalImage.setImageResource(imageResource);
        }

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(cage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cageList.size();
    }

    private int getAnimalImageResource(String animalType) {
        switch (animalType) {
            case "fish":
                return R.drawable.ic_fish;
            case "turtle":
                return R.drawable.ic_turtle;
            case "hamster":
                return R.drawable.ic_hamster;
            default:
                return 0;
        }
    }

    public static class exampleCageViewHolder extends RecyclerView.ViewHolder {
        TextView tvCageName;
        ImageView ivAnimalImage;

        public exampleCageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCageName = itemView.findViewById(R.id.tvCageName);
            ivAnimalImage = itemView.findViewById(R.id.ivAnimalImage);
        }
    }
}
