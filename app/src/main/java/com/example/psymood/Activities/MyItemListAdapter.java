package com.example.psymood.Activities;


import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
    public void onBindViewHolder(final MyViewHolder myViewHolder, int i) {
        //Aqui tengo la imagen y el texto de la celda.
        myViewHolder.img_item.setBackgroundResource(itemDataList.get(i).getImage());
        myViewHolder.text_view_item.setText(itemDataList.get(i).getTitle());
        myViewHolder.cardViewState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("MyItemAdapater", "CardView clicked");
                myViewHolder.cardViewState.setCardElevation(9f);
                myViewHolder.cardViewState.setCardBackgroundColor(ContextCompat.getColor(context,R.color.GreenCheck));
                myViewHolder.imageViewCheck.setBackgroundResource(R.drawable.ic_check_circle);

            }
        });
    }

    @Override
    public int getItemCount() {
        return (itemDataList != null ? itemDataList.size() : 0) ;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewState;
        TextView text_view_item;
        ImageView img_item,imageViewCheck;

        public MyViewHolder(View itemView) {
            super(itemView);
            cardViewState = itemView.findViewById(R.id.cardViewState);
            text_view_item  = itemView.findViewById(R.id.itemText);
            img_item = itemView.findViewById(R.id.itemImage);
            imageViewCheck = itemView.findViewById(R.id.imageViewCheck);
        }

    }

    /*public interface OnItemClickListener{
        void onItemClick();
    }*/
}
