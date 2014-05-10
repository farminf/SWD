package com.swd.app;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PairedDevices extends Activity {
	
	ArrayAdapter<String> listAdapter;
	Set<BluetoothDevice> devicesArray;
	BluetoothAdapter btAdapter;
	ListView listView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pair_devices);
		
		getPairedDevices();

	
	
	}

	private void getPairedDevices() {
		// TODO Auto-generated method stub
		listView = (ListView) findViewById(R.id.pairDevices);
		listAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0);
		listView.setAdapter(listAdapter);
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		devicesArray = btAdapter.getBondedDevices();
		
		if (devicesArray.size() > 0){
			for (BluetoothDevice device:devicesArray){
				
				listAdapter.add(device.getName()+"\n"+device.getAddress());	
			}
		}
		else {
			Toast.makeText(getApplicationContext(), "There is not any Device", Toast.LENGTH_SHORT).show();
		}
	}
	

}
