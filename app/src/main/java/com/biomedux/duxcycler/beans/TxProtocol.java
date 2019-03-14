// ============================================================
// FileName		: RxProtocol.java
// Author		: JaeHong Min
// Date			: 2017.07.04
// ============================================================
//
// Tx Format for android
//
// 0	: Command
// 1	: Label if the command is CMD_TASK_WRITE, default 0
// 2	: Temp(Temperature) if the command is CMD_TASK_WRITE 0~104, default 0
// 3	: Time_high if the command is CMD_TASK_WRITE 0~255, default 0
//		  The time value of first byte
// 4	: Time_low if the command is CMD_TASK_WRITE 0~255, default 0
//		  The time value of second byte
// 5	: LID Temp if the command is CMD_TASK_WRITE 0~255, default 0
// 6	: request line if the command is CMD_NOP, the value is label number
//		  for needs to information of the saved protocol
// 19	: checksum value if this value corrupted, don't use this packets
//
// ============================================================

package com.biomedux.duxcycler.beans;

import com.biomedux.duxcycler.util.Util;

public class TxProtocol {

    // ============================================================
    // Constants
    // ============================================================

    private static final int TX_CMD			= 0;
    private static final int TX_LABEL		= 1;
    private static final int TX_TEMP			= 2;
    private static final int TX_TIME_H		= 3;
    private static final int TX_TIME_L		= 4;
    private static final int TX_LID_TEMP	= 5;
    private static final int TX_REQ_LINE	= 6;
    private static final int TX_INDEX		= 7;
    private static final int TX_CHECKSUM	= 8;

    public static final int TX_BUFSIZE		= 20;

    private static final int AF_GOTO			= 250;

    // ============================================================
    // Fields
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

    public static byte[] makeNop(){
        byte[] buffer = new byte[TX_BUFSIZE];
        buffer[TX_CMD] = Command.NOP;
        buffer[TX_CHECKSUM] = (byte) Util.checksum(buffer);
        return buffer;
    }

    public static byte[] makeTaskWrite(String label, String temp, String time, String preheat, int index){
        byte[] buffer = new byte[TX_BUFSIZE];

        int nLabel, nTemp, nTime, nPreheat;
        if( label.equals("GOTO") )
            nLabel = AF_GOTO;
        else
            nLabel = Integer.parseInt(label);
        nTemp = Integer.parseInt(temp);
        nTime = Integer.parseInt(time);
        nPreheat = Integer.parseInt(preheat);

        buffer[TX_CMD] = Command.TASK_WRITE;
        buffer[TX_LABEL] = (byte)nLabel;
        buffer[TX_TEMP] = (byte)nTemp;
        buffer[TX_TIME_H] = (byte)(nTime/256.0);
        buffer[TX_TIME_L] = (byte)nTime;
        buffer[TX_LID_TEMP] = (byte)nPreheat;
        buffer[TX_REQ_LINE] = (byte)index;
        buffer[TX_INDEX] = (byte)index;
        buffer[TX_CHECKSUM] = (byte) Util.checksum(buffer);

        return buffer;
    }

    public static byte[] makeTaskEnd(){
        byte[] buffer = new byte[TX_BUFSIZE];
        buffer[TX_CMD] = Command.TASK_END;
        buffer[TX_CHECKSUM] = (byte) Util.checksum(buffer);
        return buffer;
    }

    public static byte[] makeGo(){
        byte[] buffer = new byte[TX_BUFSIZE];
        buffer[TX_CMD] = Command.GO;
        buffer[TX_CHECKSUM] = (byte) Util.checksum(buffer);
        return buffer;
    }

    public static byte[] makeStop(){
        byte[] buffer = new byte[TX_BUFSIZE];
        buffer[TX_CMD] = Command.STOP;
        buffer[TX_CHECKSUM] = (byte) Util.checksum(buffer);
        return buffer;
    }

    public static byte[] makeBootloader(){
        byte[] buffer = new byte[TX_BUFSIZE];
        buffer[TX_CMD] = Command.BOOTLOADER;
        buffer[TX_CHECKSUM] = (byte) Util.checksum(buffer);
        return buffer;
    }

    public static byte[] makeRequestLine(byte reqestLine){
        byte[] buffer = new byte[TX_BUFSIZE];
        buffer[TX_CMD] = Command.NOP;
        buffer[TX_REQ_LINE] = reqestLine;
        buffer[TX_CHECKSUM] = (byte) Util.checksum(buffer);
        return buffer;
    }

    // ============================================================
    // Inner and Anonymous Classes
    // ============================================================

}
