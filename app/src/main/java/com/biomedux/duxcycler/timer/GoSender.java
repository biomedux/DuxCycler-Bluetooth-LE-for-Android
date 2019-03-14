// ============================================================
// FileName		: GoSender.java
// Author		: JaeHong Min
// Date			: 2017.08.01
// ============================================================

package com.biomedux.duxcycler.timer;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

import com.biomedux.duxcycler.MainActivity;
import com.biomedux.duxcycler.beans.Action;
import com.biomedux.duxcycler.beans.TxProtocol;
import com.biomedux.duxcycler.bluetooth.BLEService;

public class GoSender {

    // ============================================================
    // Constants
    // ============================================================

    public  static final int TIMER_DURATION = 200;

    // ============================================================
    // Fields
    // ============================================================

    private Handler mSendHandler = new Handler();

    private BLEService mBLEService;
    private Handler mHandler;
    private ArrayList<Action> mActions;
    private String mPreheat;
    private int mIndex;

    private boolean taskEnded = false;
    private boolean gotoEnded = false;

    private BluetoothGattCharacteristic mTxCharacteristic;

    // ============================================================
    // Constructors
    // ============================================================

    public GoSender(BLEService bleService, Handler handler, ArrayList<Action> actions, String preheat, BluetoothGattCharacteristic txCharacteristic) {
        mBLEService = bleService;
        mHandler = handler;
        mActions = actions;
        mPreheat = preheat;
        mTxCharacteristic = txCharacteristic;
    }

    // ============================================================
    // Getter & Setter
    // ============================================================

    public int getmIndex() {
        return mIndex;
    }

    public void setmIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    public boolean isGotoEnded() {
        return gotoEnded;
    }

    public void setGotoEnded(boolean gotoEnded) {
        this.gotoEnded = gotoEnded;
    }

    public boolean isTaskEnded() {
        return taskEnded;
    }

    public void setTaskEnded(boolean taskEnded) {
        this.taskEnded = taskEnded;
    }

    // ============================================================
    // Methods for/from SuperClass/Interfaces
    // ============================================================

    // ============================================================
    // Methods
    // ============================================================

    public void start() {
        taskEnded = false;
        gotoEnded = false;
        mIndex = 0;

        mSendHandler.postDelayed(mSender, TIMER_DURATION);
    }

    public void resume() {
        mSendHandler.postDelayed(mSender, TIMER_DURATION);
    }

    public void stop() {
        mSendHandler.removeCallbacks(mSender);
    }

    private void send() {

        if (mIndex < mActions.size()) {

            Action action = mActions.get(mIndex);

            mBLEService.writeCharacteristic(mTxCharacteristic, TxProtocol.makeTaskWrite(action.getLabel(), action.getTemp(), action.getTime(), mPreheat, mIndex));

        } else {

            if (!taskEnded) {

                mBLEService.writeCharacteristic(mTxCharacteristic, TxProtocol.makeTaskEnd());

            } else if (!gotoEnded) {

                mBLEService.writeCharacteristic(mTxCharacteristic, TxProtocol.makeGo());

            } else {

                mHandler.obtainMessage(MainActivity.MESSAGE_TASK_END).sendToTarget();

            }
        }

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
