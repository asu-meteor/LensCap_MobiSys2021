// IDataListener.aidl
package edu.ame.asu.meteor.lenscap.transceiver;

// Declare any non-default types here with import statements

interface IDataListener {
    void onData(String identifier,in byte[] data);
}
