package com.example.drivervigilancesystem;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class login extends AppCompatActivity {

    EditText e,e1;

    Button b;
    String url;
    SharedPreferences sh;
TextView t1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        e=findViewById(R.id.username);
        e1=findViewById(R.id.password);
        b=findViewById(R.id.loginButton);
        t1=findViewById(R.id.signupText);
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),registration.class);
                startActivity(i);
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username =e.getText().toString();
                String password =e1.getText().toString();

                sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sh.getString("ip","");
                sh.getString("url","");
                url=sh.getString("url","")+"/and_login";

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    if (jsonObj.getString("status").equalsIgnoreCase("ok")) {
//                                Toast.makeText(Login.this, "welcome", Toast.LENGTH_SHORT).show();
                                        String typ = jsonObj.getString("type");
                                        String id = jsonObj.getString("lid");
                                        SharedPreferences.Editor ed = sh.edit();
                                        ed.putString("lid", id);
                                        ed.commit();
                                        if (typ.equalsIgnoreCase("driver")) {
                                            Toast.makeText(getApplicationContext(), "Welcome to driver", Toast.LENGTH_LONG).show();
//                                            Intent ij=new Intent(getApplicationContext(),ShakeService.class);
//                                            startService(ij);
                                            Intent ik=new Intent(getApplicationContext(),LocationService.class);
                                            startService(ik);
                                            Intent is=new Intent(getApplicationContext(),srvc.class);
                                            startService(is);
                                            Intent j=new Intent(getApplicationContext(), Shake.class);
                                            startService(j);

                                            Intent k=new Intent(getApplicationContext(), Call_service.class);
                                            startService(k);


                                            Intent i = new Intent(getApplicationContext(), MainActivity2.class);
                                            startActivity(i);


                                        }
                                        if (typ.equalsIgnoreCase("partner")) {
                                            Toast.makeText(getApplicationContext(), "Welcome to partner", Toast.LENGTH_LONG).show();
                                            Intent i = new Intent(getApplicationContext(), partner_home.class);
                                            startActivity(i);
                                        }
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

                        params.put("u", username);//passing to python
                        params.put("p", password);
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
        });
    }
}