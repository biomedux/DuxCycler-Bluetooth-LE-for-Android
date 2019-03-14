package com.biomedux.duxcycler.beans;

public class RxBuffer {
    // ============================================================
    // Constants
    // ============================================================
    public static final int RX_BUFSIZE = 20;// actual received buffer size
                                            // from Firmtech-based firmware

    // ============================================================
    // Fields
    // ============================================================
    private byte[] buffer;
    private int index = 0;

    // ============================================================
    // Methods
    // ============================================================
    public RxBuffer(){
        buffer = new byte[RX_BUFSIZE];
        for( int i=0; i<RX_BUFSIZE; i++){
            buffer[i] = 0;
        }
        index = 0;
    }//RxBuffer()
    public byte[] buffering(byte[] data){
        byte[] result = new byte[0];
        for( int i=0; i<data.length; i++){
            buffer[index++] = data[i];
            if( index >= RX_BUFSIZE){
                result = buffer;
                index = 0;
            }
        }
        return result;
    }//buffering()
    public void clear(){
        for( int i=0; i<RX_BUFSIZE; i++){
            buffer[i] = 0;
        }
        index = 0;
    }//clear()
}//class RxBuffer()
