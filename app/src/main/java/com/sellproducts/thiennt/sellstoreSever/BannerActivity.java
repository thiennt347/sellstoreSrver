package com.sellproducts.thiennt.sellstoreSever;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sellproducts.thiennt.sellstoreSever.Common.Common;
import com.sellproducts.thiennt.sellstoreSever.ViewHolder.BannerViewHolder;
import com.sellproducts.thiennt.sellstoreSever.model.Banner;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import info.hoang8f.widget.FButton;

public class BannerActivity extends AppCompatActivity {

    RecyclerView recycler_Banner ;
    RecyclerView.LayoutManager layoutManager ;
    RelativeLayout Banner_list_layout;
    FloatingActionButton fab;

    FirebaseDatabase database;
    DatabaseReference bannerlist;
    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseRecyclerAdapter<Banner, BannerViewHolder> adapter;

    MaterialEditText edtName, edtProductId;
    FButton btnSelect, btnUpload;

    Banner newbanner;

    Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        database = FirebaseDatabase.getInstance();
        bannerlist = database.getReference("Banner");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        recycler_Banner = (RecyclerView) findViewById(R.id.recycler_Banner);
        recycler_Banner.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_Banner.setLayoutManager(layoutManager);
        Banner_list_layout = (RelativeLayout) findViewById(R.id.Banner_list_layout);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //
                showBannerDialog();

            }
        });

        loadListBanner();
    }

    private void loadListBanner() {
        FirebaseRecyclerOptions<Banner> listBanner = new FirebaseRecyclerOptions.Builder<Banner>()
                .setQuery(bannerlist, Banner.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Banner, BannerViewHolder>(listBanner) {
            @Override
            protected void onBindViewHolder(@NonNull BannerViewHolder holder, int position, @NonNull Banner model) {

                holder.Banner_name.setText(model.getName());
                Picasso.with(getBaseContext())
                        .load(model.getImage())
                        .into(holder.Banner_image);
            }
            @NonNull
            @Override
            public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.banner_layout,parent, false);
                return new  BannerViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recycler_Banner.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void showBannerDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BannerActivity.this);
        alertDialog.setTitle("Thêm Banner Mới");
        alertDialog.setMessage("Điển Đầy Đủ Thông Tin!");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_banner_layout = inflater.inflate(R.layout.add_new_banner_layout, null);

        edtName  = add_banner_layout.findViewById(R.id.edtNameProduct);
        edtProductId = add_banner_layout.findViewById(R.id.edtProductId);

        btnSelect = add_banner_layout.findViewById(R.id.btnSelect);
        btnUpload = add_banner_layout.findViewById(R.id.btnUpload);

        //Event for Button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setView(add_banner_layout);
        alertDialog.setIcon(R.drawable.ic_crop_landscape_black_24dp);

        //set button for dialog
        alertDialog.setPositiveButton("Đồng Ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(newbanner != null)
                {
                    bannerlist.push().setValue(newbanner);
                }
                loadListBanner();

            }
        });
        alertDialog.setNegativeButton("Huỷ Bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                newbanner = null;
                loadListBanner();
            }
        });
        alertDialog.show();
    }

    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Đang Tải...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(BannerActivity.this, "Đã Tải Lên!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // atur nilai untuk newCategory jika gambar upload dan kita ambil download linknya.
                                    newbanner = new Banner();
                                    newbanner.setName(edtName.getText().toString());
                                    newbanner.setId(edtProductId.getText().toString());
                                    newbanner.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(BannerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Đang Tải "+progress+" %");
                        }
                    });
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Chọn Hình"), Common.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            btnSelect.setText("Đã Chọn");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE))
        {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DELETE))
        {
            showDeleteDialog(adapter.getRef(item.getOrder()).getKey());
            Toast.makeText(this, "Banner Đã Xoá", Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
    }

    private void showDeleteDialog(String key) {
        bannerlist.child(key).removeValue();
    }

    private void showUpdateDialog(final String key, final Banner item)  {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BannerActivity.this);
        alertDialog.setTitle("Chỉnh Sửa Banner");
        alertDialog.setMessage("Điển Đầy Đủ Thông Tin!");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_banner_layout = inflater.inflate(R.layout.add_new_banner_layout, null);

        edtName  = add_banner_layout.findViewById(R.id.edtNameProduct);
        edtProductId = add_banner_layout.findViewById(R.id.edtProductId);


        //set default vale for  view
        edtName.setText(item.getName());
        edtProductId.setText(item.getId());

        btnSelect = add_banner_layout.findViewById(R.id.btnSelect);
        btnUpload = add_banner_layout.findViewById(R.id.btnUpload);

        //Event for Button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });

        alertDialog.setView(add_banner_layout);
        alertDialog.setIcon(R.drawable.ic_crop_landscape_black_24dp);

        //Set button
        alertDialog.setPositiveButton("Cập Nhật", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();

                //update infomation
                item.setName(edtName.getText().toString());
                item.setId(edtProductId.getText().toString());

                //make update
                Map<String, Object> update = new HashMap<>();
                update.put("id", item.getId());
                update.put("name", item.getName());
                update.put("image", item.getImage());

                bannerlist.child(key).updateChildren(update)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Snackbar.make(Banner_list_layout, "Banner Mới "+ item.getName()+" Đã Cập Nhật", Snackbar.LENGTH_SHORT)
                                        .show();
                                loadListBanner();
                            }
                        });

                Snackbar.make(Banner_list_layout, "Banner Mới"+ item.getName()+"Đã Cập Nhật", Snackbar.LENGTH_SHORT)
                        .show();
                loadListBanner();


            }
        });
        alertDialog.setNegativeButton("Huỷ Bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                loadListBanner();
            }
        });
        alertDialog.show();
    }

    private void changeImage(final Banner item) {
        if (filePath != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Đang Tải...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(BannerActivity.this, "Đã Tải Lên!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // atur nilai untuk newCategory jika gambar upload dan kita ambil download linknya.
                                    item.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(BannerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Đang Tải "+progress+" %");
                        }
                    });
        }
    }
}
