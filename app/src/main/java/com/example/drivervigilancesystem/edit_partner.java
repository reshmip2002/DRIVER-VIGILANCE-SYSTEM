package com.example.drivervigilancesystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class edit_partner extends AppCompatActivity {

    EditText e1, e2, e3, e4, e5, e6, e7;
    Button b;
    String url;
    SharedPreferences sh;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_partner);
        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sh.getString("ip","");
        sh.getString("url","");
        url=sh.getString("url","")+"/and_view_mypartner";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            if (jsonObj.getString("status").equalsIgnoreCase("ok")) {
                                JSONObject jj=jsonObj.getJSONObject("data");
                                e1.setText(jj.getString("partner_name"));
                                e2.setText(jj.getString("place"));
                                e3.setText(jj.getString("post"));
                                e4.setText(jj.getString("pin"));
                                e5.setText(jj.getString("aadhar_no"));
                                e6.setText(jj.getString("email"));
                                e7.setText(jj.getString("phone_no"));
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

                params.put("pid", sh.getString("pid",""));//passing to python


                return params;
            }
        };


        int MY_SOCKET_TIMEOUT_MS = 100000;

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(postRequest);

        e1 = findViewById(R.id.editTextTextPersonName15);
        e2 = findViewById(R.id.editTextTextPersonName17);
        e3 = findViewById(R.id.editTextTextPersonName19);
        e4 = findViewById(R.id.editTextTextPersonName20);
        e5 = findViewById(R.id.editTextTextPersonName21);
        e6 = findViewById(R.id.editTextTextEmailAddress);
        e7 = findViewById(R.id.editTextPhone2);
        b = findViewById(R.id.button9);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flg = 0;
                String name = e1.getText().toString();
                String place = e2.getText().toString();
                String post = e3.getText().toString();
                String pin = e4.getText().toString();
                String aadhar_number = e5.getText().toString();
                String email = e6.getText().toString();
                String phone_number = e7.getText().toString();
                if (name.equalsIgnoreCase("")) {
                    e1.setError("*");
                    flg++;
                }
                if (place.equalsIgnoreCase("")) {
                    e2.setError("*");
                    flg++;
                }
                if (post.equalsIgnoreCase("")) {
                    e3.setError("*");
                    flg++;
                }
                if (pin.equalsIgnoreCase("")) {
                    e4.setError("*");
                    flg++;
                }
                if (aadhar_number.equalsIgnoreCase("")) {
                    e5.setError("*");
                    flg++;
                }
                if (email.equalsIgnoreCase("")) {
                    e6.setError("*");
                    flg++;
                }
                if (phone_number.equalsIgnoreCase("")) {
                    e7.setError("*");
                    flg++;
                }
                if (phone_number.length() != 10) {
                    e7.setError("*Invalid Phone Number*");
                    flg++;
                }

                if (aadhar_number.length() != 12) {
                    e5.setError("*Invalid Aadhar Number*");
                    flg++;
                }

                if (pin.length() != 6) {
                    e4.setError("*Invalid pin Number*");
                    flg++;
                }
                if (flg == 0) {
                    sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    sh.getString("ip", "");
                    sh.getString("url", "");
                    url = sh.getString("url", "") + "/and_edit_partner";
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                                    try {
                                        JSONObject jsonObj = new JSONObject(response);
                                        if (jsonObj.getString("status").equalsIgnoreCase("ok")) {
                                            Toast.makeText(edit_partner.this, "partner added", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(getApplicationContext(), MainActivity2.class);
                                            startActivity(i);
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

                            params.put("na", name);//passing to python
                            params.put("pl", place);
                            params.put("po", post);
                            params.put("pi", pin);
                            params.put("ad", aadhar_number);
                            params.put("em", email);
                            params.put("ph", phone_number);
                            params.put("id", sh.getString("pid", ""));//passing to python


                            return params;
                        }
                    };


                    int MY_SOCKET_TIMEOUT_MS = 100000;

                    postRequest.setRetryPolicy(new DefaultRetryPolicy(
                            MY_SOCKET_TIMEOUT_MS,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(postRequest);


                }
            }
        });
    }
}




  //  @Override
   // protected void onCreate(Bundle savedInstanceState) {
      //  super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_edit_partner);




