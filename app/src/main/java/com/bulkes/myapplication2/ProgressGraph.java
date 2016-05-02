package com.bulkes.myapplication2;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by progr on 07.04.2016.
 */
public class ProgressGraph extends AppCompatActivity {
    private LineChart lineChart;
    private Activity graphActivity;
    private Date date;
    private SimpleDateFormat sdf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.animator.activity_down_up_enter,R.animator.activity_down_up_exit);
        setContentView(R.layout.graph_layout);
        date = new Date(0);
        sdf = new SimpleDateFormat("m:ss");
        graphActivity = this;
        lineChart = (LineChart)findViewById(R.id.graphChart);
        lineChart.setHardwareAccelerationEnabled(true);
        findViewById(R.id.close_graph_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphActivity.finish();
            }
        });
        findViewById(R.id.save_graph_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                lineChart.saveToGallery("BulkesGraph_" + timeStamp + ".jpg",100);
                Toast.makeText(graphActivity,/*"GraphShot is saved"*/getResources().getString(R.string.digramm_shot),Toast.LENGTH_SHORT).show();
            }
        });
        lineChart.setDescription(null);
        lineChart.setData(getData());
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.animateXY(1500,1000);
    }

    private LineData getData()
    {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for(int i = 0; i<CriticalData.usersMass.size(); i++)
        {
            int userMass = CriticalData.usersMass.get(i);
            entries.add(new Entry(userMass,i*2));
            date.setTime(i*2000);
            labels.add(sdf.format(date));
        }
        LineDataSet dataset = new LineDataSet(entries, /*"Player`s mass"*/getResources().getString(R.string.diagram_legend));
        dataset.setDrawFilled(true);
        dataset.setLineWidth(3f);
        return  (new LineData(labels,dataset));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setFullScreenMode();
    }

    private void setFullScreenMode()
    {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            graphActivity.finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        overridePendingTransition(R.animator.activity_down_up_close_enter,R.animator.activity_down_up_close_exit);
    }
    @Override
    public void onStop()
    {
        super.onStop();
    }
}
