package com.sellproducts.thiennt.sellstoreSever;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sellproducts.thiennt.sellstoreSever.Common.Common;
import com.sellproducts.thiennt.sellstoreSever.model.User;

import info.hoang8f.widget.FButton;

public class Login extends AppCompatActivity {
    EditText edtPhone, edtPassword;
    FButton btnlogin;

    FirebaseDatabase db;
    DatabaseReference users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);

        btnlogin =(FButton) findViewById(R.id.btnlogin);

        db = FirebaseDatabase.getInstance();
        users = db.getReference("User");

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SigninUser(edtPhone.getText().toString(), edtPassword.getText().toString());
            }
        });


    }

    private void SigninUser(String phone, String password) {

        final ProgressDialog dialog = new ProgressDialog(Login.this);
        dialog.setMessage("Vui Lòng Chờ...");
        dialog.show();

        final String localPhone = phone;
        final  String localpassword = password;

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(localPhone).exists())
                {
                    dialog.dismiss();
                    User user  = dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);

                    if(Boolean.parseBoolean(user.getIsStaff()))
                    {
                        if(user.getPassword().equals(localpassword))
                        {
                            Toast.makeText(Login.this, "Đăng Nhập Thành Công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, Home.class);
                            Common.currentUser = user;
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(Login.this, "Mật Khẩu", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(Login.this, "Đăng Nhập Với Tài Khoản Nhân Viên", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(Login.this, "Tài Khoản Không Tồn Tại", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
