package com.sellproducts.thiennt.sellstoreSever;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.sellproducts.thiennt.sellstoreSever.Common.Common;
import com.sellproducts.thiennt.sellstoreSever.Remote.APIService;
import com.sellproducts.thiennt.sellstoreSever.ViewHolder.OrderViewHolder;
import com.sellproducts.thiennt.sellstoreSever.model.DataMessage;
import com.sellproducts.thiennt.sellstoreSever.model.MyResponse;
import com.sellproducts.thiennt.sellstoreSever.model.Request;
import com.sellproducts.thiennt.sellstoreSever.model.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Order_status extends AppCompatActivity {
    RecyclerView recyclerView ;
    RecyclerView.LayoutManager layoutManager ;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    MaterialSpinner StatusSpinner, ShipperSpinner;
    APIService mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        mService = Common.getFCMservice();

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = (RecyclerView) findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrder();
    }

    private void loadOrder() {

        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(requests, Request.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, final int position, final @NonNull Request model) {

                    viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                    viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                    viewHolder.txtOrderAddres.setText(model.getAddress());
                    viewHolder.txtOrderPhone.setText(model.getPhone());
                    viewHolder.order_date.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));

                    //bbutton event edit remove, detall, direction
                    viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            showUpdateDialog(adapter.getRef(position).getKey(), adapter.getItem(position));

                        }
                    });

                    // remove oder

                    viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteItemDialog(adapter.getRef(position).getKey());
                            loadOrder();
                        }
                    });

                    // detail oder

                    viewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intenOderDetail = new Intent(Order_status.this, OderDetail.class);
                            Common.currentRequest = model;
                            intenOderDetail.putExtra("OrderId", adapter.getRef(position).getKey());
                            startActivity(intenOderDetail);
                        }
                    });
                    //direction

                    viewHolder.btnDirection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intentracking = new Intent(Order_status.this, TrackingOrder.class);
                            Common.currentRequest = model;
                            startActivity(intentracking);
                        }
                    });
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View iteamView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout, parent, false);
                return new OrderViewHolder(iteamView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null)
        {
            adapter.startListening();
        }


    }


    private void deleteItemDialog(String key) {

        requests.child(key).removeValue();
        adapter.notifyDataSetChanged();

    }

    private void showUpdateDialog(String key, final Request item) {

        final AlertDialog.Builder alerdialog = new AlertDialog.Builder(Order_status.this);
        alerdialog.setTitle("Cập Nhật!!");
        alerdialog.setMessage("Hãy Chọn Trạng Thái Đơn Hàng");

        LayoutInflater inflater  = this.getLayoutInflater();
        final  View view = inflater.inflate(R.layout.update_order_layout, null);

        StatusSpinner = view.findViewById(R.id.StatusSpinner);
        ShipperSpinner = view.findViewById(R.id.ShipperSpinner);
        //load all shipper phone to spinner
        final List<String> shipperList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Shippers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot shipperSnapShot: dataSnapshot.getChildren())
                        {
                            shipperList.add(shipperSnapShot.getKey());
                        }
                        ShipperSpinner.setItems(shipperList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        StatusSpinner.setItems("Đã Đặt", "Trên đường Đến","Đang Lấy hàng");

        alerdialog.setView(view);

        final String localKey = key;
        alerdialog.setPositiveButton("Đồng Ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                item.setStatus(String.valueOf(StatusSpinner.getSelectedIndex()));

                if(item.getStatus().equals("2"))
                {

                    //COPPY ITEM TO TABLE "ORDER NEED SHIPPER"
                    FirebaseDatabase.getInstance().getReference("OrderNeedShipper")
                            .child(ShipperSpinner.getItems().get(ShipperSpinner.getSelectedIndex()).toString())
                            .child(localKey)
                            .setValue(item);


                    requests.child(localKey).setValue(item);
                    adapter.notifyDataSetChanged(); //add to update item size
                    sendOrderStatusToUser(localKey, item);
                    sendOrderStatusToShipper(ShipperSpinner.getItems().get(ShipperSpinner.getSelectedIndex()).toString());

                }
                else
                {
                    requests.child(localKey).setValue(item);
                    adapter.notifyDataSetChanged(); //add to update item size
                    sendOrderStatusToUser(localKey, item);
                }


            }
        });
        alerdialog.setNegativeButton("Huỷ Bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });

        alerdialog.show();
    }

    private void sendOrderStatusToUser(final String key ,final Request item) {

        DatabaseReference tokens = database.getReference("Tokens");
        tokens.child(item.getPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            Token token = dataSnapshot.getValue(Token.class);

                            Map<String, String> dataSend = new HashMap<>();
                            dataSend.put("tilte", "SellStore");
                            dataSend.put("message", "Đơn hàng Của Bạn " + key + " Đã Cập Nhật");
                            DataMessage dataMessage = new DataMessage(token.getToken(), dataSend);

                            mService.sendthongbao(dataMessage)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.body().success == 1) {
                                                Toast.makeText(Order_status.this, "Đơn Hàng Đã Cập Nhật", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(Order_status.this, "Không Thể Gửi", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR", t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void sendOrderStatusToShipper(String ShipperPhone) {

        DatabaseReference tokens = database.getReference("Tokens");
        tokens.child(ShipperPhone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            Token token = dataSnapshot.getValue(Token.class);

                            Map<String, String> dataSend = new HashMap<>();

                            dataSend.put("tilte", "Sell Store");
                            dataSend.put("message", "Bạn Có Đơn Hàng Mới Cần Vận Chuyển");
                            DataMessage dataMessage = new DataMessage(token.getToken(), dataSend);

                            mService.sendthongbao(dataMessage)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.body().success == 1) {
                                                Toast.makeText(Order_status.this, "Đã gửi đến shipper", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(Order_status.this, "Không thể Gửi", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR", t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}
