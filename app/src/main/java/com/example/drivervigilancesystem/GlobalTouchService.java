package com.example.drivervigilancesystem;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class GlobalTouchService extends Service implements OnTouchListener{

        private String TAG = this.getClass().getSimpleName();
        // window manager
        private WindowManager mWindowManager;
        // linear layout will use to detect touch event
        private LinearLayout touchLayout;
        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }
        @Override
        public void onCreate() {
            super.onCreate();
            // create linear layout
            touchLayout = new LinearLayout(this);
            // set layout width 30 px and height is equal to full screen
            LayoutParams lp = new LayoutParams(300, LayoutParams.MATCH_PARENT);
            touchLayout.setLayoutParams(lp);
            // set color if you want layout visible on screen
//		touchLayout.setBackgroundColor(Color.CYAN);
            // set on touch listener
            touchLayout.setOnTouchListener(this);

            // fetch window manager object
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            // set layout parameter of window manager

            int LAYOUT_FLAG;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
            }

            WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            mParams.gravity = Gravity.LEFT | Gravity.TOP;
            Log.i(TAG, "add View");
            mWindowManager.addView(touchLayout, mParams);

//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
//                        WindowManager.LayoutParams.WRAP_CONTENT, // width of layout 30 px
//                        WindowManager.LayoutParams.WRAP_CONTENT, // height is equal to full screen
//                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // Type Ohone, These are non-application windows providing user interaction with the phone (in particular incoming calls).
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // this window won't ever get key input focus
//                        PixelFormat.TRANSLUCENT);
//
//            }else{
//
//                WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
//                        WindowManager.LayoutParams.MATCH_PARENT, // width of layout 30 px
//                        WindowManager.LayoutParams.MATCH_PARENT, // height is equal to full screen
//                        WindowManager.LayoutParams.TYPE_PHONE, // Type Ohone, These are non-application windows providing user interaction with the phone (in particular incoming calls).
//                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // this window won't ever get key input focus
//                        PixelFormat.TRANSLUCENT);
//                mParams.gravity = Gravity.LEFT | Gravity.TOP;
//                Log.i(TAG, "add View");
//
//                mWindowManager.addView(touchLayout, mParams);
//            }





        }


        @Override
        public void onDestroy() {
            if(mWindowManager != null) {
                if(touchLayout != null) mWindowManager.removeView(touchLayout);
            }
            super.onDestroy();
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            Toast.makeText(getApplicationContext(),"Event occured"+event.getAction(),Toast.LENGTH_SHORT).show();

            Log.d("Event name",event.getAction()+"");

            if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP)
                Log.d(TAG, "Action :" + event.getAction() + "\t X :" + event.getRawX() + "\t Y :"+ event.getRawY());

            return true;
        }
    }
