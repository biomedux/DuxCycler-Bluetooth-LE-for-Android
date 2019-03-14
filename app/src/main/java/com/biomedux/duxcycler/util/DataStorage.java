// ============================================================
// FileName		: DataStorage.java
// Author		: JaeHong Min
// Date			: 2017.07.04
// ============================================================

package com.biomedux.duxcycler.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

import com.biomedux.duxcycler.beans.Action;
import com.biomedux.duxcycler.beans.DeviceInfo;
import com.biomedux.duxcycler.beans.Protocol;

public class DataStorage {

    // ============================================================
    // Constants
    // ============================================================

    // ============================================================
    // Fields
    // ============================================================

    private static DataStorage instance;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    // ============================================================
    // Constructors
    // ============================================================

    // ============================================================
    // Getter & Setter
    // ============================================================

    // ============================================================
    // Methods for/from SuperClass/Interfaces
    // ============================================================

    // ============================================================
    // Methods
    // ============================================================

    public static DataStorage getInstance() {
        if(instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    public void initial(Context context, String name) {
        pref = context.getSharedPreferences(name, Activity.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void put(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void put(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void put(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public void put(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public boolean get(String key, boolean value) {
        try {
            return pref.getBoolean(key, value);
        } catch (Exception e) {
            return value;
        }
    }

    public int get(String key, int value) {
        try {
            return pref.getInt(key, value);
        } catch (Exception e) {
            return value;
        }
    }

    public long get(String key, long value) {
        try {
            return pref.getLong(key, value);
        } catch (Exception e) {
            return value;
        }
    }

    public String get(String key, String value) {
        try {
            return pref.getString(key, value);
        } catch (Exception e) {
            return value;
        }
    }

    public int load(ArrayList<Protocol> protocols) {
        int protocolLength = get("protocol_length", 0);
        int currentIndex = get("current_protocol", -1);
        int count = 0;

        for (int i = 0; i < protocolLength; i++) {
            ArrayList<Action> actions = new ArrayList<Action>();
            String title = get("title" + i, "null");
            int actionLength = get("action_length" + i, 0);

            for (int j = 0; j < actionLength; j++) {
                String label = get("label" + i + "," + j, "null");
                String temp = get("temp" + i + "," + j, "null");
                String time = get("time" + i + "," + j, "null");
                actions.add(new Action(label, temp, time));
            }

            if (actions.size() == 0) {
                if (currentIndex < i)
                    count++;
                continue;
            }

            protocols.add(new Protocol(title, actions));
        }

        return currentIndex - (currentIndex == -1 ? 0 : count);
    }

    public void save(ArrayList<Protocol> protocols, int currentProtocolIndex) {
        put("protocol_length", protocols.size());

        for (int i = 0; i < protocols.size(); i++) {
            put("title" + i, protocols.get(i).getTitle());
            put("action_length" + i, protocols.get(i).getActions().size());

            for (int j = 0; j < protocols.get(i).getActions().size(); j++) {
                put("label" + i + "," + j, protocols.get(i).getActions().get(j).getLabel());
                put("temp" + i + "," + j, protocols.get(i).getActions().get(j).getTemp());
                put("time" + i + "," + j, protocols.get(i).getActions().get(j).getTime());
            }
        }

        if (currentProtocolIndex == -1)
            put("current_protocol", -1);
        else if (protocols.get(currentProtocolIndex).getActions().size() == 0)
            put("current_protocol", -1);
        else
            put("current_protocol", currentProtocolIndex);
    }

    public ArrayList<DeviceInfo> loadDeviceList(){
        ArrayList<DeviceInfo> mDeviceInfo = new ArrayList<>();
        int deviceInfo_size = get("deviceInfo_size", 0);

        for(int i = 0 ;i < deviceInfo_size;i++){
            String name = get("device_name" + i, null);
            String address = get("device_address" + i, null);
            mDeviceInfo.add(new DeviceInfo(name, address));
        }

        return mDeviceInfo;
    }

    public void saveDeviceList(ArrayList<DeviceInfo> mDeviceInfo){
        put("deviceInfo_size",mDeviceInfo.size());

        for(int i = 0 ;i < mDeviceInfo.size();i++){
            put("device_name" + i, mDeviceInfo.get(i).getName());
            put("device_address" + i, mDeviceInfo.get(i).getAddress());
        }
    }

    // ============================================================
    // Inner and Anonymous Classes
    // ============================================================

}
