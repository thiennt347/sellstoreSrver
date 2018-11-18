package com.sellproducts.thiennt.sellstoreSever.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sellproducts.thiennt.sellstoreSever.Common.Common;
import com.sellproducts.thiennt.sellstoreSever.Interface.ItemClickListener;
import com.sellproducts.thiennt.sellstoreSever.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements
        View.OnCreateContextMenuListener
{

    public TextView product_name;
    public ImageView product_image;

    private ItemClickListener itemClickListener;

    public ProductViewHolder(View itemView) {
        super(itemView);

        product_name = itemView.findViewById(R.id.Product_name);
        product_image = itemView.findViewById(R.id.Product_image);

        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Mời Chọn");

        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);
    }
}

