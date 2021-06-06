// IReceiverService.aidl
package com.example.sharedmemlib;
import com.example.sharedmemlib.IDataListener;
// Declare any non-default types here with import statements

interface IReceiverService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
     /** Request the process ID of this service, to do evil things with it. */
    int getPid();

    String getPackageName();

    boolean hasInternet();

    byte[] getReferenceImage();

    void takeReferenceImage(in byte[] inputImage);

    void onDataReceived(IDataListener listener);

    ParcelFileDescriptor OpenSharedMem(String name, int size, boolean create);
}
