package com.sellproducts.thiennt.sellstoreSever;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sellproducts.thiennt.sellstoreSever.Common.Common;
import com.sellproducts.thiennt.sellstoreSever.Interface.ItemClickListener;
import com.sellproducts.thiennt.sellstoreSever.ViewHolder.MenuViewHolder;
import com.sellproducts.thiennt.sellstoreSever.model.Category;
import com.sellproducts.thiennt.sellstoreSever.model.Token;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;


public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference categorys;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    FirebaseStorage firebaseStorage ;
    StorageReference storageReference ;

    RecyclerView recyclerView_menu;
    RecyclerView.LayoutManager layoutManager;

    TextView txtFullName;

    //add new menu layout

    MaterialEditText edtName;
    FButton btnUpload, btnSelect;

    Category newCategory;

    //url
    Uri saveUri;

    ///
    DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Quản Lý");
        setSupportActionBar(toolbar);


        //Init firebase

        database = FirebaseDatabase.getInstance();
        categorys = database.getReference("Category");

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set name fo usser
        View hederView = navigationView.getHeaderView(0);
        txtFullName = (TextView) hederView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());
        recyclerView_menu = (RecyclerView) findViewById(R.id.recycle_menu);
        recyclerView_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_menu.setLayoutManager(layoutManager);

        loadCategory();

        //send token
        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    private void updateToken(String token) {
        FirebaseDatabase db =FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token,true); //fasle, vi token gui tu clien app
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }


    //show dia log add category


    private void showDialog() {

        AlertDialog.Builder alerdialog = new AlertDialog.Builder(Home.this);
        alerdialog.setTitle("Thêm Danh Mục Sản Phẩm ");
        alerdialog.setMessage("Hãy Điển Đâỳ Đủ Thông Tin");
        LayoutInflater inflater = this.getLayoutInflater();

        View add_category_layout = inflater.inflate(R.layout.add_category_layout, null);
        edtName = add_category_layout.findViewById(R.id.edtName);
        btnUpload = add_category_layout.findViewById(R.id.btnUpload);
        btnSelect = add_category_layout.findViewById(R.id.btnSelect);
        //event choose imgae

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();// let user  select image from gallery and save uri of  this image
            }
        });
        //event btn upload

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });



        alerdialog.setView(add_category_layout);
        alerdialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //set button
        alerdialog.setPositiveButton("Đồng Ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                //create new category
                if(newCategory != null)
                {
                    categorys.push().setValue(newCategory);
                    Snackbar.make(drawer, "Danh Mục Mới " + newCategory.getName()+" Đã Thêm", Snackbar.LENGTH_SHORT).show();

                }

            }
        });

        alerdialog.setNegativeButton("Huỷ Bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        alerdialog.show();


    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Chọn Hình"),Common.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {
            saveUri = data.getData();
            btnSelect.setText("Đã Chọn");
        }
    }

    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Đang Tải...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(Home.this, "Đã Tải Lên!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // set value for new category if image upload and we can get dowload link
                                    newCategory = new Category(edtName.getText().toString(),uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    // load category

    private void loadCategory() {

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(categorys, Category.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, int position, @NonNull Category model) {

                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(Home.this).load(model.getImage())
                        .into(viewHolder.imageView);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClik) {
                        ///send categoryid den new activity
                        Intent foodList = new Intent(Home.this, Products_list.class);
                        foodList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View iteamView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(iteamView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView_menu.setAdapter(adapter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        }
        else if (id == R.id.nav_message) {
            Intent message = new Intent(Home.this, SendMessage.class) ;
            startActivity(message);

        }else if (id == R.id.nav_Banner) {
            Intent banner = new Intent(Home.this, BannerActivity.class) ;
            startActivity(banner);
        } else if (id == R.id.nav_order) {

            Intent orderIntent = new Intent(Home.this, Order_status.class) ;
            startActivity(orderIntent);

        } else if (id == R.id.nav_signout) {

        } else if (id == R.id.nav_shipper) {
            Intent shipperIntent = new Intent(Home.this, ShipperManegerment.class) ;
            startActivity(shipperIntent);

        }
        else if (id == R.id.nav_rp) {

            Intent ReportIntent = new Intent(Home.this, Report.class) ;
            startActivity(ReportIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE))
        {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteCategory(String key) {
        categorys.child(key).removeValue();
        Toast.makeText(this, "Danh Mục Đã Được Xoá!", Toast.LENGTH_SHORT).show();
    }


    private void showUpdateDialog(final String key, final Category item) {

        AlertDialog.Builder alerdialog = new AlertDialog.Builder(Home.this);
        alerdialog.setTitle("Cập Nhật Danh Mục Sản Phẩm ");
        alerdialog.setMessage("Làm Ơn Điển Đâỳ Đủ Thông Tin..");
        LayoutInflater inflater = this.getLayoutInflater();

        View add_category_layout = inflater.inflate(R.layout.add_category_layout, null);
        edtName = add_category_layout.findViewById(R.id.edtName);
        btnUpload = add_category_layout.findViewById(R.id.btnUpload);
        btnSelect = add_category_layout.findViewById(R.id.btnSelect);
        //set default name
        edtName.setText(item.getName());
        //event choose imgae


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();// let user  select image from gallery and save uri of  this image
            }
        });
        //event btn upload

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });



        alerdialog.setView(add_category_layout);
        alerdialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //set button
        alerdialog.setPositiveButton("Đồng Ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                //update infomation
                item.setName(edtName.getText().toString());
                categorys.child(key).setValue(item);

            }
        });

        alerdialog.setNegativeButton("Huỷ Bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alerdialog.show();
    }

    private void changeImage(final Category item) {

        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Đang Tải...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(Home.this, "Đã Tải Lên!!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // set value for new category if image upload and we can get dowload link
                                    item.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null)
        {
            adapter.startListening();
        }


    }
}
