package com.example.drivervigilancesystem;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Trace;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class srvc extends Service  implements TextToSpeech.OnInitListener{


    TextView timerTextView;
    long startTime = 0;

    private static final String TAG = "AusteerLogging";

    TextToSpeech th;
    private String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(System.currentTimeMillis());
    private String filename = "Austeer-" + timeStamp + ".csv";
    private String filepath = "Austeer";
    File envpath;
    File outputFile;
    String outputString = "";
    FileWriter fw;

    TextView speedTextView;
    TextView locationTextView;
    TextView steeringTextView;
    TextView storageTextView;

    String locString = "";
    String altitudeString = "";
    String speedString = "";
    String steerStringX="";
    String steerStringY="";
    String steerStringZ="";

    List<Double> speedlist = new ArrayList();
    Double lastLat=0.0;
    Double lastLng=0.0;
    Double lastAlt=0.0;
    Long lastTime= Long.valueOf(0);

    LocationManager locationManager;
    LocationListener li;

    SensorManager sMgr;
    Sensor gyro;
    SensorEventListener sev;

    @Override
    public void onInit(int status) {
        if(status != TextToSpeech.ERROR) {
            th.setLanguage(Locale.UK);


        }
        else
        {
            Toast.makeText(getApplicationContext(), "Failed to start" , Toast.LENGTH_SHORT).show();
        }

    }

    public void setBrightness(int brightness){

        //constrain the value of brightness
        if(brightness < 0)
            brightness = 0;
        else if(brightness > 255)
            brightness = 255;


        try{
            ContentResolver cResolver = this.getApplicationContext().getContentResolver();
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);

        }catch (Exception e){

            Log.d("bb=======",e.toString());
//            Toast.makeText(this, "bbb===="+e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void changebrighness(String lm)
    {

        setBrightness(Integer.parseInt(lm));


    }
    public  void changetouch(String tch)
    {
        try{
            if(tch.equalsIgnoreCase("on")) ///// change here to work in college >=0
            {
//        Toast.makeText(this, "Touch disabled", Toast.LENGTH_SHORT).show();
//                Intent ints=new Intent(getApplicationContext(), GlobalTouchService.class);
//                stopService(ints);
            }
            else
            {
        Toast.makeText(this, "touch enabled", Toast.LENGTH_SHORT).show();
//                Intent ints=new Intent(getApplicationContext(),GlobalTouchService.class);
//                startService(ints);
            }
        }catch (Exception e){
            Toast.makeText(this, "t==="+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }



    public void changemode(String mode)
    {

        String ms= mode;
        Log.d("IN--- MODE", ms);


        try{

            if(ms.equalsIgnoreCase("silent"))
            {
                AudioManager am=(AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
                am.setRingerMode(2);

                Toast.makeText(getApplicationContext(),"Mode changed to SILENT",Toast.LENGTH_SHORT).show();
            }
            else if(ms.equalsIgnoreCase("general"))
            {
                AudioManager am=(AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                Toast.makeText(getApplicationContext(),"Mode changed to General",Toast.LENGTH_SHORT).show();
                Log.d("IN--- MODE", "GEN");
            }
            else if(ms.equalsIgnoreCase("vibrate")) {
                AudioManager am = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                Toast.makeText(getApplicationContext(), "Mode changed to Vibrate", Toast.LENGTH_SHORT).show();
                Log.d("IN--- MODE", "VIB");
            }

        }catch (Exception e){
            Log.d("=====mm",e.toString());
        }
    }

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();


    int flag=0;

    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

//            Toast.makeText(srvc.this, "inside", Toast.LENGTH_SHORT).show();
            SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String url=sh.getString("url","")+"insertintodriverlogs";
//            String url=sh.getString("url","")+"Get_message";


            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject js = new JSONObject(response);


                                String res= js.getString("mstatus");

                                if(res.equalsIgnoreCase("1"))
                                {
                                    String msg_id=js.getString("msgid");
                                    String message=js.getString("message");
                                    SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor Ed=sh.edit();
                                    Ed.putString("last_id",msg_id);
                                    Ed.commit();

                                    th.speak(message, TextToSpeech.QUEUE_FLUSH, null);

                                }



                                String status= js.getString("status");
                                if(status.equalsIgnoreCase("ok"))
                                {

                                    String spd= js.getString("spd");
                                    String mode= js.getString("mode");
//                                    String bri= js.getString("bri");
                                    String tch= js.getString("tch");
                                    String blk= js.getString("blk");



                                    //  String typ= js.getString("typ");
//                                    String msg= js.getString("msg");
                                    String auto= js.getString("auto");

//                                    Toast.makeText(srvc.this, bri, Toast.LENGTH_SHORT).show();
//                                    changebrighness(bri);
                                    if(spd.equalsIgnoreCase("0-40")){
                                        if(tch.equalsIgnoreCase("on")){
                                            Toast.makeText(getApplicationContext(), "Touch block can only be enabled for speed >40km/h", Toast.LENGTH_SHORT).show();
                                        }
                                        if(blk.equalsIgnoreCase("on")){
                                            Toast.makeText(getApplicationContext(), "Call block can only be enabled for speed >40km/h", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        changetouch(tch);
                                        SharedPreferences shp=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        SharedPreferences.Editor ed=shp.edit();
                                        ed.putString("block", blk);
                                        ed.putString("auto", auto);
//                                    ed.putString("msg", msg);
                                        ed.commit();

                                    }

                                    changemode(mode);




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

                    params.put("location", place);//passing to python
//                    params.put("location", LocationService.place);//passing to python
                    params.put("driverid", sh.getString("lid",""));
                    params.put("latitudinal", latlik);
                    params.put("longitudinal", lonlik);
                    params.put("speed", speedString);
                    params.put("angle", steerStringY);
                    params.put("lastid", sh.getString("last_id","0"));



                    return params;
                }
            };


            int MY_SOCKET_TIMEOUT_MS = 100000;

            postRequest.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(postRequest);





            timerHandler.postDelayed(this, 15000);
        }
    };




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public static  String latlik="",lonlik="",place="";

    @Override
    public void onCreate() {
        super.onCreate();
        //////

        th=new TextToSpeech(this,this);
        final DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);

        final DecimalFormat df2 = new DecimalFormat("#.#");
        df2.setRoundingMode(RoundingMode.CEILING);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        li = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.

//                Toast.makeText(srvc.this, "Location changed", Toast.LENGTH_SHORT).show();
                Double newLat = location.getLatitude();
                Double newLng = location.getLongitude();
                Double newAlt = location.getAltitude();
                Long newTime = location.getTime();


                Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
                try {

                    List<Address> addresses = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    // Toast.makeText(getApplicationContext(), addresses+"...........llooc", Toast.LENGTH_SHORT).show();
                    if (addresses.size() > 0) {
                        for (int index = 0; index < addresses.get(0).getMaxAddressLineIndex(); index++)
                            place += addresses.get(0).getAddressLine(index) + " ";
                        place = addresses.get(0).getFeatureName().toString();
//                            place += addresses.get(0).getAddressLine(index) + " ";
//                        place = addresses.get(0).getFeatureName().toString();
                        // subloc = addresses.get(0).getSubLocality().toString();
//                                                 place = addresses.get(0).getLocality().toString();

                    }

                } catch (IOException e) {
                    //  e.printStackTrace();
                    // Toast.makeText(getApplicationContext(), e.getMessage().toString()+"", Toast.LENGTH_SHORT).show();
                }

                altitudeString = Double.toString(newAlt);

                Float accuracy = location.getAccuracy();

                locString = df.format(newLat) + ", " + df.format(newLng);


                latlik= newLat+"";
                lonlik= newLng+"";


                Log.v(TAG, "LOCATION , lat=" + df.format(newLat) + ", lon=" + df.format(newLng) + "(Accuracy: " + accuracy + ")");

                // on first run set location and start timer
                if (lastLat == 0.0) {
                    startTime = System.currentTimeMillis();
                    if(flag==0) {
                        timerHandler.postDelayed(timerRunnable, 0);
                        flag++;

                    }
                    speedString = "0";
                    lastLat = newLat;
                    lastLng = newLng;
                    lastAlt = newAlt;
                    lastTime = newTime;
                } else {
                    Double dist = distance(lastLat, newLat, lastLng, newLng, newAlt, newAlt);

                    Log.v(TAG, "DISTANCE = " + dist);

                    float timediff = (newTime - lastTime) / 1000;

                    if (timediff == 0) {
                        // do nothing because distance is less than accuracy
                        // or measurement too quick
                        Log.v(TAG, "SPEED UNCHANGED");
                    } else {
                        Double speed = Math.abs(dist) / timediff;
                        // Average speed from last five position results
                        speedlist.add(speed);
                        if (speedlist.size() > 5) {
                            speedlist.remove(0);
                            Double averagespeed = averageSpeed(speedlist);
                            if (averagespeed < 0.5) { averagespeed = 0.0; }
                            speedString = df2.format(Math.abs(averagespeed));
                            speedString = Integer.toString((int)Math.abs(averagespeed));
                            Log.v(TAG, "SPEEDS = "+speedlist.toString());
                            Log.v(TAG, "ALTITUDE = "+altitudeString);
                        }
                        // only update speeds if dist/time is changed
                        lastLat = newLat;
                        lastLng = newLng;
                        lastAlt = newAlt;
                        lastTime = newTime;
                    }
                }


            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(this)
                    .setTitle("Permissions Needed")
                    .setMessage("You need to give Austeer permission to use your location.")
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, li);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, li);

        sMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyro = sMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // With Accelerometer, flat on a table is X-0, Y-0, Z-10
        // Held flat upward in portrait mode, X-0, y-10, Z-0

        // Held flat upward in landscape: X-10, Y-0, Z-0
        // Steering mode fixed to steering wheel landscape: Just use Y-value

        sev = new SensorEventListener() {
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

            public void onSensorChanged(SensorEvent event) {
                float axisX = event.values[0];
                float axisY = event.values[1];
                float axisZ = event.values[2];
                // Trying to remove negative zero but doing it wrong.
//                if (1 / axisY > 0) { } else { axisY = Math.abs(axisY); }
                steerStringX = Float.toString(Math.round(axisX * 10) / 10);
                steerStringY = String.format("%.2f", axisY / 10);
                steerStringZ = Float.toString(Math.round(axisZ * 10) / 10);
            }
        };

        sMgr.registerListener(sev, gyro, SensorManager.SENSOR_DELAY_FASTEST);

    }

    public Double distance(double lat1, double lat2, double lon1,
                           double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public Double averageSpeed(List speeds) {
        Double total = 0.0;
        for (int i = 0; i < speeds.size(); i++) {
            total = total + (double)speeds.get(i);
        }
        return (total / speeds.size());
    }
}



//===================
//import android.Manifest;
//import android.app.AlertDialog;
//import android.app.Service;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.media.AudioManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.preference.PreferenceManager;
//import android.provider.Settings;
//import android.speech.tts.TextToSpeech;
//import android.util.Log;
//import android.view.WindowManager;
//import android.widget.TextView;
//import android.widget.Toast;
//
//
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.math.RoundingMode;
//import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//public class srvc extends Service  implements TextToSpeech.OnInitListener{
//
//
//    TextView timerTextView;
//    long startTime = 0;
//
//    private static final String TAG = "AusteerLogging";
//
//    TextToSpeech th;
//    private String timeStamp = n-ew SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(System.currentTimeMillis());
//    private String filename = "Austeer-" + timeStamp + ".csv";
//    private String filepath = "Austeer";
//    File envpath;
//    File outputFile;
//    String outputString = "";
//    FileWriter fw;
//
//    TextView speedTextView;
//    TextView locationTextView;
//    TextView steeringTextView;
//    TextView storageTextView;
//
//    String locString = "";
//    String altitudeString = "";
//    String speedString = "";
//    String steerStringX="";
//    String steerStringY="";
//    String steerStringZ="";
//
//    List<Double> speedlist = new ArrayList();
//    Double lastLat=0.0;
//    Double lastLng=0.0;
//    Double lastAlt=0.0;
//    Long lastTime= Long.valueOf(0);
//
//    LocationManager locationManager;
//    LocationListener li;
//
//    SensorManager sMgr;
//    Sensor gyro;
//    SensorEventListener sev;
//
//    @Override
//    public void onInit(int status) {
//        if(status != TextToSpeech.ERROR) {
//            th.setLanguage(Locale.UK);
//
//
//        }
//        else
//        {
//            Toast.makeText(getApplicationContext(), "Failed to start" , Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    public void setBrightness(int brightness){
//
//        //constrain the value of brightness
//        if(brightness < 0)
//            brightness = 0;
//        else if(brightness > 255)
//            brightness = 255;
//
//
//        try{
//            ContentResolver cResolver = this.getApplicationContext().getContentResolver();
//            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
//
//        }catch (Exception e){
//
//            Log.d("bb=======",e.toString());
////            Toast.makeText(this, "bbb===="+e.toString(), Toast.LENGTH_LONG).show();
//        }
//    }
//
//    public void changebrighness(String lm)
//    {
//
//        setBrightness(Integer.parseInt(lm));
//
//
//    }
//    public  void changetouch(String tch)
//    {
//try{
//    if(tch.equalsIgnoreCase("off")) ///// change here to work in college >=0
//    {
//        Toast.makeText(this, "touch offfff", Toast.LENGTH_SHORT).show();
//        Intent ints=new Intent(srvc.this, GlobalTouchService.class);
//        stopService(ints);
//
////        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//    }
//    else
//    {
//        Toast.makeText(this, "touch onnnnnnn", Toast.LENGTH_SHORT).show();
//        Intent ints=new Intent(srvc.this, GlobalTouchService.class);
//        startService(ints);
////        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//    }
//}catch (Exception e){
//    Toast.makeText(this, "t==="+e.toString(), Toast.LENGTH_SHORT).show();
//}
//    }
//
//
//
//    public void changemode(String mode)
//    {
//
//                String ms= mode;
////        Toast.makeText(this, "ms"+ms, Toast.LENGTH_SHORT).show();
//                Log.d("IN--- MODE", ms);
//
//
//try{
//
//    if(ms.equalsIgnoreCase("silent"))
//    {
//        AudioManager am=(AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
//        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//
//        Log.d("IN--- MODE", "SILENT1");
////                    Toast.makeText(getApplicationContext(),"Mode changed to SILENT",Toast.LENGTH_SHORT).show();
//    }
//    else if(ms.equalsIgnoreCase("general"))
//    {
//        AudioManager am=(AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
//        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
////                    Toast.makeText(getApplicationContext(),"Mode changed to General",Toast.LENGTH_SHORT).show();
//        Log.d("IN--- MODE", "GEN");
//    }
//    else if(ms.equalsIgnoreCase("vibrate")) {
//        AudioManager am = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
//        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
////                    Toast.makeText(getApplicationContext(), "Mode changed to Vibrate", Toast.LENGTH_SHORT).show();
//        Log.d("IN--- MODE", "VIB");
//    }
//
//}catch (Exception e){
//    Toast.makeText(this, "noooooooooo", Toast.LENGTH_SHORT).show();
//    Log.d("=====mm",e.toString());
//}
//    }
//
//    //runs without a timer by reposting this handler at the end of the runnable
//    Handler timerHandler = new Handler();
//
//
//    int flag=0;
//
//    Runnable timerRunnable = new Runnable() {
//
//        @Override
//        public void run() {
//            long millis = System.currentTimeMillis() - startTime;
//            int seconds = (int) (millis / 1000);
//            int minutes = seconds / 60;
//            seconds = seconds % 60;
//
////            Toast.makeText(srvc.this, "inside", Toast.LENGTH_SHORT).show();
//            SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//
////            sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//            String url=sh.getString("url","")+"insertintodriverlogs";
////            String url=sh.getString("url","")+"Get_message";
//
//
//            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//
//                            try {
//                                JSONObject js = new JSONObject(response);
//
//
//                                String res= js.getString("mstatus");
////                                Toast.makeText(srvc.this, ""+res, Toast.LENGTH_SHORT).show();
//
//
//                                if(res.equalsIgnoreCase("1"))
//                                {
//                                    String msg_id=js.getString("msgid");
//                                    String message=js.getString("message");
//                                    Toast.makeText(srvc.this, ""+message, Toast.LENGTH_SHORT).show();
//                                    SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                                    SharedPreferences.Editor Ed=sh.edit();
//                                    Ed.putString("last_id",msg_id);
//                                    Ed.commit();
//
//                                    th.speak(message, TextToSpeech.QUEUE_FLUSH, null);
//
//                                }
//
//
//
//                                String status= js.getString("status");
//                                if(status.equalsIgnoreCase("ok"))
//                                {
//
//                                    String mode= js.getString("mode");
////                                    Toast.makeText(srvc.this, "mmmmm"+mode, Toast.LENGTH_SHORT).show();
////                                    String bri= js.getString("bri");
//                                    String tch= js.getString("tch");
//                                    String blk= js.getString("blk");
//                                    //  String typ= js.getString("typ");
////                                    String msg= js.getString("msg");
////                                    String auto= js.getString("auto");
//
////                                    Toast.makeText(srvc.this, bri, Toast.LENGTH_SHORT).show();
////                                    changebrighness(bri);
//                                    changemode(mode);
//                                    changetouch(tch);
//
//
//                                    SharedPreferences shp=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                                    SharedPreferences.Editor ed=shp.edit();
//                                    ed.putString("block", blk);
////                                    ed.putString("auto", auto);
////                                    ed.putString("msg", msg);
//                                    ed.commit();
//
//                                }
//
//                            } catch (Exception e) {
//                                Toast.makeText(getApplicationContext(), "Error" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            // error
//                            Toast.makeText(getApplicationContext(), "eeeee" + error.toString(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//            ) {
//
//                //                value Passing android to python
//                @Override
//                protected Map<String, String> getParams() {
//                    SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                    Map<String, String> params = new HashMap<String, String>();
//
//                    params.put("location", place);//passing to python
//                    params.put("driverid", sh.getString("lid",""));
//                    params.put("latitudinal", latlik);
//                    params.put("longitudinal", lonlik);
//                    params.put("speed", speedString);
//                    params.put("angle", steerStringY);
//                    params.put("lastid", sh.getString("last_id","0"));
//
//
//
//                    return params;
//                }
//            };
//
//
//            int MY_SOCKET_TIMEOUT_MS = 100000;
//
//            postRequest.setRetryPolicy(new DefaultRetryPolicy(
//                    MY_SOCKET_TIMEOUT_MS,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            requestQueue.add(postRequest);
//
//
//
//
//
//            timerHandler.postDelayed(this, 5000);
//        }
//    };
//
//
//
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//    public static  String latlik="",lonlik="",place="";
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        //////
//
//        th=new TextToSpeech(this,this);
//        final DecimalFormat df = new DecimalFormat("#.####");
//        df.setRoundingMode(RoundingMode.CEILING);
//
//        final DecimalFormat df2 = new DecimalFormat("#.#");
//        df2.setRoundingMode(RoundingMode.CEILING);
//
//        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//
//        li = new LocationListener() {
//            public void onLocationChanged(Location location) {
//                // Called when a new location is found by the network location provider.
//
////                Toast.makeText(srvc.this, "Location changed", Toast.LENGTH_SHORT).show();
//                Double newLat = location.getLatitude();
//                Double newLng = location.getLongitude();
//                Double newAlt = location.getAltitude();
//                Long newTime = location.getTime();
//
//
//                Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
//                try {
//
//                    List<Address> addresses = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                    // Toast.makeText(getApplicationContext(), addresses+"...........llooc", Toast.LENGTH_SHORT).show();
//                    if (addresses.size() > 0) {
//                        for (int index = 0; index < addresses.get(0).getMaxAddressLineIndex(); index++)
//                            place += addresses.get(0).getAddressLine(index) + " ";
//                        place = addresses.get(0).getFeatureName().toString();
////                          Toast.makeText(getApplicationContext(), place, Toast.LENGTH_SHORT).show();
//                         place = addresses.get(0).getLocality().toString();
//                    }
//
//                } catch (IOException e) {
//                    //  e.printStackTrace();
//                    // Toast.makeText(getApplicationContext(), e.getMessage().toString()+"", Toast.LENGTH_SHORT).show();
//                }
//
//                altitudeString = Double.toString(newAlt);
//
//                Float accuracy = location.getAccuracy();
//
//                locString = df.format(newLat) + ", " + df.format(newLng);
//
//
//                latlik= newLat+"";
//                lonlik= newLng+"";
//
//
//                Log.v(TAG, "LOCATION , lat=" + df.format(newLat) + ", lon=" + df.format(newLng) + "(Accuracy: " + accuracy + ")");
//
//                // on first run set location and start timer
//                if (lastLat == 0.0) {
//                    startTime = System.currentTimeMillis();
//                    if(flag==0) {
//                        timerHandler.postDelayed(timerRunnable, 0);
//                    flag++;
//
//                    }
//                    speedString = "0";
//                    lastLat = newLat;
//                    lastLng = newLng;
//                    lastAlt = newAlt;
//                    lastTime = newTime;
//                } else {
//                    Double dist = distance(lastLat, newLat, lastLng, newLng, newAlt, newAlt);
//
//                    Log.v(TAG, "DISTANCE = " + dist);
//
//                    float timediff = (newTime - lastTime) / 1000;
//
//                    if (timediff == 0) {
//                        // do nothing because distance is less than accuracy
//                        // or measurement too quick
//                        Log.v(TAG, "SPEED UNCHANGED");
//                    } else {
//                        Double speed = Math.abs(dist) / timediff;
//                        // Average speed from last five position results
//                        speedlist.add(speed);
//                        if (speedlist.size() > 5) {
//                            speedlist.remove(0);
//                            Double averagespeed = averageSpeed(speedlist);
//                            if (averagespeed < 0.5) { averagespeed = 0.0; }
//                            speedString = df2.format(Math.abs(averagespeed));
//                            speedString = Integer.toString((int)Math.abs(averagespeed));
//                            Log.v(TAG, "SPEEDS = "+speedlist.toString());
//                            Log.v(TAG, "ALTITUDE = "+altitudeString);
//                        }
//                        // only update speeds if dist/time is changed
//                        lastLat = newLat;
//                        lastLng = newLng;
//                        lastAlt = newAlt;
//                        lastTime = newTime;
//                    }
//                }
//
//
//            }
//
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//            }
//
//            public void onProviderEnabled(String provider) {
//            }
//
//            public void onProviderDisabled(String provider) {
//            }
//        };
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            new AlertDialog.Builder(this)
//                    .setTitle("Permissions Needed")
//                    .setMessage("You need to give Austeer permission to use your location.")
//                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // do nothing
//                        }
//                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .show();
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, li);
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, li);
//
//        sMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
//        gyro = sMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//
//        // With Accelerometer, flat on a table is X-0, Y-0, Z-10
//        // Held flat upward in portrait mode, X-0, y-10, Z-0
//        // Held flat upward in landscape: X-10, Y-0, Z-0
//        // Steering mode fixed to steering wheel landscape: Just use Y-value
//
//        sev = new SensorEventListener() {
//            public void onAccuracyChanged(Sensor sensor, int accuracy) {
//            }
//
//            public void onSensorChanged(SensorEvent event) {
//                float axisX = event.values[0];
//                float axisY = event.values[1];
//                float axisZ = event.values[2];
//                // Trying to remove negative zero but doing it wrong.
////                if (1 / axisY > 0) { } else { axisY = Math.abs(axisY); }
//                steerStringX = Float.toString(Math.round(axisX * 10) / 10);
//                steerStringY = String.format("%.2f", axisY / 10);
//                steerStringZ = Float.toString(Math.round(axisZ * 10) / 10);
//            }
//        };
//
//        sMgr.registerListener(sev, gyro, SensorManager.SENSOR_DELAY_FASTEST);
//
//    }
//
//    public Double distance(double lat1, double lat2, double lon1,
//                           double lon2, double el1, double el2) {
//
//        final int R = 6371; // Radius of the earth
//
//        Double latDistance = Math.toRadians(lat2 - lat1);
//        Double lonDistance = Math.toRadians(lon2 - lon1);
//        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
//                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
//                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
//        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        double distance = R * c * 1000; // convert to meters
//
//        double height = el1 - el2;
//
//        distance = Math.pow(distance, 2) + Math.pow(height, 2);
//
//        return Math.sqrt(distance);
//    }
//
//    public Double averageSpeed(List speeds) {
//        Double total = 0.0;
//        for (int i = 0; i < speeds.size(); i++) {
//            total = total + (double)speeds.get(i);
//        }
//        return (total / speeds.size());
//    }
//}
