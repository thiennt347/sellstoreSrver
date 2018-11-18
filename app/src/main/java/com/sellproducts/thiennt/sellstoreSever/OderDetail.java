package com.sellproducts.thiennt.sellstoreSever;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.sellproducts.thiennt.sellstoreSever.Common.Common;
import com.sellproducts.thiennt.sellstoreSever.ViewHolder.OrderDetailAdapter;

public class OderDetail extends AppCompatActivity {

    TextView order_id, order_phone, order_address,order_total, order_comment;
    String order_id_value = "";
    RecyclerView listProduct;
    RecyclerView.LayoutManager layoutManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oder_detail);

        order_id = (TextView) findViewById(R.id.order_id);
        order_phone = (TextView) findViewById(R.id.order_phone);
        order_address = (TextView) findViewById(R.id.order_address);
        order_total = (TextView) findViewById(R.id.order_total);
        order_comment = (TextView)findViewById(R.id.order_comment);

        listProduct = (RecyclerView) findViewById(R.id.listProduct);
        listProduct.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listProduct.setLayoutManager(layoutManager);

        if(getIntent() != null)
        {
            order_id_value = getIntent().getStringExtra("OrderId");
        }

        //set values
        order_id.setText(order_id_value);
        order_phone.setText(Common.currentRequest.getPhone());
        order_address.setText(Common.currentRequest.getAddress());
        order_total.setText(Common.currentRequest.getTotal());
        order_comment.setText(Common.currentRequest.getComment());

        OrderDetailAdapter orderDetailAdapter  = new OrderDetailAdapter(Common.currentRequest.getProducts());
        orderDetailAdapter.notifyDataSetChanged();
        listProduct.setAdapter(orderDetailAdapter);

    }
}
