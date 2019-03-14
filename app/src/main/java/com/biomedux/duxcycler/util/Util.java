// ============================================================
// FileName		: Util.java
// Author		: JaeHong Min
// Date			: 2017.07.04
// ============================================================

package com.biomedux.duxcycler.util;

import java.util.ArrayList;

import com.biomedux.duxcycler.beans.Action;

public class Util {

    // ============================================================
    // Constants
    // ============================================================

    protected final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    // ============================================================
    // Fields
    // ============================================================

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

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3];

        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }

        return new String(hexChars);
    }

    public static int checksum(byte[] data) {
        int sum = 0;

        for (int i=0; i<data.length; ++i) {
            sum += data[i] & 0xff;
        }

        return (~sum & 0xff);
    }

    public static int calcProtocolTimeLite(ArrayList<Action> actions) {
        int time = 0;

        for (int i = 0; i < actions.size(); i++) {
            if (!actions.get(i).getLabel().equals("GOTO"))
                time += Integer.parseInt(actions.get(i).getTime());
        }

        return time;
    }

    public static int calcProtocolTime(ArrayList<Action> actions) {
        int time = 0;

        for (int i = 0; i < actions.size(); i++) {
            if (actions.get(i).getLabel().equals("GOTO")) {
                int sum = 0;
                int pointingIndex = 0;

                for (int j = 0; j < actions.size(); j++) {
                    if (actions.get(j).getLabel().equals(actions.get(i).getTemp())) {
                        pointingIndex = j;
                        break;
                    }
                }

                for (int j = pointingIndex; j < i; j++)
                    sum += Integer.parseInt(actions.get(j).getTime());
                time += sum * Integer.parseInt(actions.get(i).getTime());
            } else {
                time += Integer.parseInt(actions.get(i).getTime());
            }
        }

        return time;
    }

    public static String toHMS(int time) {
        int hour = time / 3600;
        int minute = time / 60 % 60;
        int second = time % 60;

        String hms = "";
        if (hour != 0)
            hms += hour + "h";
        if (minute != 0)
            hms += minute + "m";
        if (second != 0)
            hms += second + "s";

        return hms.equals("") ? "0s" : hms;
    }

    // ============================================================
    // Inner and Anonymous Classes
    // ============================================================

}
