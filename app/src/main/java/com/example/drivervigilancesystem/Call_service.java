package com.example.drivervigilancesystem;
import android.app.NotificationManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;




public class Call_service extends Service {
	String imei;
	String opn;
	String dt="",tm="";
	long diffinmin,diffinhr;
	TelephonyManager telephonyManager;
	TelephonyManager telman;

	 public static int flg=0;	
	 String phnop="";
	// @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onCreate() 
	 {
		super.onCreate();



		 SimpleDateFormat tet=new SimpleDateFormat("hh:mm:ss");
		 tm=tet.format(new Date());
		 telman=(TelephonyManager)getApplicationContext().getSystemService(TELEPHONY_SERVICE);
		
		 telman.listen(phlist,PhoneStateListener.LISTEN_CALL_STATE);
		 Log.d("....old...", ".....00");

		 deviceManger = (DevicePolicyManager)getSystemService(
				 DEVICE_POLICY_SERVICE);
	 }


	DevicePolicyManager deviceManger;
	public PhoneStateListener phlist=new PhoneStateListener()
   {
	   public void onCallStateChanged(int state, String inNum) 
	   {
		
		  switch (state) 
		  {

		     case TelephonyManager.CALL_STATE_IDLE:

		         break;

		     case TelephonyManager.CALL_STATE_OFFHOOK:

		    	 		SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		    	 		String block=sh.getString("blk","" );
		    	 		if(block.equalsIgnoreCase("on")){
								try{
									Class c= null;
									c=Class.forName(telephonyManager.getClass().getName());
									Method m=c.getDeclaredMethod("getITelephony");
									m.setAccessible(true);
									ITelephony telephonyService =(ITelephony) m.invoke(telephonyManager);
									telephonyService.endCall();
								} catch (ClassNotFoundException e){
									e.printStackTrace();
								} catch (NoSuchMethodException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (InvocationTargetException e){
									e.printStackTrace();
								}
                            }
						 else {
							AudioManager am=(AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
							am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
						}

                 break;
		    	 		
	
		     case TelephonyManager.CALL_STATE_RINGING:

		         phnop=inNum;
				 sh=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				 if(sh.getString("blk","").equalsIgnoreCase("on")) {

					 try{
						 Class c= null;
						 c=Class.forName(telephonyManager.getClass().getName());
						 Method m=c.getDeclaredMethod("getITelephony");
						 m.setAccessible(true);
						 ITelephony telephonyService =(ITelephony) m.invoke(telephonyManager);
						 telephonyService.endCall();
					 } catch (ClassNotFoundException e){
						 e.printStackTrace();
					 } catch (NoSuchMethodException e) {
						 e.printStackTrace();
					 } catch (IllegalAccessException e) {
						 e.printStackTrace();
					 } catch (InvocationTargetException e){
						 e.printStackTrace();
					 }
				 }
				 else {
					 AudioManager am=(AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
					 am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				 }
				 break;
		  }	

	   }

   };

	public IBinder onBind(Intent arg0) {
		
		return null;
	}

}