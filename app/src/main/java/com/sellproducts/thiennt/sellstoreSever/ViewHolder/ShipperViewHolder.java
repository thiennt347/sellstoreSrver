package com.sellproducts.thiennt.sellstoreSever.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sellproducts.thiennt.sellstoreSever.Interface.ItemClickListener;
import com.sellproducts.thiennt.sellstoreSever.R;

import info.hoang8f.widget.FButton;

public class ShipperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView shipper_name, shipper_phone;
    public FButton btnEdit, btnRemove;
    private ItemClickListener itemClickListener;


    public ShipperViewHolder(View itemView) {
        super(itemView);
        shipper_name = itemView.findViewById(R.id.shipper_name);
        shipper_phone = itemView.findViewById(R.id.shipper_phone);
        btnEdit = itemView.findViewById(R.id.btnEdit);
        btnRemove = itemView.findViewById(R.id.btnRemove);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
