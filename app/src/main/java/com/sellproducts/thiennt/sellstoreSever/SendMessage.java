package com.sellproducts.thiennt.sellstoreSever;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.sellproducts.thiennt.sellstoreSever.Common.Common;
import com.sellproducts.thiennt.sellstoreSever.Remote.APIService;
import com.sellproducts.thiennt.sellstoreSever.model.DataMessage;
import com.sellproducts.thiennt.sellstoreSever.model.MyResponse;

import java.util.HashMap;
import java.util.Map;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMessage extends AppCompatActivity {

    MaterialEditText edtTitle, edtMessage;
    FButton btnSend;
    APIService mSerice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        edtTitle = (MaterialEditText) findViewById(R.id.edtTitel);
        edtMessage = (MaterialEditText) findViewById(R.id.edtMessage);

        btnSend = (FButton) findViewById(R.id.btnSend);

        mSerice = Common.getFCMservice();


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> dataSend = new HashMap<>();
                dataSend.put("tilte", edtTitle.getText().toString());
                dataSend.put("message", edtMessage.getText().toString());
                DataMessage dataMessage = new DataMessage(new StringBuilder("/topics/").append(Common.topicName).toString(), dataSend);

                mSerice.sendthongbao(dataMessage).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if (response.isSuccessful())
                            Toast.makeText(SendMessage.this, "Tin Nhắn Đã Gửi", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                        Toast.makeText(SendMessage.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                edtMessage.setText("");
                edtTitle.setText("");
            }
        });


    }
}
