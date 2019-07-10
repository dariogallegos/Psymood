package com.example.psymood.Activities;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.psymood.Models.ItemData;
import com.example.psymood.R;

import java.util.List;

public class MyItemListAdapter extends RecyclerView.Adapter<MyItemListAdapter.MyViewHolder> {
    private Context context;
    private List<ItemData> itemDataList;

    public MyItemListAdapter(Context context, List<ItemData> itemDataList) {
        this.context = context;
        this.itemDataList = itemDataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_list, viewGroup , false);
        MyItemListAdapter.MyViewHolder viewHolder = new MyItemListAdapter.MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        myViewHolder.text_view_item.setText(itemDataList.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return (itemDataList != null ? itemDataList.size() : 0) ;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_view_item;
        ImageView img_item;

        public MyViewHolder(View itemView) {
            super(itemView);
            text_view_item  = itemView.findViewById(R.id.itemText);
            img_item = itemView.findViewById(R.id.itemImage);
        }
    }
}
