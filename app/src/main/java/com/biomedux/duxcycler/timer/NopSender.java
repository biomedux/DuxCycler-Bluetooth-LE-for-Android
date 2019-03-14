// ============================================================
// FileName		: NopSender.java
// Author		: JaeHong Min
// Date			: 2017.08.01
// ============================================================

package com.biomedux.duxcycler.timer;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Handler;
import android.util.Log;

import com.biomedux.duxcycler.beans.TxProtocol;
import com.biomedux.duxcycler.bluetooth.BLEService;

public class NopSender {

    // ============================================================
    // Constants
    // ============================================================

    public static final int TIMER_DURATION = 100;

    // ============================================================
    // Fields
    // ============================================================

    private Handler mSendHandler = new Handler();

    private BLEService mBLEService;
    private BluetoothGattCharacteristic mTxCharacteristic;

    // ============================================================
    // Constructors
    // ============================================================

    public NopSender(BLEService bleService, BluetoothGattCharacteristic txCharacteristic) {
        mBLEService = bleService;
        mTxCharacteristic = txCharacteristic;
    }

    // ============================================================
    // Getter & Setter
    // ============================================================

    // ============================================================
    // Methods for/from SuperClass/Interfaces
    // ============================================================

    // ============================================================
    // Methods
    // ============================================================

    public void start() {
        mSendHandler.postDelayed(mSender, TIMER_DURATION);
    }

    public void stop() {
        mSendHandler.removeCallbacks(mSender);
    }

    private void send() {
        if (mBLEService != null && mTxCharacteristic != null)
            mBLEService.writeCharacteristic(mTxCharacteristic, TxProtocol.makeNop());
        mSendHandler.postDelayed(mSender, TIMER_DURATION);
    }

    // ============================================================
    // Inner and Anonymous Classes
    // ============================================================

    private final Runnable mSender = new Runnable() {
        @Override
        public void run() {
            send();
        }
    };
}
