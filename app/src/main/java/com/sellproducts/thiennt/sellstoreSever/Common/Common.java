package com.sellproducts.thiennt.sellstoreSever.Common;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.format.DateFormat;

import com.sellproducts.thiennt.sellstoreSever.Remote.APIService;
import com.sellproducts.thiennt.sellstoreSever.Remote.RetrofitService;
import com.sellproducts.thiennt.sellstoreSever.model.Request;
import com.sellproducts.thiennt.sellstoreSever.model.User;

import java.util.Calendar;
import java.util.Locale;

public class    Common {
    public  static String topicName = "News";
    public static User currentUser;
   public static Request currentRequest;

    public static final String UPDATE = "Cập Nhật";
    public static final String DELETE = "Xoá";
//
      public static final int PICK_IMAGE_REQUEST = 71;

    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMservice()
    {
        return RetrofitService.getclient(BASE_URL).create(APIService.class);
    }
//
public static String convertCodeToStatus(String code){
    if (code.equals("0"))
        return "Đã Đặt";
    else if (code.equals("1"))
        return "Trên đường Đến";
    else if (code.equals("2"))
        return "Đang Lấy hàng";
    else
        return "Đã Giao Hàng";
}


    public static Bitmap scaleBitmap (Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaleBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth/(float)bitmap.getWidth();
        float scaleY = newHeight/(float)bitmap.getHeight();
        float pivotX=0, pivotY=0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaleBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaleBitmap;
    }

    public  static String getDate(long time)
    {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(DateFormat.format("dd-MM-yyyy HH:mm", calendar).toString());
        return  date.toString();
    }
}
