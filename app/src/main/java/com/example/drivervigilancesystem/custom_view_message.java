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

public class custom_view_message extends BaseAdapter {

    String[] mid,date, msg;
    private Context context;


    public custom_view_message(Context applicationContext,String[] mid, String[] date, String[] msg) {
        this.context = applicationContext;
        this.mid = mid;
        this.date = date;
        this.msg = msg;

    }


    @Override
    public int getCount() {
        return msg.length;
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
        LayoutInflater inflator=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        if(view==null)
        {
            gridView=new View(context);
            //gridView=inflator.inflate(R.layout.customview, null);
            gridView=inflator.inflate(R.layout.activity_custom_view_message,null);//same class name

        }
        else
        {
            gridView=(View)view;

        }
        TextView tv1=(TextView)gridView.findViewById(R.id.textView51);
        TextView tv2=(TextView)gridView.findViewById(R.id.textView54);



        tv1.setTextColor(Color.BLACK);//color setting
        tv2.setTextColor(Color.BLACK);


        tv1.setText(date[i]);
        tv2.setText(msg[i]);

        return gridView;





    }
}