package com.example.drivervigilancesystem;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import org.ksoap2.SoapEnvelope;
//import org.ksoap2.serialization.SoapObject;
//import org.ksoap2.serialization.SoapSerializationEnvelope;
//import org.ksoap2.transport.HttpTransportSE;

public class ShakeService extends Service {

    private boolean mIsServiceStarted = false;
    private Context mContext = null;
    private SensorManager mSensorManager = null;
    private Sensor mSensor;
    private File mLogFile = null;
    private FileOutputStream mFileStream = null;
    private Float[] mValues = null;
    private long mTimeStamp = 0;
    private ExecutorService mExecutor = null;

    String cond="";

    TextToSpeech th;


    private static final int RUN_THRESHOLD = 4000;
    private static final int WAL_THRESHOLD = 2800;

    /**
     * Default empty constructor needed by Android OS
     */
    public ShakeService() {
        super();
    }

    /**
     * Constructor which takes context as argument
     *
     * @param context
     */
    public ShakeService(Context context) {
        super();

        if (context != null)
            mContext = context;
        else
            mContext = getBaseContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(getBaseContext(), "Service onCreate", Toast.LENGTH_SHORT).show();
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
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (isServiceStarted() == false) {

            mContext = getBaseContext();
            mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mValues = new Float[]{0f, 0f, 0f};
            mTimeStamp = 0;
            mExecutor = Executors.newSingleThreadExecutor();

//            setupFolderAndFile();
            startLogging();
        }

        //set started to true
        mIsServiceStarted = true;


        Toast.makeText(mContext, "Service onStartCommand", Toast.LENGTH_SHORT).show();
        return Service.START_STICKY;
    }

    private void setupFolderAndFile() {
        mLogFile = new File(Environment.getExternalStorageDirectory().toString()
                + "/test.txt");

        try {
            mFileStream = new FileOutputStream(mLogFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private long lastUpdate = -1;
    private float x, y, z;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 2


            ;

    private void startLogging() {

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mSensorManager.registerListener(
                        new SensorEventListener() {
                            @Override
                            public void onSensorChanged(SensorEvent sensorEvent) {
//                                mTimeStamp = System.currentTimeMillis();
                                long curTime = System.currentTimeMillis();
                                // only allow one update every 100ms.
                                if ((curTime - lastUpdate) > 100) {
                                    long diffTime = (curTime - lastUpdate);
                                    lastUpdate = curTime;
                                    String formatted = String.valueOf(mTimeStamp)
                                            + "\t" + String.valueOf(mValues[0])
                                            + "\t" + String.valueOf(mValues[1])
                                            + "\t" + String.valueOf(mValues[2])
                                            + "\r\n";

//                                mValues[0] = sensorEvent.values[0];
//                                mValues[1] = sensorEvent.values[1];
//                                mValues[2] = sensorEvent.values[2];
                                    x = mValues[0] = sensorEvent.values[0];
                                    y = mValues[1] = sensorEvent.values[1];
                                    z = mValues[2] = sensorEvent.values[2];

                                    float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                                    if (speed > 3000 && speed < 5000) {
                                        cond = "bad road";
                                    } else if (speed >= 5000) {
                                        cond = "damaged road";
                                    }
//
//                                        TextToSpeech th = null;
//                                        th.speak("bad road condition", TextToSpeech.QUEUE_FLUSH, null);
                                    String toSpeak = cond + "detected";
//                                        Toast.makeText(getApplicationContext(), "hiiiiiiiiiiiiii",Toast.LENGTH_SHORT).show();
                                    th.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);


                                    SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    String apiURL = sh.getString("url", "") + "/and_road_condition";
                                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                    StringRequest postRequest = new StringRequest(Request.Method.POST, apiURL,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                                                    // response
                                                    try {
                                                        JSONObject jsonObj = new JSONObject(response);
                                                        if (jsonObj.getString("status").equalsIgnoreCase("ok")) {
//
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "invalid.....", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(getApplicationContext(), "eeeee" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                    ) {
                                        @Override
                                        protected Map<String, String> getParams() {
                                            SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            Map<String, String> params = new HashMap<String, String>();
                                            String voice = "Critical condition";
                                            params.put("did", sh.getString("lid", ""));
                                            params.put("lati", LocationService.lati);
                                            params.put("longi", LocationService.longi);
                                            params.put("cond", cond);

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

                            @Override
                            public void onAccuracyChanged(Sensor sensor, int i) {

                            }

                        }, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Flush and close file stream
        if (mFileStream != null) {
            try {
                mFileStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mFileStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Toast.makeText(mContext, "Service onDestroy", Toast.LENGTH_LONG).show();
        mIsServiceStarted = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Indicates if service is already started or not
     *
     * @return
     */
    public boolean isServiceStarted() {
        return mIsServiceStarted;
    }

}