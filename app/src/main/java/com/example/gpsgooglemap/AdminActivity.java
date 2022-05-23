package com.example.gpsgooglemap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;




public class AdminActivity extends AppCompatActivity {
    Boolean isAdmin;
    int colorRed = Color.RED;
    int colorGreen = Color.GREEN;
    int colorYellow = Color.YELLOW;
    private String alt, fuel, oil, timebegin, timeend;
    private int fuelColor, oilColor;
    TextView begin, end;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Intent intent = getIntent();
        isAdmin = intent.getExtras().getBoolean("extraRules");
        alt = intent.getExtras().getString("altitude");
        fuel = intent.getExtras().getString("fuel");
        oil = intent.getExtras().getString("oil");
        timebegin = intent.getExtras().getString("timebegin");
        timeend = intent.getExtras().getString("timeend");
        Toast.makeText(this, "Admin permission: " + String.valueOf(isAdmin), Toast.LENGTH_SHORT).show();
        BarChart bchart = (BarChart) findViewById(R.id.chart1);
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        begin = findViewById(R.id.textView2);
        end = findViewById(R.id.textView);
        begin.setText("Время вылета:          " + timebegin);
        end.setText("Время прилета:         " + timeend);
        if(isAdmin) {

            float val = Float.parseFloat(fuel) / 100;

            yVals1.add(new BarEntry(2, val));
            val = Float.parseFloat(oil) / 100;

            yVals1.add(new BarEntry(3, val));


            BarDataSet set1;

            set1 = new BarDataSet(yVals1, "fuel, oil");
            if (Integer.parseInt(fuel) > 70) fuelColor = colorGreen;
            if (Integer.parseInt(fuel) > 50 && Integer.parseInt(fuel) < 70) fuelColor = colorYellow;
            if (Integer.parseInt(fuel) > 0 && Integer.parseInt(fuel) < 50) fuelColor = colorRed;
            if (Integer.parseInt(oil) > 80) oilColor = colorGreen;
            if (Integer.parseInt(oil) > 60 && Integer.parseInt(oil) < 80) oilColor = colorYellow;
            if (Integer.parseInt(oil) > 0 && Integer.parseInt(oil) < 60) oilColor = colorRed;
            set1.setColors(fuelColor, oilColor);
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);

            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);
            bchart.getAxisRight().setStartAtZero(true);
            bchart.getAxisLeft().setStartAtZero(true);
            bchart.getAxisLeft().setAxisMaximum(1);
            bchart.getAxisRight().setAxisMaximum(1);
            bchart.setTouchEnabled(false);
            bchart.setData(data);
        }

    }
}