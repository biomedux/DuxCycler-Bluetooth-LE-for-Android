// ============================================================
// FileName		: BLEUUID.java
// Author		: JaeHong Min
// Date			: 2017.08.01
// ============================================================

package com.biomedux.duxcycler.bluetooth;

import java.util.UUID;

public interface BLEUUID {

    // ============================================================
    // Constants
    // ============================================================

    // UUID for the UART BTLE client characteristic which is necessary for notifications.
    UUID CLIENT	= UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    // UUIDs for UART service and associated characteristics.
    UUID UART		= UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    UUID TX			= UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    UUID RX			= UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");

    // UUIDs for the Device Information service and associated characeristics.
    UUID DIS			= UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    UUID DIS_MANUF		= UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb");
    UUID DIS_MODEL		= UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb");
    UUID DIS_HWREV		= UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
    UUID DIS_SWREV		= UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb");

    // ============================================================
    // Methods
    // ============================================================

}
