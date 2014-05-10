package com.swd.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BGservice extends Service {
	 
	   String tag="TestService";
	   @Override
	   public void onCreate() {
	       super.onCreate();
	       Toast.makeText(this, "Service created...", Toast.LENGTH_LONG).show();      
	       Log.i(tag, "Service created...");
	   }
	 
	   @Override
	   public void onStart(Intent intent, int startId) {      
	       super.onStart(intent, startId);  
	       Log.i(tag, "Service started...");
	   }
	   @Override
	   public void onDestroy() {
	       super.onDestroy();
	       Toast.makeText(this, "Service destroyed...", Toast.LENGTH_LONG).show();
	   }

	   @Override
	   public IBinder onBind(Intent intent) {
	       return null;
	   }
	}