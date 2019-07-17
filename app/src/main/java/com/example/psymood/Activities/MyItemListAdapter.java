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
import com.example.psymood.Preferences.ApplicationPreferences;
import com.example.psymood.R;

import java.util.List;

public class MyItemListAdapter extends RecyclerView.Adapter<MyItemListAdapter.MyViewHolder> {
    private static final String KEYSTATE = "NUMSTATE";
    private Context context;
    private OnItemClickListener mlistener;
    private List<ItemData> itemDataList;

    public MyItemListAdapter(Context context, List<ItemData> itemDataList,OnItemClickListener listener) {
        this.context = context;
        this.itemDataList = itemDataList;
        this.mlistener =  listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_list, viewGroup , false);
        MyItemListAdapter.MyViewHolder viewHolder = new MyItemListAdapter.MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int i) {


        myViewHolder.bind(itemDataList.get(i), mlistener);

        //Aqui tengo la imagen y el texto de la celda.
        myViewHolder.img_item.setBackgroundResource(itemDataList.get(i).getImage());
        myViewHolder.text_view_item.setText(itemDataList.get(i).getTitle());

        repaintCellData(myViewHolder,i);


        //TODO si clicko y el anterior elemento era el mismo que yo entonces desclicko
        myViewHolder.cardViewState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("MyItemAdapater", "CardView clicked");

                checkHasCellClicked();

                myViewHolder.cardViewState.setCardElevation(9f);
                myViewHolder.cardViewState.setCardBackgroundColor(ContextCompat.getColor(context,R.color.GreenCheck));
                myViewHolder.imageViewCheck.setBackgroundResource(R.drawable.ic_check_circle);
                itemDataList.get(i).setClicked(true);
                updateNumState(1);
                mlistener.onItemClick(itemDataList.get(i));
            }
        });




    }



    private void checkHasCellClicked() {

        for(int i = 0;i < getItemCount(); i++){
            if(itemDataList.get(i).getClicked()){
                itemDataList.get(i).setClicked(false);
                updateNumState(-1);
                notifyItemChanged(i);
            }
        }
    }

    private void repaintCellData(MyViewHolder myViewHolder,int positionCell) {

        if(itemDataList.get(positionCell).getClicked()==false) {
            myViewHolder.cardViewState.setCardBackgroundColor(ContextCompat.getColor(context, R.color.White));
            myViewHolder.imageViewCheck.setBackgroundResource(R.color.White);
        }
        else{
            myViewHolder.cardViewState.setCardElevation(9f);
            myViewHolder.cardViewState.setCardBackgroundColor(ContextCompat.getColor(context,R.color.GreenCheck));
            myViewHolder.imageViewCheck.setBackgroundResource(R.drawable.ic_check_circle);
        }
    }
    private void updateNumState(int i) {
        int contador = ApplicationPreferences.loadNumState(KEYSTATE);
        contador =  contador + i;
        if(contador < 0){
            contador = 0;
        }
        ApplicationPreferences.saveNumState(KEYSTATE,contador);

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


        public void bind(final ItemData itemData, final OnItemClickListener mlistener) {

            //itemView es el item del ViewHolder
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mlistener.onItemClick(itemData);
                }
            });

        }
    }

    public interface OnItemClickListener{
        void onItemClick(ItemData itemData);
    }
}
