package com.example.drivervigilancesystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class View_drivers extends AppCompatActivity {

//    ListView li;
//    String[] driver_id,driver_name,address,age,blood_group,license_number,aadhar_number,phone_number,photo;
    TextView driver_id,driver_name,address,age,blood_group,license_number,aadhar_number,phone_number,photo;
    String url;
    SharedPreferences sh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_drivers);
//        li=findViewById(R.id.list);
        TextView name =findViewById(R.id.textView25);
        TextView address =findViewById(R.id.textView26);
        TextView age =findViewById(R.id.textView27);
        TextView blood_group =findViewById(R.id.textView28);
        TextView license_number =findViewById(R.id.textView29);
        TextView aadhar_number =findViewById(R.id.textView44);
        TextView phone_number =findViewById(R.id.textView43);
        ImageView im = findViewById(R.id.imageView3);
        Button b=findViewById(R.id.button11);
        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sh.getString("ip","");
        sh.getString("url","");
        url=sh.getString("url","")+"and_view_drivers";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            if (jsonObj.getString("status").equalsIgnoreCase("ok")) {
                                JSONObject jj = jsonObj.getJSONObject("data");
                                String did=jj.getString("driver_id");
                                SharedPreferences.Editor ed = sh.edit();
                                ed.putString("driver_id", did);
                                ed.commit();
                                name.setText(jj.getString("driver_name"));
                                String pl=jj.getString("place");
                                String po=jj.getString("post");
                                String pi=jj.getString("pin");
                                address.setText(pl+"\n"+po+"\n"+pi+"\n");
                                age.setText(jj.getString("age"));
                                blood_group.setText(jj.getString("blood_group"));
                                license_number.setText(jj.getString("license_no"));
                                aadhar_number.setText(jj.getString("aadhar_no"));
                                phone_number.setText(jj.getString("phone_no"));


                                String image = jj.getString("photo");
                                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                String ip = sh.getString("ip", "");
                                String url = "http://" + ip + ":5000" + image;
                                Picasso.with(getApplicationContext()).load(url).transform(new CircleTransform()).into(im);//circle

                            } else {
                                Toast.makeText(getApplicationContext(), "Not found", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(getApplicationContext(), "eeeee" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {

            //                value Passing android to python
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", sh.getString("lid", ""));//passing to python

                return params;
            }
        };
        int MY_SOCKET_TIMEOUT_MS = 100000;

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(postRequest);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),view_message.class);
                startActivity(i);
            }
        });



    }
}