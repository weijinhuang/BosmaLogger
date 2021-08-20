// IBosmaLog.aidl
package com.bosma.logger;

// Declare any non-default types here with import statements

interface IBosmaLog {

    void log(String msg);

    boolean logEnable();
}