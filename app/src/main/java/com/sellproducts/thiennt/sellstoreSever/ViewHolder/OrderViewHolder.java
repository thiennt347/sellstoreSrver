package com.sellproducts.thiennt.sellstoreSever.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sellproducts.thiennt.sellstoreSever.Interface.ItemClickListener;
import com.sellproducts.thiennt.sellstoreSever.R;

import info.hoang8f.widget.FButton;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddres, order_date;
    public FButton btnEdit, btnRemove, btnDetail, btnDirection;

    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderAddres = itemView.findViewById(R.id.order_addres);
        txtOrderId = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderPhone = itemView.findViewById(R.id.order_phone);
        order_date = itemView.findViewById(R.id.order_date);

        btnEdit = (FButton) itemView.findViewById(R.id.btnEdit);
        btnRemove = (FButton) itemView.findViewById(R.id.btnRemove);
        btnDetail = (FButton) itemView.findViewById(R.id.btnDetail);
        btnDirection = (FButton) itemView.findViewById(R.id.btnDirection);

    }

}
