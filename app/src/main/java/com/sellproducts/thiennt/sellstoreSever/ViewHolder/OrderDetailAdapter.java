package com.sellproducts.thiennt.sellstoreSever.ViewHolder;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sellproducts.thiennt.sellstoreSever.R;
import com.sellproducts.thiennt.sellstoreSever.model.Order;

import java.util.List;

class  MyViewHolder extends RecyclerView.ViewHolder{

    public TextView name, quantity, price, discount;

    public MyViewHolder(View itemView) {
        super(itemView);

        name = (TextView) itemView.findViewById(R.id.detail_Product_name);
        quantity = (TextView) itemView.findViewById(R.id.detail_Product_quantity);
        price = (TextView) itemView.findViewById(R.id.detail_Product_price);
        discount = (TextView) itemView.findViewById(R.id.detail_Product_discount);
    }
}
public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder> {

    List<Order> myOrder;

    public OrderDetailAdapter(List<Order> myOrder) {
        this.myOrder = myOrder;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout,parent, false);
        return new MyViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Order order = myOrder.get(position);
        holder.name.setText(String.format("Tên Sản Phẩm : %s", order.getProductName()));
        holder.quantity.setText(String.format("Số Lượng : %s", order.getQuantity()));
        holder.price.setText(String.format("Giá : %s", order.getPrice()));
        holder.discount.setText(String.format("Giảm Giá : %s", order.getDiscount()));


    }

    @Override
    public int getItemCount() {
        return myOrder.size();
    }
}
