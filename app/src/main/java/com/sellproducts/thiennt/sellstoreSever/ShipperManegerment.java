package com.sellproducts.thiennt.sellstoreSever;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sellproducts.thiennt.sellstoreSever.ViewHolder.ShipperViewHolder;
import com.sellproducts.thiennt.sellstoreSever.model.Shipper;

import java.util.HashMap;
import java.util.Map;

public class ShipperManegerment extends AppCompatActivity {

    FloatingActionButton fab_add;

    FirebaseDatabase database;
    DatabaseReference shippers;

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Shipper, ShipperViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_manegerment);

        fab_add = (FloatingActionButton)findViewById(R.id.fab_add);
        recyclerView = (RecyclerView)findViewById(R.id.recycle_Shipper);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);


        //firebase
        database = FirebaseDatabase.getInstance();
        shippers = database.getReference("Shippers");

        //load tat ca shipper

        loadALlShippers();

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShipperLayout();
            }
        });

    }

    private void loadALlShippers() {

        FirebaseRecyclerOptions<Shipper> options = new FirebaseRecyclerOptions.Builder<Shipper>()
                .setQuery(shippers, Shipper.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Shipper, ShipperViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ShipperViewHolder holder, final int position, @NonNull final Shipper model) {

                holder.shipper_phone.setText(model.getPhone());
                holder.shipper_name.setText(model.getName());
                holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditDialog(adapter.getRef(position).getKey(), model);
                    }
                });
                holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showRemoveDialog(adapter.getRef(position).getKey());
                    }
                });
            }

            @NonNull
            @Override
            public ShipperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shipper_layout, parent, false);
                return new ShipperViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void showEditDialog(String key, Shipper model) {
        AlertDialog.Builder create_shipper_layout = new AlertDialog.Builder(ShipperManegerment.this);
        create_shipper_layout.setTitle("Chỉnh Sửa Shipper");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.create_shipper_layout,null);

        final MaterialEditText editName = (MaterialEditText)view.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = (MaterialEditText)view.findViewById(R.id.edtPhone);
        final MaterialEditText edtPassword = (MaterialEditText)view.findViewById(R.id.edtPassword);

        //setdata
        editName.setText(model.getName());
        edtPhone.setText(model.getPhone());
        edtPassword.setText( model.getPassword());



        create_shipper_layout.setView(view);
        create_shipper_layout.setIcon(R.drawable.ic_local_shipping_black_24dp);
        create_shipper_layout.setPositiveButton("Đồng Ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                Map<String, Object> update = new HashMap<>();
                update.put("name", editName.getText().toString());
                update.put("phone", editName.getText().toString());
                update.put("password", editName.getText().toString());

                shippers.child(edtPhone.getText().toString())
                        .updateChildren(update)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(ShipperManegerment.this, "Shipper Đã Cập Nhật", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ShipperManegerment.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        create_shipper_layout.setNegativeButton("Huỷ Bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        create_shipper_layout.show();

    }

    private void showRemoveDialog(String key) {

            shippers.child(key)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ShipperManegerment.this, "Xoá Shipper Thành Công", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ShipperManegerment.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            adapter.notifyDataSetChanged();
    }


    private void showShipperLayout() {

        AlertDialog.Builder create_shipper_layout = new AlertDialog.Builder(ShipperManegerment.this);
        create_shipper_layout.setTitle("Tạo Shipper");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.create_shipper_layout,null);

        final MaterialEditText editName = (MaterialEditText)view.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = (MaterialEditText)view.findViewById(R.id.edtPhone);
        final MaterialEditText edtPassword = (MaterialEditText)view.findViewById(R.id.edtPassword);

        create_shipper_layout.setView(view);
        create_shipper_layout.setIcon(R.drawable.ic_local_shipping_black_24dp);
        create_shipper_layout.setPositiveButton("Đồng Ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                Shipper shipper = new Shipper();
                shipper.setName(editName.getText().toString());
                shipper.setPhone(edtPhone.getText().toString());
                shipper.setPassword(edtPassword.getText().toString());

                shippers.child(edtPhone.getText().toString())
                        .setValue(shipper)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(ShipperManegerment.this, "Shipper Tạo Thành Công", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ShipperManegerment.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        create_shipper_layout.setNegativeButton("Huỷ Bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        create_shipper_layout.show();

    }
}
