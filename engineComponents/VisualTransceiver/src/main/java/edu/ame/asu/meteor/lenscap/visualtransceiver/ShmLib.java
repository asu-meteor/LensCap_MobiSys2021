package edu.ame.asu.meteor.lenscap.visualtransceiver;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
public class ShmLib {
    static {
        System.loadLibrary("native-lib");
    }
    private static HashMap<String,Integer> memAreas = new HashMap<>();

    public static int OpenSharedMem(String name, int size, boolean create)  {
        Integer i = memAreas.get(name);
        //Log.d("lenscap1 shmlib", "name " + name + " Integer i: " + i);
        if (create && i != null)
            return -1;
        if (i == null){
            i = getFD(name, size);
            //Log.d("lenscap1 shmlib", "create FD" + i);
            memAreas.put(name, i);
        }
        return i;

    }
    public static <ByteArray> void setValue(String name, ByteArray val){
        //Log.d("ashmem1 Shmlib", "setValue");
        Integer fd = memAreas.get(name);
        //String s = new String((byte[]) val, StandardCharsets.UTF_8);
        //Log.d("lenscap1 ShimLib input val ", s);
        if(fd != null) {
            setVal(fd, val);
        }
    }
    /*
    public static void setValue(String name, int pos, int val){
        //Log.d("ashmem1 Shmlib", "setValue");
        Integer fd = memAreas.get(name);
        if(fd != null) {
            setVal(fd, pos, val);
        }
    }
    public static int getValue(String name, int pos ){
        Integer fd = memAreas.get(name);
        if(fd != null)
            return getVal(fd,pos);
        return -1;
    }*/
    //private static native int setVal(int fd,int pos, int val);
    //private static native int getVal(int fd,int pos);
    public static native <ByteArray> ByteArray getVal(int insize);
    private static native <ByteArray> void setVal(int fd, ByteArray val);
    public static native void setMap(int fd , int size);
    private static native int getFD(String name , int size);
}
