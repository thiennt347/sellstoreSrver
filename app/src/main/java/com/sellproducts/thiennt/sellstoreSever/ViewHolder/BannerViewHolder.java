package com.sellproducts.thiennt.sellstoreSever.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sellproducts.thiennt.sellstoreSever.Common.Common;
import com.sellproducts.thiennt.sellstoreSever.Interface.ItemClickListener;
import com.sellproducts.thiennt.sellstoreSever.R;

public class BannerViewHolder extends RecyclerView.ViewHolder implements
        View.OnCreateContextMenuListener
{
    public TextView Banner_name;
    public ImageView Banner_image;

    public BannerViewHolder(View itemView) {
        super(itemView);
        Banner_name = itemView.findViewById(R.id.Banner_name);
        Banner_image = itemView.findViewById(R.id.Banner_image);

        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        contextMenu.setHeaderTitle("Mời Chọn");

        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);
    }
}
