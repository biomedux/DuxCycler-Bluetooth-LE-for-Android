// ============================================================
// FileName		: DeviceData.java
// Author		: JaeHong Min
// Date			: 2017.08.02
// ============================================================

package com.biomedux.duxcycler.beans;

public class DeviceInfo {

    // ============================================================
    // Constants
    // ============================================================

    // ============================================================
    // Fields
    // ============================================================

    private String name;
    private String address;
    private long date;

    // ============================================================
    // Constructors
    // ============================================================

    public DeviceInfo() {
        this("", "", 0);
    }

    public DeviceInfo(String name, String address){
        this.name = name;
        this.address = address;
    }

    public DeviceInfo(String name, String address, long date) {
        this.name = name;
        this.address = address;
        this.date = date;
    }

    // ============================================================
    // Getter & Setter
    // ============================================================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    // ============================================================
    // Methods for/from SuperClass/Interfaces
    // ============================================================

    // ============================================================
    // Methods
    // ============================================================

    // ============================================================
    // Inner and Anonymous Classes
    // ============================================================

}