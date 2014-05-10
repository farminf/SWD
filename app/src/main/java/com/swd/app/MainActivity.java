package com.swd.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

    private static final String FILENAME = "Bluetooth.txt";
    String sendFile;
    String discoveredTime;
    String inputString;
    String ReqDeviceName;
	Button Exit;
	BluetoothAdapter btAdapter;
	Button PairedDevicebtn;
	IntentFilter filter;
	BroadcastReceiver receiver;
	ArrayList<String> pairedDevices;
	ArrayList<String> listAdapter;
	Set<BluetoothDevice> devicesArray;
	ArrayList<BluetoothDevice> devices;
	String deviceMac;
	BluetoothDevice selectedDevice;
	public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;
    String tag = "debugging";
	Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Log.i(tag, "in handler");
            super.handleMessage(msg);
            switch(msg.what){
            case SUCCESS_CONNECT:
                // DO something
                ConnectedThread connectedThread = new ConnectedThread((BluetoothSocket)msg.obj);
                Toast.makeText(getApplicationContext(), "CONNECT", Toast.LENGTH_SHORT).show();
                String s = "successfully connected";
                connectedThread.write(s.getBytes());
                Log.i(tag, "connected");
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[])msg.obj;
                String string = new String(readBuf);
                Toast.makeText(getApplicationContext(), string,  Toast.LENGTH_LONG).show();
                break;

            }
        }
    };
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	    /*	if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	    */
		
		//Checking Device Bluetooth
		CheckbluetoothAdapter();
		if(btAdapter == null) {
			Toast.makeText(getApplicationContext(),"No BT module detected on your device",Toast.LENGTH_LONG).show();
		}
		else{
			if(!btAdapter.isEnabled()) {
				TurnOnBT();
				
			}
			
		}
        //


        // Exit button for killing app
        Killapp();
        //
        // Paired Devices Page
        PairedDevices();
        //
        // Starting Discovery
        startDiscovery();
        //

        // Get the name of required Bluetooth Device from string
        //SharedPreferences sharedPref = getPreferences(getApplicationContext().MODE_PRIVATE);
        ReqDeviceName = getString(R.string.DeviceName);


        // My Main Function
        getDiscoveredDevices();
        //
        checkExternalStorage();

	}

    private void checkExternalStorage() {
        if (isExternalStorageWritable()) {

            Toast.makeText(getApplicationContext(),"SD Card is ready",Toast.LENGTH_SHORT).show();

            //Write to file
            //generateNoteOnSD(sendFile,inputString);

        }
        else {

            Toast.makeText(getApplicationContext(),"SD Card is not ready",Toast.LENGTH_SHORT).show();
        }
    }
//----------------------------------------------------------------------
	
	private void getDiscoveredDevices() {
		// TODO Auto-generated method stub
		startDiscovery();
		
		
		
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		devicesArray = btAdapter.getBondedDevices();
		listAdapter= new ArrayList<String>();
    		pairedDevices = new ArrayList<String>();
		
		//putting Paired Devices to array
        if(devicesArray.size()>0){
            for(BluetoothDevice device:devicesArray){
                pairedDevices.add(device.getName());
                 
            }
        }
		//--------------
		//discover close devices 
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		receiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				
				String action = intent.getAction();
				if(BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					Log.v("",device.toString());

					//Putting Discovered Devices in listAdapter
					listAdapter.add(device.getName());

                    for (int a = 0; a < listAdapter.size(); a++){

						Log.v(" ",ReqDeviceName);
                        String listAdapterString = listAdapter.get(a);
                        Log.v(" ",listAdapterString);

					    //Comparing the names of new devices with "my device"
						if(listAdapterString.contains(ReqDeviceName))
						{

                            //Making String for File,include the time of discovering our requested device

                            Calendar c = Calendar.getInstance();
                            discoveredTime = c.getTime().toString();
                            if  (discoveredTime != null) {
                                Log.v(" ", discoveredTime);
                            }
                            inputString = "the " + ReqDeviceName + " has discovered in " + discoveredTime;

                            //Write to File
                            //generateNoteOnSD(sendFile,inputString);
                            writeToFile(inputString);


                            //For Connecting
                            selectedDevice = device;
							Toast.makeText(getApplicationContext(), "Device Founded", Toast.LENGTH_SHORT).show();


						    //Connecting
							connectAsServer();

						
						}
                        else
                            {
                            Toast.makeText(getApplicationContext(), "Device not Founded", Toast.LENGTH_SHORT).show();
                            //getDiscoveredDevices();

                             }

					}


						
					
				}
				else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){


                }
                else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){

                	 Log.v(" ","discovery Finished ");
                     if(listAdapter.size() != 0)
                     {
                        //deviceList.invalidateViews();
                         //sectionAdapter.notifyDataSetChanged();
                         Toast.makeText(MainActivity.this, "Some New Devices Found", Toast.LENGTH_LONG).show();

                     }
                     else
                     {
                         startDiscovery();
                         Toast.makeText(MainActivity.this, "No New Devices Found", Toast.LENGTH_LONG).show();
                     }


                }
                else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                    if(btAdapter.getState() == btAdapter.STATE_OFF){
                        TurnOnBT();
                    }

                }
			}
//Connecting
			private void connectAsServer() {
				// TODO Auto-generated method stub
			//Check to cancel Discovery
				if(btAdapter.isDiscovering()){
	                btAdapter.cancelDiscovery();
				}
			//
				ConnectThread connect = new ConnectThread(selectedDevice);
                connect.start();

			
			}	
		};
		
		
		registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
       registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
       registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
       registerReceiver(receiver, filter);
	}
	
//Unregistering when the app is not active	
	@Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        unregisterReceiver(receiver);
    }
 
	
	
//----------------------------------------------------------------	
	
// Discovery

	private void startDiscovery() {
		// TODO Auto-generated method stub
		btAdapter.cancelDiscovery();
        btAdapter.startDiscovery();
//        while (btAdapter.isDiscovering()) {
//
//        }
//
        Log.d("Save", "After - btAdapter.isDiscovering()" + btAdapter.isDiscovering());
//
	}
	
//Turning On the Bluetooth

	private void TurnOnBT() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(intent,1);
	}
//Clicking on Paired Device Button
	private void PairedDevices() {
		// TODO Auto-generated method stub
		
		PairedDevicebtn = (Button) findViewById(R.id.PairedDevicesbtn);
		PairedDevicebtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent pairedDevices = new Intent(MainActivity.this,PairedDevices.class);
				startActivity(pairedDevices);
				
			}
		});
		
	}
//Killing the app
	private void Killapp() {
		// TODO Auto-generated method stub
		//Creating Exit Button
				Exit = (Button) findViewById(R.id.Kill);
		        Exit.setOnClickListener(new OnClickListener() {
				
			      @Override
			        public void onClick(View v) {
			            // TODO Auto-generated method stub
			            finish();
			            System.exit(0);
			        }
			    });
	}
//Checking Bluetooth Status
	private void CheckbluetoothAdapter() {
		// TODO Auto-generated method stub
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		
	}

    
//Warn user to turn on the Bluetooth	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode , data);
		if(resultCode == RESULT_CANCELED){
				Toast.makeText(getApplicationContext(), "BT must be enabled", Toast.LENGTH_SHORT).show();
				finish();
				
		}
		
	}

	
	private class ConnectThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;
	 
	    public ConnectThread(BluetoothDevice device) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        mmDevice = device;
	 
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            // MY_UUID is the app's UUID string, also used by the server code
	        	// Insecure
	            tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
	        } catch (IOException e) { }
	        mmSocket = tmp;
	    }
	 
	    public void run() {
	        // Cancel discovery because it will slow down the connection
	      btAdapter.cancelDiscovery();
	 
	        try {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	            mmSocket.connect();
	        } catch (IOException connectException) {
	            // Unable to connect; close the socket and get out
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) { }
	            return;
	        }
	 
	        // Do work to manage the connection (in a separate thread)
	       
	        mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
	        // manageConnectedSocket(mmSocket);
	    }
	 
	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
	
	
	
	
	private class ConnectedThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final InputStream mmInStream;
	    private final OutputStream mmOutStream;
	 
	    public ConnectedThread(BluetoothSocket socket) {
	        mmSocket = socket;
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;
	 
	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) { }
	 
	        mmInStream = tmpIn;
	        mmOutStream = tmpOut;
	    }
	 
	    public void run() {
	        byte[] buffer = new byte[1024];  // buffer store for the stream
	        int bytes; // bytes returned from read()
	 
	        // Keep listening to the InputStream until an exception occurs
	        while (true) {
	            try {
	                // Read from the InputStream
	                bytes = mmInStream.read(buffer);
	                // Send the obtained bytes to the UI activity
	                mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
	                        .sendToTarget();
	            } catch (IOException e) {
	                break;
	            }
	        }
	    }
	 
	    /* Call this from the main activity to send data to the remote device */
	    public void write(byte[] bytes) {
	        try {
	            mmOutStream.write(bytes);
	        } catch (IOException e) { }
	    }
	 
	    /* Call this from the main activity to shutdown the connection */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
	
	
//Menu ----------------------------------------------------------------------
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
            Intent SettingActivity = new Intent(MainActivity.this,SettingActivity.class);
            startActivity(SettingActivity);
			//return true;
		}
		return super.onOptionsItemSelected(item);
	}



    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


    private void writeToFile(String data) {
        try {
            File myFile = new File("/sdcard/Bluetooth.txt");
            //check if file already exist
            if(!myFile.isFile()) {

                myFile.createNewFile();

            }
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fOut);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            fOut.close();
        }
        catch (Exception e) {
            Log.e("ERRR", "Could not create file",e);
        }
    }
//    public  generateNoteOnSD(String FileName, String Body){
//
//
//        //File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), FileName);
//        File file = new File(Environment.getExternalStorageDirectory(), "Notes");
//        FileWriter writer = new FileWriter(gpxfile);
//
//        if (!file.mkdirs()) {
//            Log.e(" ", "Directory not created");
//        }
//        return file;
//    }
//
//        try
//        {
//            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
//            if (!root.exists()) {
//                root.mkdirs();
//            }
//            File gpxfile = new File(root, sFileName);
//            FileWriter writer = new FileWriter(gpxfile);
//            writer.append(sBody);
//            writer.flush();
//            writer.close();
//            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
//        }
//        catch(IOException e)
//        {
//            e.printStackTrace();
////            importError = e.getMessage();
////            iError();
//        }
//    }


//	public static class PlaceholderFragment extends Fragment {
//
//		public PlaceholderFragment() {
//		}
//
//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container,
//				Bundle savedInstanceState) {
//			View rootView = inflater.inflate(R.layout.fragment_main, container,
//					false);
//			return rootView;
//		}
//
    }

	
	

