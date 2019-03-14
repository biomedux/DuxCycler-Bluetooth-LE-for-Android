// ============================================================
// FileName		: MainActivity.java
// Author		: JaeHong Min
// Date			: 2017.07.04
// ============================================================

package com.biomedux.duxcycler;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.biomedux.duxcycler.beans.Action;
import com.biomedux.duxcycler.beans.Protocol;
import com.biomedux.duxcycler.beans.RxBuffer;
import com.biomedux.duxcycler.beans.RxProtocol;
import com.biomedux.duxcycler.beans.State;
import com.biomedux.duxcycler.beans.StateOperation;
import com.biomedux.duxcycler.beans.TxProtocol;
import com.biomedux.duxcycler.bluetooth.BLEService;
import com.biomedux.duxcycler.dialog.ActionEdit;
import com.biomedux.duxcycler.dialog.DialogMessage;
import com.biomedux.duxcycler.dialog.PreheatEdit;
import com.biomedux.duxcycler.dialog.ProtocolTitleEdit;
import com.biomedux.duxcycler.R;
import com.biomedux.duxcycler.timer.GoSender;
import com.biomedux.duxcycler.timer.NopSender;
import com.biomedux.duxcycler.ui.ActionTable;
import com.biomedux.duxcycler.ui.ListAdapter;
import com.biomedux.duxcycler.ui.SlideMenu;
import com.biomedux.duxcycler.util.DataStorage;
import com.biomedux.duxcycler.util.Util;

public class MainActivity extends Activity implements DialogMessage {

    // ============================================================
    // Constants
    // ============================================================

    // Permission
    private static final String PERMISSION_LOCATION	= Manifest.permission.ACCESS_COARSE_LOCATION;

    // Message types sent from the Bluetooth Service Handler
    public static final int MESSAGE_STATE_CHANGE	= 2;
    public static final int MESSAGE_READ			= 3;
    public static final int MESSAGE_WRITE			= 4;
    public static final int MESSAGE_TOAST			= 5;
    public static final int MESSAGE_TASK_WRITE		= 6;
    public static final int MESSAGE_TASK_END		= 7;
    public static final int MESSAGE_COMM_END		= 8;

    // Bluetooth LE Request
    int REQUEST_ENABLE_BT								= 0;
    int REQUEST_CONNECT_DEVICE_BT					= 1;

    // Timer
    private static final int TIMER_NOP				= 0;
    private static final int TIMER_GO				= 1;

    // Debug
    private final boolean DEBUG	= true;
    private final String TAG		= MainActivity.class.getSimpleName();

    // ============================================================
    // Fields
    // ============================================================

    /* Android */

    private Setting setting;


    /* Main UI */

    private ImageButton imgMenuToggle;
    private TextView txtProtocolTitle;
    private TextView txtProtocolTime;

    private ImageView imgLED_R;
    private ImageView imgLED_G;
    private ImageView imgLED_B;

    private TextView txtChamber;
    private TextView txtLidHeater;
    private TextView txtPreheat;

    /* ActionGraph
    private ActionGraph actionGraph; */
    private ActionTable actionTable;

    private TextView txtWork;
    private LinearLayout lytWork;
    private TextView txtCancel;
    private TextView txtAdd;
    private TextView txtCommit;


    /* Menu UI */

    private LinearLayout lytShadow;
    private SlideMenu slideMenu;
    private RelativeLayout lytNewProtocol;
    private ListAdapter adpProtocolList;
    private ListView lstProtocol;


    /* Custom Dialog */

    private ActionEdit actionEdit;
    private ProtocolTitleEdit protocolTitleEdit;
    private PreheatEdit preheatEdit;
    private ProgressDialog progressDialog;


    /* Protocol */

    private ArrayList<Protocol> protocols;
    private int currentProtocolIndex;

    private Protocol tempProtocol;


    /* Timer */

    private NopSender mNopSender;
    private GoSender mGoSender;


    /* Bluetooth LE */

    private BLEService mBluetoothLeService;
    private BluetoothGattCharacteristic mRxCharacteristic;
    private BluetoothGattCharacteristic mTxCharacteristic;
    private BluetoothGattCharacteristic mInitCharacteristic;

    private BLE_STATE mCurrentBLEState = BLE_STATE.DISCONNECTED;

    private enum BLE_STATE {DISCONNECTED, SCANNING, FOUND, CONNECTING, CONNECTED}

    private IntentFilter mIntentFilter;

    private RxBuffer mRxBuffer; // #MHRYU-INS


    /* Variables & flags */

    private int LED_Counter = 0;
    private int List_Counter = 0;
    private int Timer_Counter = 0;
    private boolean IsRunning = false;
    private boolean IsReadyToRun = true;
    private boolean IsFinishPCR = false;
    private boolean IsRefrigeratorEnd = false;
    private boolean IsProtocolEnd = false;
    private boolean IsGotoStart = false;
    private boolean mPCRStop;

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
        setTheme(android.R.style.Theme_Holo_Light_NoActionBar_TranslucentDecor);
        setting = new Setting(this);
        setContentView(R.layout.activity_main);

        initBluetoothLE();
        initProtocol();
        initUI();
        mRxBuffer = new RxBuffer(); //#MHRYU-INS
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            setting.hideNavigation();
    }

    @Override
    protected void onResume() {
        log("onResume()");
        super.onResume();

        if (mCurrentBLEState == BLE_STATE.CONNECTED) {
            if (mNopSender != null)
                mNopSender.start();

            if (mGoSender != null)
                mGoSender.start();
        }

        registerReceiver(mGattUpdateReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        log("onPause()");
        super.onPause();

        if (mCurrentBLEState == BLE_STATE.CONNECTED) {
            if (mNopSender != null)
                mNopSender.stop();

            if (mGoSender != null)
                mGoSender.stop();
        }

        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        log("onDestroy()");
        super.onDestroy();
        unbindService(mBluetoothLeServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ENABLE_BT) {

                mBluetoothLeService.scanBLEDevice(true);
                mCurrentBLEState = BLE_STATE.SCANNING;

                Intent intent = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivityForResult(intent, REQUEST_CONNECT_DEVICE_BT);

            } else if (requestCode == REQUEST_CONNECT_DEVICE_BT) {
                if (mBluetoothLeService.connect(data.getStringExtra(DeviceListActivity.EXTRA_DATA))) {
                    mCurrentBLEState = BLE_STATE.CONNECTING;
                    mBluetoothLeService.scanBLEDevice(false);

                    onProgressDialog("Bluetooth Connecting..");
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // ============================================================
    // Methods
    // ============================================================

    private void initBluetoothLE() {
        Intent gattServiceIntent = new Intent(MainActivity.this, BLEService.class);
        bindService(gattServiceIntent, mBluetoothLeServiceConnection, BIND_AUTO_CREATE);

        if (mBluetoothLeService != null) {
            mCurrentBLEState = mBluetoothLeService.isBluetoothEnabled()
                    ? BLE_STATE.SCANNING : BLE_STATE.DISCONNECTED;
        }

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BLEService.ACTION_BLE_CONNECTED);
        mIntentFilter.addAction(BLEService.ACTION_BLE_DISCONNECTED);
        mIntentFilter.addAction(BLEService.ACTION_BLE_SERVICES_DISCOVERED);
        mIntentFilter.addAction(BLEService.ACTION_BLE_DEVICE_FOUND);
        mIntentFilter.addAction(BLEService.ACTION_BLE_DATA_AVAILABLE);
    }

    private void initProtocol() {
        protocols = new ArrayList<>();
        DataStorage.getInstance().initial(this, "Protocol");
        currentProtocolIndex = DataStorage.getInstance().load(protocols);

        if (protocols.size() == 0) {
            ArrayList<Action> actions = new ArrayList<>();
            actions.add(new Action("1", "95", "180"));
            actions.add(new Action("2", "95", "10"));
            actions.add(new Action("3", "60", "30"));
            actions.add(new Action("4", "72", "30"));
            actions.add(new Action("GOTO", "2", "34"));
            actions.add(new Action("5", "95", "10"));
            actions.add(new Action("6", "50", "30"));
            protocols.add(new Protocol("Default", actions));
            currentProtocolIndex = 0;
        }
    }

    private void initUI() {
        imgMenuToggle = (ImageButton) findViewById(R.id.MainFragment_ImageView_MenuToggle);
        imgMenuToggle.setOnClickListener(OnClickListener);

        txtProtocolTitle = (TextView) findViewById(R.id.MainFragment_TextView_ProtocolTitle);
        txtProtocolTitle.setOnClickListener(OnClickListener);

        txtProtocolTime = (TextView) findViewById(R.id.MainFragment_TextView_ProtocolTime);

        txtChamber = (TextView) findViewById(R.id.MainFragment_TextView_Chamber);

        txtLidHeater = (TextView) findViewById(R.id.MainFragment_TextView_LidHeater);

        txtPreheat = (TextView) findViewById(R.id.MainFragment_TextView_Preheat);
        txtPreheat.setOnClickListener(OnClickListener);

        imgLED_R = (ImageView) findViewById(R.id.MainFragment_ImageView_LED_Red);
        imgLED_G = (ImageView) findViewById(R.id.MainFragment_ImageView_LED_Green);
        imgLED_B = (ImageView) findViewById(R.id.MainFragment_ImageView_LED_Blue);

        /* ActionGraph
        actionGraph = (ActionGraph) findViewById(R.id.MainFragment_ActionGraph); */

        actionTable = (ActionTable) findViewById(R.id.MainFragment_ActionTable);
        actionTable.initial(this);
        actionTable.setOnClickListener(OnClickListenerAction);

        txtWork = (TextView) findViewById(R.id.MainFragment_TextView_Work);
        txtWork.setOnClickListener(OnClickListener);

        lytWork = (LinearLayout) findViewById(R.id.MainFragment_LinearLayout_Work);
        lytWork.setVisibility(View.INVISIBLE);

        txtCancel = (TextView) findViewById(R.id.MainFragment_TextView_Cancel);
        txtCancel.setOnClickListener(OnClickListener);

        txtAdd = (TextView) findViewById(R.id.MainFragment_TextView_Add);
        txtAdd.setOnClickListener(OnClickListener);

        txtCommit = (TextView) findViewById(R.id.MainFragment_TextView_Commit);
        txtCommit.setOnClickListener(OnClickListener);

        lytShadow = (LinearLayout) findViewById(R.id.MainActivity_LinearLayout_MenuShadow);
        lytShadow.setOnClickListener(OnClickListener);

        slideMenu = new SlideMenu(this, R.id.MainActivity_LinearLayout_MenuFragment, R.id.MainActivity_LinearLayout_MenuShadow, 0.8f);

        lytNewProtocol = (RelativeLayout) findViewById(R.id.MenuFragment_RelativeLayout_NewProtocol);
        lytNewProtocol.setOnClickListener(OnClickListener);

        adpProtocolList = new ListAdapter(this, android.R.layout.simple_list_item_1, protocols);

        lstProtocol = (ListView) findViewById(R.id.MenuFragment_ListView_Protocol);
        lstProtocol.setAdapter(adpProtocolList);
        lstProtocol.setOnItemClickListener(OnItemClickListener);
        lstProtocol.setOnItemLongClickListener(OnItemLongClickListener);

        actionEdit = ActionEdit.getInstance();
        actionEdit.initial(this, actionTable, DialogCallback);

        protocolTitleEdit = ProtocolTitleEdit.getInstance();
        protocolTitleEdit.initial(this, protocols, DialogCallback);

        preheatEdit = PreheatEdit.getInstance();
        preheatEdit.initial(this, DialogCallback);

        progressDialog = new ProgressDialog(this);

        update(currentProtocolIndex);
    }

    private void update(int index) {
        Protocol protocol = new Protocol("(none)", new ArrayList<Action>());
        int time;

        if (index > -1 && index < protocols.size())
            protocol = protocols.get(index);

        time = Util.calcProtocolTime(protocol.getActions());
        txtProtocolTitle.setText(protocol.getTitle());
        txtProtocolTime.setText(String.format("%02d:%02d:%02d", time / 3600, time / 60 % 60, time % 60));
        actionTable.update(protocol.getActions());

        /* ActionGraph
        actionGraph.update(protocol.getActions(), setting.getDeviceWidth(), setting.getDeviceHeight() / 3, 180, 120); */

        DataStorage.getInstance().save(protocols, index);
    }

    private void setTimer(int timer) {
        switch(timer) {
            case TIMER_NOP:

                if (mGoSender != null) {
                    mGoSender.stop();
                    mGoSender = null;
                }
                mNopSender = new NopSender(mBluetoothLeService, mTxCharacteristic);
                mNopSender.start();
                break;
            case TIMER_GO:

                if (mNopSender != null) {
                    mNopSender.stop();
                    mNopSender = null;
                }

                mGoSender = new GoSender(mBluetoothLeService, mBluetoothTaskHandler
                        , protocols.get(currentProtocolIndex).getActions()
                        , txtPreheat.getText().toString(), mTxCharacteristic);
                mGoSender.start();
                break;
        }
    }

    private void start() {
        setTimer(TIMER_GO);

        progressDialog.dismiss();
        onProgressDialog("Protocol is sending to DuxCycler.");

        txtWork.setText("Stop");
    }

    private void stop() {
        onProgressDialog("DuxCycler is stopping work.");

        if (mNopSender != null)
            mNopSender.stop();

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mBluetoothLeService.writeCharacteristic(mTxCharacteristic, TxProtocol.makeStop());

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        progressDialog.dismiss();

        if (IsRunning) {
            if (!IsProtocolEnd) {
                controlLED(true, false, false);
                IsFinishPCR = false;
            } else if (IsRefrigeratorEnd) {
                controlLED(true, false, false);
                IsFinishPCR = true;
                setTimer(TIMER_NOP);
                return;
            }

            end();
        }

        setTimer(TIMER_NOP);
    }

    public void end() {
        IsRunning = false;

        txtWork.setText("Start");

        actionTable.selection(-1);

        if (IsFinishPCR) {
            for(int i=0; i<actionTable.getLength(); i++)
                actionTable.setItem(i, ActionTable.REMAIN, "");
            Toast.makeText(this, "PCR Ended!!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "PCR Incomplete!!", Toast.LENGTH_LONG).show();
        }
    }

    private void log(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }

    private void onProgressDialog(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage(msg);
                progressDialog.show();
            }
        });
    }

    public void onRXProcess(RxProtocol rxProtocol) {
        checkTemp(rxProtocol);

        checkStatus(rxProtocol.getState(), rxProtocol.getCurrentOperation());

        lineTask(rxProtocol);

        calcTime(rxProtocol);
    }

    private void checkTemp(RxProtocol rxProtocol) {
        txtChamber.setText(String.format("%4.1fºC", rxProtocol.getChamberTemp()));
        txtLidHeater.setText(String.format("%4.1fºC", rxProtocol.getLidTemp()));
    }

    private void checkStatus(int state, int operation) {
        switch (state) {
            case State.READY:
                switch (operation) {
                    case StateOperation.INIT:
                        controlLED(false, true, false);
                        break;

                    case StateOperation.COMPLETE:
                        controlLED(false, true, true);
                        if (!IsReadyToRun) {
                            IsFinishPCR = true;
                            IsReadyToRun = true;
                            end();
                        }
                        break;

                    case StateOperation.INCOMPLETE:
                        controlLED(true, true, false);
                        break;
                }
                break;

            case State.RUN:
                if (operation == StateOperation.RUN_REFRIGERATOR) {
                    controlLED(false, true, true);
                    IsRefrigeratorEnd = true;
                    IsProtocolEnd = true;
                    IsFinishPCR = true;
                } else {
                    if (LED_Counter > 8) {
                        controlLED(false, true, true);
                    } else if (LED_Counter == 0) {
                        controlLED(false, true, false);
                    }
                }
                LED_Counter++;

                if (LED_Counter == 14)
                    LED_Counter = 0;
                IsReadyToRun = false;
                IsRunning = true;

                txtWork.setText("Stop");

                break;

            case State.PCREND:
                controlLED(false, true, true);
                break;
        }
    }

    private void lineTask(RxProtocol rxProtocol) {
        int index = 0;

        if (List_Counter > 4) {

            // 현재 진행중인 액션의 라벨을 ActionTable 에서 찾아서 index를 구한다.
            for (int i = 0; i < actionTable.getLength(); i++) {
                String label = actionTable.getItem(i, ActionTable.LABEL);
                if (!label.equals("GOTO")) {
                    if (Integer.parseInt(label) == rxProtocol.getCurrentLabel()) {
                        index = i;
                        break;
                    }
                }
            }

            if (IsRunning) {
                String tempString = Util.toHMS((int) rxProtocol.getLineTime());

                for (int i = 0; i < rxProtocol.getLabelCount(); i++) {
                    if (!tempString.equals("GOTO"))
                        actionTable.setItem(i, ActionTable.REMAIN, "");
                }

                actionTable.setItem(index, ActionTable.REMAIN, tempString.equals("0s") ? "" : tempString);

                if (rxProtocol.getGotoCount() != 0) {
                    if (rxProtocol.getGotoCount() == 255)
                        IsGotoStart = true;
                }

                if (rxProtocol.getGotoCount() != 255) {
                    if (IsGotoStart) {
                        boolean flag = true;
                        for (int i = index; i < rxProtocol.getLabelCount(); i++) {
                            tempString = actionTable.getItem(i, ActionTable.LABEL);
                            if (tempString.equals("GOTO")) {
                                if (flag) {
                                    flag = false;
                                    tempString = rxProtocol.getGotoCount() + "";
                                    actionTable.setItem(i, actionTable.REMAIN, tempString);
                                }
                            }
                        }
                    }
                }

                actionTable.selection(index);
            }

            List_Counter = 0;
        } else {
            List_Counter++;
        }
    }

    private void calcTime(RxProtocol rxProtocol) {
        int time = (int) rxProtocol.getTotalTime();

        if (IsRunning)
            txtProtocolTime.setText(String.format("%02d:%02d:%02d", time / 3600, time / 60 % 60, time % 60));
    }

    private void controlLED(boolean r, boolean g, boolean b) {
        imgLED_R.setImageResource(r ? R.drawable.led_red : R.drawable.led_gray);
        imgLED_G.setImageResource(g ? R.drawable.led_green : R.drawable.led_gray);
        imgLED_B.setImageResource(b ? R.drawable.led_blue : R.drawable.led_gray);
    }

    // ============================================================
    // Inner and Anonymous Classes
    // ============================================================

    private final ServiceConnection mBluetoothLeServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (MainActivity.this.checkSelfPermission(PERMISSION_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    MainActivity.this.requestPermissions(new String[]{PERMISSION_LOCATION}, 0);
                }
            }

            mBluetoothLeService = ((BLEService.LocalBinder) service).getService();

            if (!mBluetoothLeService.initialize()) {
                Toast.makeText(MainActivity.this, "Failed to initialize bluetooth", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case BLEService.ACTION_BLE_CONNECTED:
                    mCurrentBLEState = BLE_STATE.CONNECTED;
                    mRxBuffer.clear(); // #MHRYU-INS
                    break;

                case BLEService.ACTION_BLE_DISCONNECTED:
                    mCurrentBLEState = BLE_STATE.SCANNING;

                    if (mRxCharacteristic != null)
                        mBluetoothLeService.setCharacteristicNotification(mRxCharacteristic, false);
                    mBluetoothLeService.scanBLEDevice(true);

                    mRxCharacteristic = null;
                    mTxCharacteristic = null;
                    mInitCharacteristic = null;

                    txtWork.setText("Start");
                    break;

                case BLEService.ACTION_BLE_SERVICES_DISCOVERED:
                    mRxCharacteristic = mBluetoothLeService.getFirmtechRxCharacteristic();
                    mTxCharacteristic = mBluetoothLeService.getFirmtechTxCharacteristic();

                    if ((mRxCharacteristic != null) && (mTxCharacteristic != null)) {
                        mBluetoothLeService.setCharacteristicNotification(mRxCharacteristic, true);
                        start();
                    }
                    else{
                        // not complete, needs fix.
                        Toast.makeText(getApplicationContext(), "Connect fail", Toast.LENGTH_LONG).show();
                        mCurrentBLEState = BLE_STATE.SCANNING;
                        mBluetoothLeService.scanBLEDevice(true);
                        mRxCharacteristic = null;
                        mTxCharacteristic = null;
                    }
                    // #MHRYU-INS-END
                    break;

                case BLEService.ACTION_BLE_DATA_AVAILABLE:
                    // #MHRYU-DEL-START
                    //byte[] data = intent.getByteArrayExtra(BLEService.EXTRA_DATA);
                    // #MHRYU-DEL-END
                    // #MHRYU-INS-START

                    byte[] raw_data = intent.getByteArrayExtra(BLEService.EXTRA_DATA);

                    String log = "";
                    for (int i = 0; i < raw_data.length; i++)
                        log += String.format("0x%2X ", raw_data[i]);

                    Log.d("asdasd", "len: " + raw_data.length + " | " + log);

                    byte[] buffered_data = mRxBuffer.buffering(raw_data);
                    if( buffered_data.length == 0) // still buffering
                        break;
                    byte[] data = new byte[20]; // cut last 21th byte
                    for( int i=0; i<20; i++)
                        data[i] = buffered_data[i];
                    // #MHRYU-INS-START
                    if (data[0] == -1) {
                        log("Read config received");
                    } else {
                        if (data.length == 20) {
                            RxProtocol rxData = new RxProtocol(data);

                            if (rxData.isValidPacket()) {
                                onRXProcess(rxData);

                                if (mNopSender != null) {
                                    if (mPCRStop) {

                                        if (rxData.getState() == State.READY && (rxData.getCurrentOperation() == StateOperation.INCOMPLETE || rxData.getCurrentOperation() == StateOperation.COMPLETE)) {
                                            mPCRStop = false;
                                            progressDialog.dismiss();
                                        } else {
                                            stop();
                                        }

                                    }
                                }

                                if (mGoSender != null) {
                                    ArrayList<Action> actions = protocols.get(currentProtocolIndex).getActions();

                                    if (rxData.getState() == State.RUN) {

                                        mGoSender.setGotoEnded(true);

                                        progressDialog.dismiss();

                                        Log.d(TAG, "GO SENDER - GOTO ENDED");

                                    } else if (rxData.getLabelCount() == actions.size()) {

                                        mGoSender.setTaskEnded(true);

                                        Log.d(TAG, "GO SENDER - TASK ENDED");

                                    } else if (rxData.getState() == State.TASK_WRITE) {
                                        int label = Integer.parseInt(actions.get(mGoSender.getmIndex()).getLabel().equals("GOTO") ? "250" : actions.get(mGoSender.getmIndex()).getLabel());
                                        int temp = Integer.parseInt(actions.get(mGoSender.getmIndex()).getTemp());
                                        int time = Integer.parseInt(actions.get(mGoSender.getmIndex()).getTime());

                                        if (rxData.getRequestLabel() == label && rxData.getRequestTemp() == temp && rxData.getRequestTime() == time && mGoSender.getmIndex() == rxData.getReqLine()) {
                                            mGoSender.setmIndex(mGoSender.getmIndex() + 1);
                                        }
                                    }
                                }
                            }
                        }

                        // Log.d(TAG, String.format("send:%d,ok:%d,miss:%d,corrupt:%d", mSendCount, mReceiveOkCount, mReceiveMissCount, mReceiveCorruptCount));
                    }

                    break;

            }
        }
    };

    private final Handler mBluetoothTaskHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE_TASK_END:
                    setTimer(TIMER_NOP);
                    progressDialog.dismiss();
                    break;
            }
        }
    };

    private final Handler DialogCallback = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case DIALOG_ACTION_EDIT:
                    update(currentProtocolIndex);
                    break;

                case DIALOG_PROTOCOL_TITLE_EDIT:
                    if (msg.arg2 == 1) {
                        protocols.add(new Protocol((String) msg.obj, new ArrayList<Action>()));
                        currentProtocolIndex = protocols.size() - 1;

                        txtWork.setVisibility(View.INVISIBLE);
                        lytWork.setVisibility(View.VISIBLE);
                        slideMenu.menuToggle();
                    } else {
                        protocols.get(msg.arg1).setTitle((String) msg.obj);
                        if (msg.arg1 == currentProtocolIndex)
                            txtProtocolTitle.setText(protocols.get(currentProtocolIndex).getTitle());
                    }

                    update(currentProtocolIndex);

                    adpProtocolList.notifyDataSetChanged();

                    break;

                case DIALOG_PREHEAT_EDIT:
                    txtPreheat.setText((String) msg.obj);
                    break;
            }
        }
    };

    private final Handler BluetoothCallback = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            /*
            switch (msg.what) {

                case MESSAGE_STATE_CHANGE:
                    onBluetoothStateChange(msg.arg1);
                    break;

                case MESSAGE_READ:
                    onRxProcess(msg.obj);
                    break;

                case MESSAGE_WRITE:
                    // Write Log Handler
                    break;

                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), (String)msg.obj, Toast.LENGTH_LONG).show();
                    break;

                case MESSAGE_TASK_WRITE:
                    break;

                case MESSAGE_TASK_END:
                    try {
                        Thread.sleep(300);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                    setTimer(TIMER_NOP);
                    progressDialog.dismiss();
            }
            */
        }
    };

    private final View.OnClickListener OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.MainFragment_TextView_ProtocolTitle:
                    if (currentProtocolIndex != -1)
                        protocolTitleEdit.showEditDialog(currentProtocolIndex, false);
                    break;

                case R.id.MainFragment_ImageView_MenuToggle:
                    slideMenu.menuToggle();
                    break;

                case R.id.MainFragment_TextView_Preheat:
                    preheatEdit.showEditDialog(txtPreheat.getText().toString());
                    break;

                case R.id.MainFragment_TextView_Work:
                    if (txtWork.getText().equals("Start")) {
                        if (mBluetoothLeService != null) {

                            if (mBluetoothLeService.isBluetoothEnabled()) {
                                if (mCurrentBLEState == BLE_STATE.CONNECTED) {
                                    start();
                                } else {
                                    mBluetoothLeService.scanBLEDevice(true);
                                    mCurrentBLEState = BLE_STATE.SCANNING;

                                    Intent intent = new Intent(MainActivity.this, DeviceListActivity.class);
                                    startActivityForResult(intent, REQUEST_CONNECT_DEVICE_BT);
                                }
                            } else {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            }
                        }
                    } else {
                        stop();
                    }

                    break;

                case R.id.MainFragment_TextView_Cancel:
                    txtWork.setVisibility(View.VISIBLE);
                    lytWork.setVisibility(View.INVISIBLE);

                    protocols.set(currentProtocolIndex, tempProtocol);
                    update(currentProtocolIndex);
                    break;

                case R.id.MainFragment_TextView_Add:
                    actionEdit.showAddDialog(protocols.get(currentProtocolIndex).getActions());
                    break;

                case R.id.MainFragment_TextView_Commit:
                    txtWork.setVisibility(View.VISIBLE);
                    lytWork.setVisibility(View.INVISIBLE);
                    break;

                case R.id.MainActivity_LinearLayout_MenuShadow:
                    if (slideMenu.isExpanded())
                        slideMenu.menuToggle();
                    break;

                case R.id.MenuFragment_RelativeLayout_NewProtocol:
                    ProtocolTitleEdit.getInstance().showEditDialog(protocols.size(), true);
                    break;
            }
        }
    };

    private final View.OnClickListener OnClickListenerAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!IsRunning) {
                if (currentProtocolIndex != -1 && lytWork.getVisibility() == View.VISIBLE) {
                    for (int i = 0; i < protocols.get(currentProtocolIndex).getActions().size(); i++) {
                        if (v.getId() == i) {
                            actionEdit.showEditDialog(protocols.get(currentProtocolIndex).getActions(), i);
                            break;
                        }
                    }
                }
            }
        }
    };

    private final AdapterView.OnItemClickListener OnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (IsRunning) {
                Toast.makeText(getApplicationContext(), "DuxCycler is running.", Toast.LENGTH_LONG).show();
            } else {
                txtWork.setVisibility(View.VISIBLE);
                lytWork.setVisibility(View.INVISIBLE);
                update(currentProtocolIndex = position);
            }

            slideMenu.menuToggle();
        }
    };

    private final AdapterView.OnItemLongClickListener OnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            AlertDialog dialog;

            builder.setTitle(protocols.get(position).getTitle());
            builder.setItems( new String[]{"Protocol Title Edit", "Protocol Edit", "Protocol Delete"} , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            protocolTitleEdit.showEditDialog(position, false);
                            break;
                        case 1:
                            if (IsRunning) {
                                Toast.makeText(getApplicationContext(), "DuxCycler is running.", Toast.LENGTH_LONG).show();
                                break;
                            }
                            txtWork.setVisibility(View.INVISIBLE);
                            lytWork.setVisibility(View.VISIBLE);
                            update(currentProtocolIndex = position);

                            tempProtocol = protocols.get(currentProtocolIndex).clone();

                            slideMenu.menuToggle();
                            break;
                        case 2:
                            if (currentProtocolIndex == position) {
                                if (IsRunning) {
                                    Toast.makeText(getApplicationContext(), "DuxCycler is running.", Toast.LENGTH_LONG).show();
                                    break;
                                }

                                currentProtocolIndex = -1;
                            }
                            protocols.remove(position);
                            update(currentProtocolIndex);
                            adpProtocolList.notifyDataSetChanged();
                            break;
                    }
                }
            });
            builder.setNegativeButton("Cancel", null);

            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            return true;
        }
    };
}
