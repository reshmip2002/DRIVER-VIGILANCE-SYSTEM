package com.example.drivervigilancesystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class custom_view_drivers extends BaseAdapter {

    private final Context context;
    String[] driver_id,driver_name,address,age,blood_group,license_number,aadhar_number,phone_number,photo;

    public custom_view_drivers(Context applicationContext, String[] driver_id, String[] driver_name, String[] address, String[] age, String[] blood_group, String[] license_number, String[] aadhar_number, String[] phone_number, String[] photo) {


        this.context = applicationContext;
        this.driver_id = driver_id;
        this.driver_name =driver_name ;
        this. address = address;
        this.age = age;
        this.blood_group= blood_group;
        this.license_number =license_number ;
        this.aadhar_number = aadhar_number;
        this.phone_number =phone_number ;
        this.photo = photo;

    }
    @Override
    public int getCount() {
        return age.length;
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
            gridView = inflator.inflate(R.layout.activity_custom_view_drivers, null);//same class name

        } else {
            gridView = (View) view;

        }
        TextView tv1 = (TextView) gridView.findViewById(R.id.textView25);
        TextView tv2 = (TextView) gridView.findViewById(R.id.textView26);
        TextView tv3 = (TextView) gridView.findViewById(R.id.textView27);
        TextView tv4 = (TextView) gridView.findViewById(R.id.textView28);
        TextView tv5 = (TextView) gridView.findViewById(R.id.textView29);
        TextView tv6 = (TextView) gridView.findViewById(R.id.textView44);
        TextView tv7 = (TextView) gridView.findViewById(R.id.textView43);
        ImageView im = (ImageView) gridView.findViewById(R.id.imageView3);
        Button b=(Button) gridView.findViewById(R.id.button11);
        b.setTag(i);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos=(int) view.getTag();
                SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor ed=sh.edit();
                ed.putString("driver_id",driver_id[pos]);
                ed.commit();
                Intent i=new Intent(context.getApplicationContext(),view_message.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

        tv1.setTextColor(Color.RED);//color setting
        tv2.setTextColor(Color.BLACK);
        tv3.setTextColor(Color.BLACK);
        tv4.setTextColor(Color.BLACK);
        tv5.setTextColor(Color.BLACK);
        tv6.setTextColor(Color.BLACK);
        tv7.setTextColor(Color.BLACK);



        tv1.setText(driver_name[i]);
        tv2.setText(address[i]);
        tv3.setText(age[i]);
        tv4.setText(blood_group[i]);
        tv5.setText(license_number[i]);
        tv6.setText(aadhar_number[i]);
        tv7.setText(phone_number[i]);


        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);
        String ip = sh.getString("ip", "");
        String url = "http://" + ip + ":5000" + photo[i];
        Picasso.with(context).load(url).transform(new CircleTransform()).into(im);//circle
//

        return gridView;
    }
}