package com.example.checkpartgc.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkpartgc.R;

import java.util.List;

public class PartNoList_Adapter_Recycler extends RecyclerView.Adapter<PartNoList_Adapter_Recycler.PartViewHolder> {
    private List<PartItem> _partNoList;

    public PartNoList_Adapter_Recycler(List<PartItem> partNoList) {
        this._partNoList = partNoList;
    }

    @NonNull
    @Override
    public PartNoList_Adapter_Recycler.PartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.part_no_layout, parent, false);
        return new PartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PartNoList_Adapter_Recycler.PartViewHolder holder, int position) {
        PartItem partNo = _partNoList.get(position);
        holder.noTextView.setText(String.valueOf(position +1) );
        holder.partNoTextView.setText(partNo.getPART_NO());
    }

    @Override
    public int getItemCount() {
        return _partNoList.size();
    }

    public static class PartViewHolder extends RecyclerView.ViewHolder {
        TextView noTextView, partNoTextView;

        public PartViewHolder(@NonNull View itemView) {
            super(itemView);
            noTextView = itemView.findViewById(R.id.txtNo);
            partNoTextView = itemView.findViewById(R.id.txtPartNo);

        }
    }
}
