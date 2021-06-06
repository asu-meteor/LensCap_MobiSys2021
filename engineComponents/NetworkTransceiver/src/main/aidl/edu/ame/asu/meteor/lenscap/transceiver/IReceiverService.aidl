// IReceiverService.aidl
package edu.ame.asu.meteor.lenscap.transceiver;

// Declare any non-default types here with import statements
import edu.ame.asu.meteor.lenscap.transceiver.IDataListener;

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

    void takeReferenceImage(String identifier,in byte[] inputImage);

    void onDataReceived(IDataListener listener);

    ParcelFileDescriptor OpenSharedMem(String name, int size, boolean create);
}


