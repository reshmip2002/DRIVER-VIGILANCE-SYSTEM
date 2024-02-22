package com.example.drivervigilancesystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class custom_view_driving_logs extends BaseAdapter {

    private final Context context;
    String[]  name,speed,angle,date,time;

    public custom_view_driving_logs(Context applicationContext, String[] name, String[] speed, String[] angle, String[] date, String[] time) {


        this.context = applicationContext;
        this.name = name;
        this.speed =speed ;
        this.angle = angle;
        this.date= date;
        this.time =time ;

    }
    @Override
    public int getCount() {
        return time.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        if (view == null) {
            gridView = new View(context);
            //gridView=inflator.inflate(R.layout.customview, null);
            gridView = inflator.inflate(R.layout.activity_custom_view_driving_logs, null);//same class name

        } else {
            gridView = (View) view;

        }
        TextView tv1 = (TextView) gridView.findViewById(R.id.textView16);
        TextView tv2 = (TextView) gridView.findViewById(R.id.textView17);
        TextView tv4 = (TextView) gridView.findViewById(R.id.textView19);
        TextView tv5 = (TextView) gridView.findViewById(R.id.textView30);
        TextView tv6 = (TextView) gridView.findViewById(R.id.textView31);


        tv1.setTextColor(Color.BLACK);//color setting
        tv2.setTextColor(Color.BLACK);
        tv4.setTextColor(Color.BLACK);
        tv5.setTextColor(Color.BLACK);
        tv6.setTextColor(Color.BLACK);



        tv1.setText(name[i]);
        tv2.setText(speed[i]);
        tv4.setText(angle[i]);
        tv5.setText(date[i]);
        tv6.setText(time[i]);



//

        return gridView;
    }
}

