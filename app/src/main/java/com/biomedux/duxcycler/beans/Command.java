// ============================================================
// FileName		: Command.java
// Author		: JaeHong Min
// Date			: 2017.07.04
// ============================================================
//
// Command List
//
// 0 (CMD_NOP)			: only receive the device status
// 1 (CMD_TASK_WRITE)	: write protocol on device with label, temp(0~104), time_high, time_low
// 2 (CMD_TASK_END)		: end process of task write
// 3 (CMD_GO)			: start pcr task with saved protocol
// 4 (CMD_STOP)			: stop pcr task
// 5 (CMD_BOOTLOADER)	: go to bootloader mode for change the firmware
//
// ============================================================

package com.biomedux.duxcycler.beans;

public interface Command {

    // ============================================================
    // Constants
    // ============================================================

    int NOP				= 0;
    int TASK_WRITE		= 1;
    int TASK_END		= 2;
    int GO				= 3;
    int STOP			= 4;
    int BOOTLOADER		= 5;

    // ============================================================
    // Methods
    // ============================================================

}
