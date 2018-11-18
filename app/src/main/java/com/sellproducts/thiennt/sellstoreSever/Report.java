package com.sellproducts.thiennt.sellstoreSever;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sellproducts.thiennt.sellstoreSever.model.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Report extends AppCompatActivity {

    private BarChart barChart;
    FirebaseDatabase database;
    DatabaseReference request;

    private Map<String, Integer> districtIndexMap = null;

   private String[] districtDisplay = new String[] {"Q1","Q2","Q3","Q4"
            ,"Q5","Q6", "Q7","Q8","Q9","Q10", "Q11",
            "Q12","TBình", "BTân","TĐức","GVấp","BThạnh"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        barChart = (BarChart) findViewById(R.id.barChart);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);
        barChart.setDescription(null);


        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {


            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private void displayChart(ArrayList<Integer> chartData) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < chartData.size(); i++)
        {
            entries.add(new BarEntry(i, chartData.get(i)));
        }
        BarDataSet dataSet = new BarDataSet(entries, "Số Đơn Hàng Theo Quận");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.5f);
        data.setValueTextSize(9f);
        barChart.setData(data);
        dataSet.setValueTextColor(Color.rgb(205,55, 0));


        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new MyxAxisValueFormatter(districtDisplay));
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setLabelCount(17);
    }

    public class MyxAxisValueFormatter implements IAxisValueFormatter
    {
        private String[] mValues;

        public MyxAxisValueFormatter(String[] values)
        {
            this.mValues = values;
        }
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int)value];
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        database = FirebaseDatabase.getInstance();
        request = database.getReference("Reports");
        request.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> addresses = new ArrayList<>();
                for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren() ) {
                    Request requestvalue = myDataSnapshot.getValue(Request.class);
                    String address = requestvalue.getAddress();
                    addresses.add(address);
                }

                ArrayList<Integer> chartData = fromAddressesToChartData(addresses);
                displayChart(chartData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private ArrayList<Integer> fromAddressesToChartData(ArrayList<String> addresses) {
        Map<String, Integer> districtIndexMap = getDistrictIndexMap();
        int districtMapSize = districtIndexMap.size();
        ArrayList<Integer> chartData = new ArrayList<>(districtMapSize);
        for(int i = 0; i < districtMapSize; i++) {
            chartData.add(i, 0);
        }
        for(String address : addresses) {
            for(Map.Entry<String, Integer> districtIndex: districtIndexMap.entrySet()){
                String district = districtIndex.getKey();
                if (address.contains(district)) {
                    if (district.equals("Quận 1") && (address.contains("Quận 10") || address.contains("Quận 11") || address.contains("Quận 12"))) {
                        continue;
                    }
                    int index = districtIndex.getValue();
                    int old_value = chartData.get(index);
                    chartData.set(index, old_value + 1);
                    break;
                }
            }
        }

        return chartData;
    }

    private Map<String, Integer> getDistrictIndexMap() {
        if (districtIndexMap == null) {
            districtIndexMap = new HashMap<String, Integer>();
            districtIndexMap.put("Quận 1", 0);
            districtIndexMap.put("Quận 2", 1);
            districtIndexMap.put("Quận 3", 2);
            districtIndexMap.put("Quận 4", 3);
            districtIndexMap.put("Quận 5", 4);
            districtIndexMap.put("Quận 6", 5);
            districtIndexMap.put("Quận 7", 6);
            districtIndexMap.put("Quận 8", 7);
            districtIndexMap.put("Quận 9", 8);
            districtIndexMap.put("Quận 10", 9);
            districtIndexMap.put("Quận 11", 10);
            districtIndexMap.put("Quận 12", 11);
            districtIndexMap.put("Tân Bình", 12);
            districtIndexMap.put("Bình Tân", 13);
            districtIndexMap.put("Thủ Đức", 14);
            districtIndexMap.put("Gò Vấp", 15);
            districtIndexMap.put("Bình Thạnh", 16);


        }
        return districtIndexMap;
    }


}
