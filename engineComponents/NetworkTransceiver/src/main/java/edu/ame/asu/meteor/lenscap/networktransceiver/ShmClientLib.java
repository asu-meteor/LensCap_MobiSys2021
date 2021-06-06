package edu.ame.asu.meteor.lenscap.networktransceiver;

import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class ShmClientLib {
    static {
        System.loadLibrary("client-lib");
    }
    private static HashMap<String,Integer> memAreas = new HashMap<>();

    public static int OpenSharedMem(String name, int size, boolean create)  {
        Integer i = memAreas.get(name);
        //Log.d("lenscap1 shmclientlib", "integer" + i);
        if (create && i != null)
            return -1;
        if (i == null){
            //Log.d("lenscap1 shmclientlib", "get called");
            i = getFD(name, size);
            memAreas.put(name, i);
        }
        return i;
    }
    public static <ByteArray> void setValue(String name, ByteArray val){
        //Log.d("ashmem1 ShmClientlib", "setValue");
        Integer fd = memAreas.get(name);
        //String s = new String((byte[]) val, StandardCharsets.UTF_8);
        //Log.d("lenscap1 ShimLib input val ", s);
        if(fd != null) {
            setVal(fd, val);
        }
    }
    private static native <ByteArray> void setVal (int fd, ByteArray val);
    public static native <ByteArray> ByteArray getVal(int insize);
    public static native void setMap(int fd , int size);
    private static native int getFD(String name , int size);
}
