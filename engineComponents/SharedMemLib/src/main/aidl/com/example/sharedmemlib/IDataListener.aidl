// IDataListener.aidl
package com.example.sharedmemlib;

// Declare any non-default types here with import statements

interface IDataListener {
    void onData(in byte[] data);
}
