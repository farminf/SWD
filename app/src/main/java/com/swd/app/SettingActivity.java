package com.swd.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by farminfarzin on 5/8/14.
 */
public class SettingActivity extends Activity {

    SharedPreferences setting;
    SharedPreferences.Editor settingEditor;
    Button Apply ;
    EditText DeviceName;
    String Device;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);





        //Device = "Null";
        Apply = (Button) findViewById(R.id.button);
        DeviceName = (EditText) findViewById(R.id.InputDeviceName);
//        Device = DeviceName.getText().toString();
        //Device.equals(DeviceName);
        Apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               // Apply = (Button) findViewById(R.id.button);
               //DeviceName = (EditText) findViewById(R.id.InputDeviceName);
                Device = DeviceName.getText().toString();

                setting = getPreferences(getApplicationContext().MODE_PRIVATE);
                settingEditor = setting.edit();
                settingEditor.putString(getString(R.string.DeviceName),Device);
                settingEditor.commit();


                Intent i = new Intent(SettingActivity.this, MainActivity.class);
                //i.putExtra("KEY",Device);
                startActivity(i);
            }

        });
    }
}
