// ============================================================
// FileName		: RxProtocol.java
// Author		: JaeHong Min
// Date			: 2017.07.04
// ============================================================
//
// Rx Format for android
//
// 0	: state of device
// 1	: current label
// 2	: current goto count
// 3	: total label count
// 4	: line left time high value
// 5	: line left time low value
// 6	: total left time high value
// 7	: total left time low value
// 8	: Lid temp high value
// 9	: Lid temp low value
// 10	: chamber temp high value
// 11	: chamber temp low value
// 12	: heatsink temp high value
// 13	: heatsink temp low value
// 14	: current operation value
// 15	: error value
// 16	: serial_high1
// 17	: serial_high2
// 18	: serial_low
// 19	: checksum
//
// ============================================================

package com.biomedux.duxcycler.beans;

import com.biomedux.duxcycler.util.Util;

public class RxProtocol {

    // ============================================================
    // Constants
    // ============================================================

    public static final int RX_BUFSIZE			= 20;

    private static final int RX_STATE			= 0;
    private static final int RX_CURRENT_LABEL	= 1;
    private static final int RX_GOTO_COUNT		= 2;
    private static final int RX_LABEL_COUNT		= 3;
    private static final int RX_LINE_TIME_H		= 4;
    private static final int RX_LINE_TIME_L		= 5;
    private static final int RX_TOTAL_TIME_H	= 6;
    private static final int RX_TOTAL_TIME_L	= 7;
    private static final int RX_LID_TEMP_H		= 8;
    private static final int RX_LID_TEMP_L		= 9;
    private static final int RX_CHAMBER_TEMP_H	= 10;
    private static final int RX_CHAMBER_TEMP_L	= 11;
    private static final int RX_HEAT_TEMP		= 12;
    private static final int RX_CURRENT_OPER	= 13;
    private static final int RX_ERROR_REQLINE	= 14;
    private static final int RX_REQ_LABEL		= 15;
    private static final int RX_REQ_TEMP		= 16;
    private static final int RX_REQ_TIME_H		= 17;
    private static final int RX_REQ_TIME_L		= 18;
    private static final int RX_CHECKSUM		= 19;

    // ============================================================
    // Fields
    // ============================================================

    private int state;
    private int currentLabel;
    private int gotoCount;
    private int labelCount;
    private int lineTime;
    private int totalTime;
    private double lidTemp;
    private double chamberTemp;
    private int heatTemp;
    private int currentOperation;
    private int error;
    private int requestLine;
    private int requestLabel;
    private int requestTemp;
    private int requestTime;

    private boolean validPacket;

    // ============================================================
    // Getter & Setter
    // ============================================================

    public int getState() {
        return state;
    }

    public int getCurrentLabel() {
        return currentLabel;
    }

    public int getGotoCount() {
        return gotoCount;
    }

    public int getLabelCount() {
        return labelCount;
    }

    public double getLineTime() {
        return lineTime;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public double getLidTemp() {
        return lidTemp;
    }

    public double getChamberTemp() {
        return chamberTemp;
    }

    public double getHeatTemp() {
        return heatTemp;
    }

    public int getCurrentOperation() {
        return currentOperation;
    }

    public int getError() {
        return error;
    }

    public int getReqLine(){
        return requestLine;
    }

    public int getRequestLabel() {
        return requestLabel;
    }

    public int getRequestTemp() {
        return requestTemp;
    }

    public int getRequestTime() {
        return requestTime;
    }

    public boolean isValidPacket(){
        return validPacket;
    }

    // ============================================================
    // Methods for/from SuperClass/Interfaces
    // ============================================================

    // ============================================================
    // Methods
    // ============================================================

    public RxProtocol(byte[] buffer){
        state				= buffer[RX_STATE]&0xff;
        currentLabel		= buffer[RX_CURRENT_LABEL]&0xff;
        gotoCount			= buffer[RX_GOTO_COUNT]&0xff;
        labelCount			= buffer[RX_LABEL_COUNT]&0xff;
        lineTime			= ((buffer[RX_LINE_TIME_H]&0xff) * 256) + (buffer[RX_LINE_TIME_L]&0xff);
        totalTime			= ((buffer[RX_TOTAL_TIME_H]&0xff) * 256) + (buffer[RX_TOTAL_TIME_L]&0xff);
        lidTemp				= (double)(buffer[RX_LID_TEMP_H]&0xff) + ((buffer[RX_LID_TEMP_L]&0xff) * 0.1);
        chamberTemp		= (double)(buffer[RX_CHAMBER_TEMP_H]&0xff) + ((buffer[RX_CHAMBER_TEMP_L]&0xff) * 0.1);
        heatTemp			= (buffer[RX_HEAT_TEMP]&0xff);
        currentOperation	= buffer[RX_CURRENT_OPER]&0xff;
        error				= buffer[RX_ERROR_REQLINE]&0xf0;
        requestLine		= buffer[RX_ERROR_REQLINE]&0x0f;
        requestLabel		= buffer[RX_REQ_LABEL]&0xff;
        requestTemp		= buffer[RX_REQ_TEMP]&0xff;
        requestTime		= ((buffer[RX_REQ_TIME_H]&0xff) * 256) + (buffer[RX_REQ_TIME_L]&0xff);
        validPacket		= Util.checksum(buffer) == 0;
    }

    // ============================================================
    // Inner and Anonymous Classes
    // ============================================================

}
