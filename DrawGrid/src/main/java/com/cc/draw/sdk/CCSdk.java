package com.cc.draw.sdk;

public class CCSdk {

    static {
        System.loadLibrary("drawgrid");
    }
    private static CCSdk instance;

    public static CCSdk getInstance() {
        if (instance == null) {
            synchronized (CCSdk.class) {
                if (instance == null) {
                    instance = new CCSdk();
                }
            }
        }
        return instance;
    }

    public void init() {
        initial();
    }

    private native void initial();
}
