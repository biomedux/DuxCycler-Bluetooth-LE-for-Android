// ============================================================
// FileName		: BLEService.java
// Author		: JaeHong Min
// Date			: 2017.08.01
// ============================================================

package com.biomedux.duxcycler.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.List;

public class BLEService extends Service {

    // ============================================================
    // Constants
    // ============================================================

    public static final String ACTION_BLE_CONNECTED				= "com.biomedux.duxcycler.bluetooth.BLEService.ACTION_BLE_CONNECTED";
    public static final String ACTION_BLE_DISCONNECTED			= "com.biomedux.duxcycler.bluetooth.BLEService.ACTION_BLE_DISCONNECTED";
    public static final String ACTION_BLE_SERVICES_DISCOVERED	= "com.biomedux.duxcycler.bluetooth.BLEService.ACTION_BLE_SERVICES_DISCOVERED";
    public static final String ACTION_BLE_DATA_AVAILABLE			= "com.biomedux.duxcycler.bluetooth.BLEService.ACTION_BLE_DATA_AVAILABLE";
    public static final String ACTION_BLE_DEVICE_FOUND			= "com.biomedux.duxcycler.bluetooth.BLEService.ACTION_BLE_DEVICE_FOUND";
    public static final String EXTRA_DATA							= "com.biomedux.duxcycler.bluetooth.BLEService.EXTRA_DATA";

    private final IBinder mBinder	= new LocalBinder();

    // ============================================================
    // Fields
    // ============================================================

    private BluetoothManager mBLEManager;
    private BluetoothAdapter mBLEAdapter;

    private BluetoothGatt mBLEGatt;

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
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    // ============================================================
    // Methods
    // ============================================================

    public boolean initialize() {
        if (mBLEManager == null) {
            mBLEManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

            if (mBLEManager == null)
                return false;
        }

        mBLEAdapter = mBLEManager.getAdapter();

        return mBLEAdapter != null;
    }

    public boolean isBluetoothEnabled() {
        if (mBLEAdapter == null)
            return false;

        return mBLEAdapter.isEnabled();
    }

    public void scanBLEDevice(boolean enable) {
        if (mBLEAdapter == null)
            return;

        BluetoothLeScanner scanner = mBLEAdapter.getBluetoothLeScanner();

        if (scanner == null)
            return;

        if (enable)
            scanner.startScan(mScanCallback);
        else
            scanner.stopScan(mScanCallback);
    }

    public boolean connect(String address) {
        if (mBLEAdapter == null || address == null)
            return false;

        BluetoothDevice device = mBLEAdapter.getRemoteDevice(address);

        if (device == null)
            return false;

        mBLEGatt = device.connectGatt(this, false, mBLEGattCallback);

        return true;
    }

    public void disconnect() {
        if (mBLEAdapter == null || mBLEGatt == null)
            return;

        mBLEGatt.disconnect();
    }

    public void close() {
        if (mBLEGatt == null)
            return;

        mBLEGatt.close();
        mBLEGatt = null;
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBLEAdapter == null || mBLEGatt == null)
            return;

        mBLEGatt.readCharacteristic(characteristic);
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] data) {
        if (mBLEAdapter == null || mBLEGatt == null)
            return;
        characteristic.setValue(data);
        mBLEGatt.writeCharacteristic(characteristic);
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable) {
        if (mBLEAdapter == null || mBLEGatt == null)
            return;
/*
        mBLEGatt.setCharacteristicNotification(characteristic, enable);

        if (characteristic.getUuid().equals(BLEUUID.RX)) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(BLEUUID.CHARACTERISTIC_CONFIG);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBLEGatt.writeDescriptor(descriptor);
        }*/


        // Setup notifications on RX characteristic changes (i.e. data received).
        // First call setCharacteristicNotification to enable notification.
        if (!mBLEGatt.setCharacteristicNotification(characteristic, true)) {
            // Stop if the characteristic notification setup failed.

            return;
        }
        // Next update the RX characteristic's client descriptor to enable notifications.
        BluetoothGattDescriptor desc = characteristic.getDescriptor(BLEUUID.CLIENT);
        if (desc == null) {
            // Stop if the RX characteristic has no client descriptor.

            return;
        }
        desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        if (!mBLEGatt.writeDescriptor(desc)) {
            // Stop if the client descriptor could not be written.

            return;
        }

    }

    public BluetoothGattCharacteristic getFirmtechRxCharacteristic() {
        if (mBLEAdapter == null || mBLEGatt == null )
            return null;

        List<BluetoothGattService> gattServices = mBLEGatt.getServices();
        BluetoothGattCharacteristic characteristic = null;

        for (int i = 0; i < gattServices.size(); i++) {
            String uuid = gattServices.get(i).getUuid().toString();
            if (uuid.equals(BLEUUID.UART.toString())) {
                characteristic = gattServices.get(i).getCharacteristic(BLEUUID.RX);
            }
        }

        return characteristic;
    }

    public BluetoothGattCharacteristic getFirmtechTxCharacteristic() {
        if (mBLEAdapter == null || mBLEGatt == null)
            return null;

        List<BluetoothGattService> gattServices = mBLEGatt.getServices();
        BluetoothGattCharacteristic characteristic = null;

        for (int i = 0; i < gattServices.size(); i++) {
            String uuid = gattServices.get(i).getUuid().toString();

            if (uuid.equals(BLEUUID.UART.toString()))
                characteristic = gattServices.get(i).getCharacteristic(BLEUUID.TX);
        }

        return characteristic;
    }

    private void broadcastUpdate(String action) {
        sendBroadcast(new Intent(action));
    }

    private void broadcastUpdate(String action, String... data) {
        Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA, data);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(String action, BluetoothGattCharacteristic characteristic) {
        Intent intent = new Intent(action);

        byte[] data = characteristic.getValue();

        if (data != null && data.length > 0)
            intent.putExtra(EXTRA_DATA, data);

        sendBroadcast(intent);
    }

    // ============================================================
    // Inner and Anonymous Classes
    // ============================================================

    private final BluetoothGattCallback mBLEGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                broadcastUpdate(ACTION_BLE_CONNECTED);
                mBLEGatt.discoverServices();
            } else {
                broadcastUpdate(ACTION_BLE_DISCONNECTED);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS)
                broadcastUpdate(ACTION_BLE_SERVICES_DISCOVERED);

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS)
                broadcastUpdate(ACTION_BLE_DATA_AVAILABLE, characteristic);

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_BLE_DATA_AVAILABLE, characteristic);
        }
    };

    private final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            BluetoothDevice device = result.getDevice();
              if (device.getName() != null) {
                if (device.getName().equalsIgnoreCase("DuxCycler")) {
                    broadcastUpdate(ACTION_BLE_DEVICE_FOUND, device.getName(), device.getAddress());
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    public class LocalBinder extends Binder {
        public BLEService getService() {
            return BLEService.this;
        }
    }
}
