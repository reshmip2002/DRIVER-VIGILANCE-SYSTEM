package com.example.drivervigilancesystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class custom_view_locations extends BaseAdapter {
    private final Context context;
    String[] date, time,place,latitude, longitude;

    public custom_view_locations(Context applicationContext, String[] date, String[] time, String[] place, String[] latitude, String[] longitude) {

        this.context = applicationContext;
        this.date = date;
        this.time = time;
        this.place = place;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    @Override
    public int getCount() {
        return date.length;
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
            gridView = inflator.inflate(R.layout.activity_custom_view_locations, null);//same class name

        } else {
            gridView = (View) view;

        }
        TextView tv1 = (TextView) gridView.findViewById(R.id.textView35);
        TextView tv2 = (TextView) gridView.findViewById(R.id.textView36);
        TextView tv3 = (TextView) gridView.findViewById(R.id.textView37);
        Button b = (Button) gridView.findViewById(R.id.button17);
        tv1.setTextColor(Color.RED);//color setting
        tv2.setTextColor(Color.BLACK);
        tv3.setTextColor(Color.BLACK);


        tv1.setText(date[i]);
        tv2.setText(time[i]);
        tv3.setText(place[i]);


        b.setTag(i);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos=(int)view.getTag();
                String url="http://maps.google.com/?q="+latitude[pos]+","+longitude[pos];
                Intent j = new Intent(Intent.ACTION_VIEW);
                j.setData(Uri.parse(url));
                j.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(j);

            }
        });

        return gridView;
    }
}



