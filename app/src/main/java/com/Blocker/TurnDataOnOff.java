package com.Blocker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TurnDataOnOff extends Activity {

    Method dataConnSwitchmethod_ON;
    Method dataConnSwitchmethod_OFF;
    Class telephonyManagerClass;
    Object ITelephonyStub;
    Class ITelephonyClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_on_off);


        //checkPermission();
        GetDataConnectionAPI();

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        ToggleButton toggleWifi = (ToggleButton) findViewById(R.id.toggleButtonWifi);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    turnDataOn();
                } else {
                    turnDataOff();
                }
            }
        });

        toggleWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifi.setWifiEnabled(true);

                } else {
                    WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifi.setWifiEnabled(false);

                }
            }
        });

    }

    public void checkPermission()
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;

        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.MODIFY_PHONE_STATE, Manifest.permission.ACCESS_NETWORK_STATE}, 100);

            }
            else {
                GetDataConnectionAPI();
            }
        }
        else {

            GetDataConnectionAPI();
        }
    }
    private void GetDataConnectionAPI() {
        this.getApplicationContext();
        TelephonyManager telephonyManager =
                (TelephonyManager) this.getApplicationContext().
                        getSystemService(Context.TELEPHONY_SERVICE);

        try {
            telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
            Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
            getITelephonyMethod.setAccessible(true);
            ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
            ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());

            dataConnSwitchmethod_OFF = ITelephonyClass.getDeclaredMethod("disableDataConnectivity");
            dataConnSwitchmethod_ON = ITelephonyClass.getDeclaredMethod("enableDataConnectivity");
        } catch (Exception e) { // ugly but works for me
            e.printStackTrace();
        }
    }
    private void turnDataOn() {
        dataConnSwitchmethod_ON.setAccessible(true);
        try {
            dataConnSwitchmethod_ON.invoke(ITelephonyStub);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void turnDataOff() {
        dataConnSwitchmethod_OFF.setAccessible(true);
        try {
            dataConnSwitchmethod_OFF.invoke(ITelephonyStub);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}