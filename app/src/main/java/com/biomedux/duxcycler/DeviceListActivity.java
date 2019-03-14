// ============================================================
// FileName		: DeviceListActivity.java
// Author		: JaeHong Min
// Date			: 2017.08.02
// ============================================================

package com.biomedux.duxcycler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import com.biomedux.duxcycler.beans.DeviceInfo;
import com.biomedux.duxcycler.bluetooth.BLEService;
import com.biomedux.duxcycler.ui.BluetoothListAdapter;
import com.biomedux.duxcycler.util.DataStorage;

public class DeviceListActivity extends Activity {

    // ============================================================
    // Constants
    // ============================================================

    public static final String EXTRA_DATA					= "com.biomedux.duxcycler.mypcrbluetoothle.BluetoothListActivity.EXTRA_DATA";

    // ============================================================
    // Fields
    // ============================================================

    private ArrayList<DeviceInfo> mDeviceInfo;
    private ArrayList<DeviceInfo> mDeviceNameList;

    private ListView mDeviceList;
    private BluetoothListAdapter mDeviceListAdapter;
    private IntentFilter mIntentFilter;

    private Handler mHandler = new Handler();

    // ============================================================
    // Constructors
    // ============================================================

    // ============================================================
    // Getter & Setter
    // ============================================================

    // ============================================================
    // Methods for/from SuperClass/Interfaces
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        setResult(Activity.RESULT_CANCELED);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BLEService.ACTION_BLE_DEVICE_FOUND);

        mDeviceInfo = new ArrayList<>();
        mDeviceNameList = DataStorage.getInstance().loadDeviceList();

        mDeviceListAdapter = new BluetoothListAdapter(this, android.R.layout.simple_list_item_1, mDeviceInfo);

        mDeviceList = (ListView) findViewById(R.id.ActivityBluetoothList_ListView);
        mDeviceList.setAdapter(mDeviceListAdapter);
        mDeviceList.setOnItemClickListener(bluetooth_item_click);
        mDeviceList.setOnItemLongClickListener(bluetooth_item_long_click);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mGattUpdateReceiver, mIntentFilter);
        mHandler.postDelayed(mTimeoutCheck, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mGattUpdateReceiver);
        mHandler.removeCallbacks(mTimeoutCheck);
    }

    // ============================================================
    // Methods
    // ============================================================

    protected void showEditDialog(final int position, final String name, final String address){
        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceListActivity.this);

        View view = this.getLayoutInflater().inflate(R.layout.dialog_simple_edit, null);

        final EditText editText = (EditText) view.findViewById(R.id.DialogSimpleEdit_EditText);

        editText.setText(name);

        builder.setTitle(name);
        builder.setView(view);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nickname = editText.getText().toString();
                boolean flag = true;

                for(int i = 0;i<mDeviceNameList.size();i++){
                    if (mDeviceNameList.get(i).getAddress().equals(address)){
                        mDeviceNameList.get(i).setName(nickname);
                        flag = false;
                        break;
                    }
                }

                if (flag) {
                    mDeviceNameList.add(new DeviceInfo(nickname, address));
                }

                mDeviceInfo.get(position).setName(nickname);

                DataStorage.getInstance().saveDeviceList(mDeviceNameList);
                mDeviceListAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    // ============================================================
    // Inner and Anonymous Classes
    // ============================================================

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String[] data;

            String name;
            String address;

            if (action.equals(BLEService.ACTION_BLE_DEVICE_FOUND)) {

                mDeviceListAdapter.notifyDataSetChanged();
                // setBLEStatus(BLE_STATE.FOUND);

                data = intent.getStringArrayExtra(BLEService.EXTRA_DATA);
                name = data[0];
                address = data[1];

                // Update
                for (int i = 0; i < mDeviceInfo.size(); i++) {
                    if (mDeviceInfo.get(i).getAddress().equals(address)) {
                        mDeviceInfo.get(i).setDate(System.currentTimeMillis());
                        return;
                    }
                }

                // Add New Device
                for (int i = 0; i < mDeviceNameList.size(); i++) {
                    if (mDeviceNameList.get(i).getAddress().equals(address)) {
                        name = mDeviceNameList.get(i).getName();
                        break;
                    }
                }

                mDeviceInfo.add(new DeviceInfo(name, address, System.currentTimeMillis()));
                mDeviceListAdapter.notifyDataSetChanged();
            }
        }
    };

    private final Runnable mTimeoutCheck = new Runnable() {
        @Override
        public void run() {
            int size = mDeviceInfo.size();

            for (int i = 0; i < size; i++) {
                if (System.currentTimeMillis() - mDeviceInfo.get(i).getDate() > 10000L) {
                    mDeviceInfo.remove(i);
                    size--;
                    i--;
                }
            }

            mDeviceListAdapter.notifyDataSetChanged();
            mHandler.postDelayed(mTimeoutCheck, 1000);
        }
    };

    private final AdapterView.OnItemClickListener bluetooth_item_click = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String address = ((TextView) view.findViewById(R.id.BluetoothAdapter_TextView_DeviceAddress)).getText().toString();

            Intent intent = getIntent();
            intent.putExtra(EXTRA_DATA, address);
            setResult(Activity.RESULT_OK, intent);

            finish();
        }
    };

    private final AdapterView.OnItemLongClickListener bluetooth_item_long_click = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            TextView device_name_text = (TextView) view.findViewById(R.id.BluetoothAdapter_TextView_DeviceName);
            TextView device_address_text = (TextView) view.findViewById(R.id.BluetoothAdapter_TextView_DeviceAddress);

            String name = device_name_text.getText().toString();
            String address = device_address_text.getText().toString();

            showEditDialog(position, name, address);

            return true;
        }
    };
}
