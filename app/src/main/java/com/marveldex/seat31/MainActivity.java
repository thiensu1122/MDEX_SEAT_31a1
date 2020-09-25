
/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.marveldex.seat31;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.marveldex.seat31.Model.SensorData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.marveldex.seat31.UartService.EXTRA_DATA;
import static com.marveldex.seat31.UartService.EXTRA_DATA_STRING;
/**
 *
 * @mainpage SCMS( Sitting Cushion Management System) - Marveldex
 * @brief 스마트 방석을 이용한 Application :
 * @details 방석의 각 압력 센서의 값을 VENUS 보드를 통해 전달 받아 데이터를 처리하여 Application을 통해 여러 형태의 값으로 보여주는 기능을 하는 Source
 *
 * @brief 2번째 설명 :
 * @details 방석의 각 압력 센서의 값을 VENUS 보드를 통해 전달 받아 데이터를 처리하여 Application을 통해 여러 형태의 값으로 보여주는 기능을 하는 Source
 *
 */

/**
 *
 * @file MainActivity.java
 * @brief 메인 기능을 수행하는 파일
 *
 */

/**
 *
 * @brief this is main function for run this app
 * @details show values of sence and save to CSV File
 * @author Marveldex
 * @date 2017-03-17
 * @version 0.0.1
 * @li list1
 * @li list2
 *
 */



public class   MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener {
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    //private static final int UART_PROFILE_READY = 10;
    public static final String TAG = "nRFUART";
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;

    private int m_State = UART_PROFILE_DISCONNECTED;
    private UartService m_UartService = null;

    private BluetoothDevice m_Device = null;

    private BluetoothAdapter m_BtAdapter = null;
    //private ListView messageListView;
    //private ArrayAdapter<String> listAdapter;
    private Button mbtn_ConnectDisconnect,mbtn_Send,mbtn_Disconnect;

    ArrayList<String> m_DetailList;
    ArrayAdapter<String> m_DetailAdapter;
    ListView mlv_DetailStateList;
    
    private float m_LateralVector;
    private String m_Mode_Info;
    private TextView m_Mode_TxtView;
    private int m_ToastFlag = 0;
    RelativeLayout mrl_ui_coc_com_layout;
    SharedPreferences pref;

    private SensorData sensorData = new SensorData();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        m_BtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (m_BtAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        pref = getSharedPreferences("MacAddr", Activity.MODE_PRIVATE);
/*
        messageListView = (ListView) findViewById(R.id.listMessage);
        listAdapter = new ArrayAdapter<String>(this, R.layout.message_detail);
        messageListView.setAdapter(listAdapter);
        messageListView.setDivider(null);
*/
        mbtn_ConnectDisconnect =(Button) findViewById(R.id.btn_select);
        mbtn_Disconnect = (Button) findViewById(R.id.btn_disconnect);
        mrl_ui_coc_com_layout = (RelativeLayout)findViewById(R.id.RelativeLayout_COM);

        service_init();

        mbtn_ConnectDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!m_BtAdapter.isEnabled()) {
                    Log.i(TAG, "onClick - BT not enabled yet");
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                }
                else {

                    Intent newIntent = new Intent(MainActivity.this, com.marveldex.seat31.DeviceListActivity.class);
                    startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                }

                m_ToastFlag = 0;

            }
        });

        mbtn_Disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_Device!=null) {
                    m_UartService.disconnect();

                }
            }
        });

    }

    //UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            m_UartService = ((com.marveldex.seat31.UartService.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected m_UartService= " + m_UartService);
            if (!m_UartService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            if(m_UartService != null) {
                //m_UartService.disconnect(m_Device);
                m_UartService.disconnect();
                //m_UartService = null;
            }
            //m_UartService = null;
        }
    };


    private Handler mHandler = new Handler() {
        @Override
        
        //Handler events that received from UART service 
        public void handleMessage(Message msg) {
            Log.i(TAG, "Uart service handleMessage message= " + msg);
        }
    };

    /**
     *
     * @brief Receive Broadcast and Check and Action each Function
     * @details  It receives data from the UartService.java file as a broadcast and performs its function through its value. Manage Bluetooth and device connection status.
     * @param
     * @return
     * @throws
     */
    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;
           //*********************//
            if (action.equals(com.marveldex.seat31.UartService.ACTION_GATT_CONNECTED)) {
            	 runOnUiThread(new Runnable() {
                     public void run() {
                         if (m_Device == null) return;
                         SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
                         Calendar cal = Calendar.getInstance();
                         String time_str = dateFormat.format(cal.getTime());

                         Log.d(TAG, "ACTION_GATT_CONNECTED " + time_str);

                         ((TextView) findViewById(R.id.deviceName)).setText(m_Device.getName()+ " - connected " + time_str);

                         mbtn_ConnectDisconnect.setText("Connect (" +m_UartService.getConncetedDeviceCount()+ ")");
/*
                         listAdapter.add("["+currentDateTimeString+"] Connected to: "+ m_Device.getName());
                         messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
*/

                         //연결 완료  - 맥어드레스 저장
                         //업데이트
                         SharedPreferences.Editor editor = pref.edit();
                         editor.putString("MacAddr",m_Device.getAddress());
                         editor.commit();

                         m_State = UART_PROFILE_CONNECTED;
                     }
            	 });
            }
           
          //*********************//
            if (action.equals(com.marveldex.seat31.UartService.ACTION_GATT_DISCONNECTED)) {
            	 runOnUiThread(new Runnable() {
                     public void run() {
                    	 	 String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                             Log.d(TAG, "ACTION_GATT_DISCONNECTED");
//                             mbtn_ConnectDisconnectFirst.setText("Connect");
//                             mbtn_ConnectDisconnectSecond.setText("Connect");
                             ((TextView) findViewById(R.id.deviceName)).setText("Not Connected");
/*
                             listAdapter.add("["+currentDateTimeString+"] Disconnected to: "+ m_Device.getName());
*/
                             mbtn_ConnectDisconnect.setText("Connect");
                             m_State = UART_PROFILE_DISCONNECTED;
                             m_UartService.close();
                             sensorData.clearIndicator(1);
                             sensorData.clearIndicator(2);
                     }
                 });
            }


          //*********************//
            if (action.equals(com.marveldex.seat31.UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                String address = intent.getStringExtra(EXTRA_DATA_STRING);
                m_UartService.enableTXNotification(address);
                Log.d(TAG, "ACTION_GATT_SERVICES_DISCOVERED");
            }

            //-----------------------------------------------------
            // HERE RECEIVE RAW BLE DATA AND PARSE
            //-----------------------------------------------------

            if (action.equals(com.marveldex.seat31.UartService.ACTION_DATA_AVAILABLE)) {
                final byte[] packetVenus2Phone = intent.getByteArrayExtra(EXTRA_DATA);

                runOnUiThread(new Runnable() {
                     public void run() {
                         try {
/*
                         	String text = new String(packetVenus2Phone, "UTF-8");
                         	String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                             listAdapter.add("["+currentDateTimeString+"] RX: "+text);
                             messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
*/

                         } catch (Exception e) {
                             Log.e(TAG, e.toString());
                         }

                         sensorData.onReceiveRawBytes(packetVenus2Phone);
//                         sensorData.printData();


                     }

                 });
             }
           //*********************//
            if (action.equals(com.marveldex.seat31.UartService.DEVICE_DOES_NOT_SUPPORT_UART)){
            	showMessage("Device doesn't support UART. Disconnecting");
            	//m_UartService.disconnect();
            }
        }
    };


    private void service_init() {
        Intent bindIntent = new Intent(this, com.marveldex.seat31.UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(com.marveldex.seat31.UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(com.marveldex.seat31.UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(com.marveldex.seat31.UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(com.marveldex.seat31.UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(com.marveldex.seat31.UartService.DEVICE_DOES_NOT_SUPPORT_UART);

        //      gap messages
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        //intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);

        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        return intentFilter;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
    	 super.onDestroy();
        Log.d(TAG, "onDestroy()");
        
        try {
        	LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        } 
        unbindService(mServiceConnection);

        m_UartService.stopSelf();
        m_UartService= null;

       
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!m_BtAdapter.isEnabled()) {
            Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
 
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

        case REQUEST_SELECT_DEVICE:
                //When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    m_Device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);

                    Log.d(TAG, "... onActivityResultdevice.address==" + m_Device + "mserviceValue" + m_UartService);
                    ((TextView) findViewById(R.id.deviceName)).setText(m_Device.getName()+ " - connecting");
//                    mbtn_ConnectDisconnect.setText("Connecting...");

                    m_UartService.connect(deviceAddress);
                }
                break;

        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();

            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show();
                finish();
            }
            break;
        default:
            Log.e(TAG, "wrong request code");
            break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (m_State == UART_PROFILE_CONNECTED) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            showMessage("nRFUART's running in background.\n             Disconnect to exit");
        }
        else {
            new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.popup_title)
            .setMessage(R.string.popup_message)
            .setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
   	                finish();
                }
            })
            .setNegativeButton(R.string.popup_no, null)
            .show();
        }
    }



}