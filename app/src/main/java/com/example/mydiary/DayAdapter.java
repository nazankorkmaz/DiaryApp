package com.example.mydiary;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydiary.databinding.RecycleRowBinding;

import java.util.ArrayList;

public class DayAdapter  extends RecyclerView.Adapter<DayAdapter.DayHolder> {
    ArrayList<Day> dayArrayList;
    public DayAdapter(ArrayList<Day> dayArrayList){     //constructor
        this.dayArrayList = dayArrayList;
    }
    @NonNull
    @Override
    public DayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecycleRowBinding recycleRowBinding = RecycleRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new DayHolder(recycleRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DayHolder holder, int position) {
        holder.binding.recycleViewTextView.setText(dayArrayList.get(position).date);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(),DayActivity.class);                                                                                         //dayactivitye gitcem aşağıdaki değerlerle
                intent.putExtra("info","old");
                intent.putExtra("dayId",dayArrayList.get(position).id);                                                                                                             //hangi pozisyondaysam o değeri al ve
                holder.itemView.getContext().startActivity(intent);                                                                                                                             //intent başlatıldı
            }
        });
    }
                                                                                                                                                                    //kaç görünüm oluşturulcak
    @Override
    public int getItemCount() {
        return dayArrayList.size();
    }

    public  class DayHolder extends  RecyclerView.ViewHolder{                                                                                                       //görünüm tutucu sınıf  yani recycleview xmli bağlıcaz  yani recyle_row.xml burda oluşturduk

        private RecycleRowBinding binding;                                                                                                                          //bu
        public DayHolder(RecycleRowBinding binding) {                                                                                                                    //xml adı row işte
            super(binding.getRoot());                                                                                                                                       //görünümü aldı
            this.binding = binding;
        }
    }
}
