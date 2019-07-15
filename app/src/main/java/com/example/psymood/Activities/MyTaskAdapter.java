package com.example.psymood.Activities;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.psymood.Models.ItemTask;
import com.example.psymood.R;

import java.util.List;

public class MyTaskAdapter extends RecyclerView.Adapter<MyTaskAdapter.ViewHolder> {

    private List<ItemTask> itemTaskList;
    private Context context;


    public MyTaskAdapter(Context context,List<ItemTask> itemTaskList) {
        this.context =  context;
        this.itemTaskList = itemTaskList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_task, parent, false);
        MyTaskAdapter.ViewHolder viewHolder = new MyTaskAdapter.ViewHolder(v);
        //ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        //holder.cardTask.setBackground(new ColorDrawable(itemTaskList.get(position).getColorTask()));
        holder.iconOfTask.setBackgroundResource(itemTaskList.get(position).getIconTask());
        holder.cardTask.setCardBackgroundColor(ContextCompat.getColor(context,itemTaskList.get(position).getColorTask()));
        holder.titleTask.setText(itemTaskList.get(position).getTitleTask());
        holder.numTask.setText(itemTaskList.get(position).getNumTask());

    }

    @Override
    public int getItemCount() {
        return (itemTaskList != null ? itemTaskList.size() : 0) ;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iconOfTask;
        TextView titleTask;
        TextView numTask;
        CardView cardTask;

        public ViewHolder(View itemView) {
            super(itemView);

            iconOfTask = itemView.findViewById(R.id.iconOfTask);
            titleTask = itemView.findViewById(R.id.titleTask);
            numTask =  itemView.findViewById(R.id.numTask);
            cardTask = itemView.findViewById(R.id.cardViewTask);

        }
    }
}
