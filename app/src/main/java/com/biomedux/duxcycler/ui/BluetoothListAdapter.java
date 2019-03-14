// ============================================================
// FileName		: BluetoothListAdapter.java
// Author		: JaeHong Min
// Date			: 2017.08.02
// ============================================================

package com.biomedux.duxcycler.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.biomedux.duxcycler.R;
import com.biomedux.duxcycler.beans.DeviceInfo;

public class BluetoothListAdapter extends ArrayAdapter<DeviceInfo> {

    // ============================================================
    // Constants
    // ============================================================

    // ============================================================
    // Fields
    // ============================================================

    private ArrayList<DeviceInfo> mDevices;

    // ============================================================
    // Constructors
    // ============================================================

    public BluetoothListAdapter(Context context, int textViewResourceId, ArrayList<DeviceInfo> devices) {
        super(context, textViewResourceId, devices);
        mDevices = devices;
    }

    // ============================================================
    // Getter & Setter
    // ============================================================

    // ============================================================
    // Methods for/from SuperClass/Interfaces
    // ============================================================

    @Override
    public int getCount() {
        return mDevices.size() ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        String name = null;
        String address = null;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.adapter_bluetooth, null);
        }

        name = mDevices.get(position).getName();
        address = mDevices.get(position).getAddress();

        if (name != null && address != null) {
            ((TextView) view.findViewById(R.id.BluetoothAdapter_TextView_DeviceName)).setText(name);
            ((TextView) view.findViewById(R.id.BluetoothAdapter_TextView_DeviceAddress)).setText(address);
        }

        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public DeviceInfo getItem(int position) {
        return mDevices.get(position);
    }

    // ============================================================
    // Methods
    // ============================================================

    // ============================================================
    // Inner and Anonymous Classes
    // ============================================================

}
