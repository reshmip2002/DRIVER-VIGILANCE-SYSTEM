package com.example.drivervigilancesystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class custom_view_feedback extends BaseAdapter {

    private final Context context;
    String[] n, f, d;

    public custom_view_feedback(Context applicationContext, String[] n, String[] f, String[] d) {

        this.context = applicationContext;
        this.n = n;
        this.f = f;
        this.d = d;

    }

    @Override
    public int getCount() {
        return d.length;
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
            gridView = inflator.inflate(R.layout.activity_custom_view_feedback, null);//same class name

        } else {
            gridView = (View) view;

        }
        TextView tv1 = (TextView) gridView.findViewById(R.id.textView5);
        TextView tv2 = (TextView) gridView.findViewById(R.id.textView6);
        TextView tv3 = (TextView) gridView.findViewById(R.id.textView7);
        tv1.setTextColor(Color.BLACK);//color setting
        tv2.setTextColor(Color.BLACK);
        tv3.setTextColor(Color.BLACK);


        tv1.setText(n[i]);
        tv2.setText(d[i]);
        tv3.setText(f[i]);

//

        return gridView;
    }
}