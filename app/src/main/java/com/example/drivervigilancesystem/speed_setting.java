package com.example.drivervigilancesystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

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

public class speed_setting extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    Button spped_button_1,spped_button_2,spped_button_3,save;
    RadioButton silent,vibrate,general;
    ToggleButton toggle_touch,toggle_callblock;
    String speed="",mode="",touch="off",callblock="off";
    String lid="",url="",url1="";
    SharedPreferences sh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_setting);

        silent=(RadioButton)findViewById(R.id.radioButton8);
        vibrate=(RadioButton)findViewById(R.id.radioButton6);
        general=(RadioButton)findViewById(R.id.radioButton7);

        toggle_touch=(ToggleButton)findViewById(R.id.toggleButton2);
        toggle_callblock=(ToggleButton)findViewById(R.id.toggleButton3);

        spped_button_1=(Button)findViewById(R.id.button13);
        spped_button_2=(Button)findViewById(R.id.button14);
        spped_button_3=(Button)findViewById(R.id.button15);

        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        lid=sh.getString("lid","");
        url=sh.getString("url","")+"and_view_mode_setting";
        url1=sh.getString("url","")+"and_mode_settings";


        save=(Button)findViewById(R.id.button16);

        save.setOnClickListener(this);

        //speed limit button click

        spped_button_1.setOnClickListener(this);
        spped_button_2.setOnClickListener(this);
        spped_button_3.setOnClickListener(this);

        // toggle button click

        toggle_touch.setOnCheckedChangeListener(this);

        toggle_callblock.setOnCheckedChangeListener(this);




    }

    @Override
    public void onClick(View V) {

        //speed limit conditions

        if (V == spped_button_1) {
            speed = "0-40";
            toggle_callblock.setEnabled(false);
            toggle_callblock.setChecked(true);
            toggle_touch.setEnabled(false);
            toggle_touch.setChecked(true);
            DisplayMysettings(speed, lid);
        }
        if (V == spped_button_2) {
            speed = "40-60";

            toggle_callblock.setChecked(true);
            toggle_callblock.setEnabled(true);
            toggle_touch.setChecked(true);
            toggle_touch.setEnabled(true);
            DisplayMysettings(speed, lid);
        }
        if (V == spped_button_3) {
            speed = ">60";

            toggle_callblock.setChecked(true);
            toggle_callblock.setEnabled(true);
            toggle_touch.setChecked(true);
            toggle_touch.setEnabled(true);
            DisplayMysettings(speed, lid);
        }
        if (V == save) {
            if (silent.isChecked()) {
                mode = "silent";
            } else if (vibrate.isChecked()) {
                mode = "vibrate";
            } else if (general.isChecked()) {
                mode = "general";
            }

            //add and update code.............


            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest postRequest = new StringRequest(Request.Method.POST, url1,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                            try {
                                JSONObject json = new JSONObject(response);
                                String res = json.getString("status");
                                if (res.equalsIgnoreCase("Your mode settings successfully updated..")) {
// ==================================================================================================
//                                    Toast.makeText(speed_setting.this, res, Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(speed_setting.this, partner_home.class));

                                } else if (res.equalsIgnoreCase("Your mode settings successfully added..")) {
//                                    Toast.makeText(speed_setting.this, res, Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(speed_setting.this, partner_home.class));
                                } else {
                                    Toast.makeText(speed_setting.this, "No data", Toast.LENGTH_SHORT).show();
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

                    params.put("lid", sh.getString("lid", ""));//passing to python
                    params.put("speed", speed);
                    params.put("mode", mode);
//                params.put("brightness", brightness);
                    params.put("touch", touch);
                    params.put("automsg", "");
                    params.put("callblock", callblock);
//                params.put("message", "");

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


    private void DisplayMysettings(String speed,String lid){

        //view code

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                        try {
                            JSONObject json = new JSONObject(response);
                            String res=json.getString("status");
                            if(res.equalsIgnoreCase("1"))
                            {

                                try {
                                    String my_mode = json.getString("mode");
//                                    String my_bright = json.getString("brightness");
                                    String my_touch = json.getString("touch");
                                    String my_call = json.getString("callblock");
                                    String my_automsg = json.getString("automsg");
//                                    String my_message = json.getString("message");

                                    if (my_mode.equals("silent")) {
                                        silent.setChecked(true);
                                    } else if (my_mode.equals("vibrate")) {
                                        vibrate.setChecked(true);
                                    }
                                    if (my_mode.equals("general")) {
                                        general.setChecked(true);
                                    }
//                                    bright.setProgress(Integer.parseInt(my_bright));
                                    if (my_touch.equals("on")) {
                                        toggle_touch.setChecked(true);
                                    } else {
                                        toggle_touch.setChecked(false);
                                    }
                                    if (my_call.equals("on")) {
                                        toggle_callblock.setChecked(true);
                                    } else {
                                        toggle_callblock.setChecked(false);
                                    }

                                }catch (Exception e)
                                {
                                    Toast.makeText(speed_setting.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }

                            }
                            else{
                                Toast.makeText(speed_setting.this, "No data!!!", Toast.LENGTH_SHORT).show();
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

                params.put("lid", sh.getString("lid",""));//passing to python
                params.put("speed", speed);

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

//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        brightness=String.valueOf(progress);
//    }

//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//
//    }
//
///    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//
//    }

    //toggle setting

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView==toggle_touch)
        {
            if (isChecked)
            {
                touch="on";
            }
            else
            {
                touch="off";
            }
        }
        if (buttonView==toggle_callblock)
        {
            if (isChecked)
            {
                callblock="on";
            }
            else
            {
                callblock="off";
            }
        }

    }



    }

