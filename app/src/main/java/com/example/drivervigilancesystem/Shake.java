package com.example.drivervigilancesystem;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;




public class Shake extends Service implements SensorListener {



    String url="";
    SharedPreferences sh;
    private SensorManager sensorMgr;
    private static final int SHAKE_THRESHOLD = 2000;
    private long lastUpdate = -1;
    private float x, y, z;
    private float last_x, last_y, last_z;


    TextToSpeech th;

    String road_cond="";

String[]contact;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        boolean accelSupported = sensorMgr.registerListener(this,SensorManager.SENSOR_ACCELEROMETER,SensorManager.SENSOR_DELAY_GAME);

        if (!accelSupported) {
            // on accelerometer on this device
            sensorMgr.unregisterListener((SensorListener) this,
                    SensorManager.SENSOR_ACCELEROMETER);
        }
//        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        url = sh.getString("url", "") + "add_distruption/";

        th=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    th.setLanguage(Locale.UK);
                }
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    public void onAccuracyChanged(int arg0, int arg1) {
        // TODO Auto-generated method stub
    }

    public void onSensorChanged(int sensor, float[] values) {
        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                x = values[SensorManager.DATA_X];
                y = values[SensorManager.DATA_Y];
                z = values[SensorManager.DATA_Z];

                if(Round(x,4)>10.0000){
                 //   Log.d("sensor", "X Right axis: " + x);
                    //     Toast.makeText(this, "Right shake detected", Toast.LENGTH_SHORT).show();
                }
                else if(Round(y,4)>10.0000){
                 //   Log.d("sensor", "X Right axis: " + x);
                    //    Toast.makeText(this, "Top shake detected", Toast.LENGTH_SHORT).show();
                }
                else if(Round(y,4)>-10.0000){
                  //  Log.d("sensor", "X Right axis: " + x);
                    //     Toast.makeText(this, "Bottom shake detected", Toast.LENGTH_SHORT).show();
                }
                else if(Round(x,4)<-10.0000){
              //      Log.d("sensor", "X Left axis: " + x);
                    //    Toast.makeText(this, "Left shake detected", Toast.LENGTH_SHORT).show();
                }

                float speed = Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > 2500 && speed <=6000)
                {

                    Toast.makeText(this, speed+"  bad road", Toast.LENGTH_SHORT).show();
                    road_cond ="bad road";
                    road_condition(road_cond);
                } else if(speed >6000 ){

                    Toast.makeText(this, speed+"  damaged road", Toast.LENGTH_SHORT).show();
                    road_cond = "damaged road";
                    road_condition(road_cond);
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    public static float Round(float Rval, int Rpl) {
        float p = (float)Math.pow(10,Rpl);
        Rval = Rval * p;
        float tmp = Math.round(Rval);
        return (float)tmp/p;
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId) {

        Toast.makeText(this,"start Service.",Toast.LENGTH_SHORT).show();



        return START_REDELIVER_INTENT;

    }

    public void road_condition(String cond){
        SharedPreferences sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String hu = sh.getString("ip", "");
        String url=sh.getString("url","")+"/and_road_condition";



        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                        // response
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            if (jsonObj.getString("status").equalsIgnoreCase("ok")) {

//
                                String toSpeak = cond + "detected";
//                                        Toast.makeText(getApplicationContext(), "hiiiiiiiiiiiiii",Toast.LENGTH_SHORT).show();
                                th.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                            }

                            // }
                            else {
                                Toast.makeText(getApplicationContext(), "Not found", Toast.LENGTH_LONG).show();
                            }

                        }    catch (Exception e) {
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
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Map<String, String> params = new HashMap<String, String>();

                params.put("lati",LocationService.lati);
                params.put("longi",LocationService.longi);
                params.put("cond", cond);
                params.put("did",sh.getString("lid",""));

                return params;
            }
        };

        int MY_SOCKET_TIMEOUT_MS=100000;

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(postRequest);
    }
}
