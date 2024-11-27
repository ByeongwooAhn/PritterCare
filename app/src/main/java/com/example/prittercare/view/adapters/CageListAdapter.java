package com.example.prittercare.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prittercare.R;
import com.example.prittercare.model.data.CageData;

import java.util.List;

public class CageListAdapter extends RecyclerView.Adapter<CageListAdapter.CageViewHolder> {

    private List<CageData> cageList;
    private OnItemClickListener onItemClickListener;

    // 인터페이스: 클릭 이벤트 처리
    public interface OnItemClickListener {
        void onItemClick(CageData cage);
        void onItemLongClick(CageData cage, int position);
    }

    // 생성자
    public CageListAdapter(List<CageData> cageList) {
        this.cageList = cageList;
    }

    // 클릭 리스너 설정
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public CageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cage_list_item, parent, false);
        return new CageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CageViewHolder holder, int position) {
        CageData cage = cageList.get(position);

        // 데이터를 UI에 바인딩
        holder.tvCageName.setText(cage.getCageName());
        holder.ivAnimalImage.setImageResource(getAnimalImageResource(cage.getAnimalType()));

        // 클릭 이벤트 바인딩
        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(cage);
            }
        });

        // 길게 클릭 이벤트 처리
        holder.itemView.setOnLongClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemLongClick(cage, holder.getAdapterPosition());
            }
            return true; // 롱클릭 이벤트 소비
        });
    }

    @Override
    public int getItemCount() {
        return cageList.size();
    }

    // 동물 타입에 따른 이미지 리소스 반환
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

    // ViewHolder 클래스 : UI 요소와 연결
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
