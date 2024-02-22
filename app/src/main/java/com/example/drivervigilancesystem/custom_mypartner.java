package com.example.drivervigilancesystem;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class custom_mypartner extends AppCompatActivity {
    TextView name,address,aadhar_number,email,phone_number;
    SharedPreferences sh;
    String url,url1;
    FloatingActionButton fab;
    Button e,d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_mypartner);
        TextView name =findViewById(R.id.textView21);
        TextView address =findViewById(R.id.textView23);
        TextView aadhar_number =findViewById(R.id.textView47);
        TextView email =findViewById(R.id.textView48);
        TextView phone_number =findViewById(R.id.textView52);
         fab =findViewById(R.id.floatingActionButton2);
         e=findViewById(R.id.button);
         d=findViewById(R.id.button10);

         fab.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent i=new Intent(getApplicationContext(),partner_setting.class);
                 startActivity(i);
             }
         });
//
        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sh.getString("ip","");
        sh.getString("url","");
        url=sh.getString("url","")+"and_view_mypartner";
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
                                String did=jj.getString("partner_id");
                                SharedPreferences.Editor ed = sh.edit();
                                ed.putString("pid", did);
                                ed.commit();
                                name.setText(jj.getString("partner_name"));
                                String pl=jj.getString("place");
                                String po=jj.getString("post");
                                String pi=jj.getString("pin");
                                address.setText(pl+"\n"+po+"\n"+pi+"\n");
                                aadhar_number.setText(jj.getString("aadhar_no"));
                                email.setText(jj.getString("email"));
                                phone_number.setText(jj.getString("phone_no"));



//                                String image = jj.getString("photo");
//                                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                                String ip = sh.getString("ip", "");
//                                String url = "http://" + ip + ":5000" + image;
//                                Picasso.with(getApplicationContext()).load(url).transform(new CircleTransform()).into(im);//circle

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

        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),edit_partner.class);
                startActivity(i);
            }
        });
        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sh.getString("ip","");
                sh.getString("url","");
                url1=sh.getString("url","")+"/delete_partner";

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest postRequest = new StringRequest(Request.Method.POST, url1,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    if (jsonObj.getString("status").equalsIgnoreCase("ok")) {
                                        Toast.makeText(custom_mypartner.this, "successfully deleted", Toast.LENGTH_SHORT).show();
                                        Intent i =new Intent(getApplicationContext(),custom_mypartner.class);
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

//                        params.put("comp", complaint);//passing to python
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



            }
        });
//



    }
}
//}

//    private final Context context;
//    TextView name,address,aadhar_number,email,phone_number,pid;
//
//    public custom_mypartner(Context applicationContext, String[] name, String[] address, String[] aadhar_number, String[] email, String[] phone_number, String[] p_id){
//
//
//        this.context = applicationContext;
//        this.name =name ;
//        this.address = address;
//        this.aadhar_number = aadhar_number;
//        this.email =email ;
//        this.phone_number =phone_number ;
//        this.pid=p_id;
//
//    }
//
//    @Override
//    public int getCount() {
//        return email.length;
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return null;
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return 0;
//    }
//
//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        View gridView;
//        if (view == null) {
//            gridView = new View(context);
//            //gridView=inflator.inflate(R.layout.customview, null);
//            gridView = inflator.inflate(R.layout.activity_custom_mypartner, null);//same class name
//
//        } else {
//            gridView = (View) view;
//
//        }
//
//            TextView tv1 = (TextView) gridView.findViewById(R.id.textView21);
//            TextView tv2 = (TextView) gridView.findViewById(R.id.textView23);
//            TextView tv3 = (TextView) gridView.findViewById(R.id.textView47);
//            TextView tv4 = (TextView) gridView.findViewById(R.id.textView52);
//            TextView tv5 = (TextView) gridView.findViewById(R.id.textView48);
//            Button b=(Button) gridView.findViewById(R.id.button);
//            Button b1=(Button) gridView.findViewById(R.id.button10);
//            b.setTag(i);
//
//b.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View view) {
//        int ik= (int) view.getTag();
//        SharedPreferences sh;
//        sh= PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor ed=sh.edit();
//        ed.putString("pid",pid[ik]);
//        ed.commit();
//        Intent i=new Intent(context,edit_partner.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);
//
//    }
//});
//            tv1.setTextColor(Color.BLACK);//color setting
//            tv2.setTextColor(Color.BLACK);
//            tv3.setTextColor(Color.BLACK);
//            tv4.setTextColor(Color.BLACK);
//            tv5.setTextColor(Color.BLACK);
//
//
//
//            tv1.setText(name[i]);
//            tv2.setText(address[i]);
//            tv3.setText(aadhar_number[i]);
//            tv4.setText(email[i]);
//            tv5.setText(phone_number[i]);
//
//
//            SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);
//            String ip = sh.getString("ip", "");
//
////
//
//            return gridView;
//        }
//    }
