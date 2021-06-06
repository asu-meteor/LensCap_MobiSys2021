package edu.ame.asu.meteor.lenscap.networktransceiver;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

public class Verifier {
    private List<byte[]> list;
    public PublicKey pubKey;
    public byte[] held;
    @SuppressWarnings("unchecked")
    public Verifier(String filename, String keyFile) throws Exception {



    }

    public void updatesigned(byte[] filename) throws Exception {
       held=filename;
        /*
        //list.clear();
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "SignedData.txt");
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
        if(in!=null) {
            list = (List<byte[]>) in.readObject();
            in.close();
        }

         */

    }
    public boolean verifySignature( byte [] data, String keyFile) throws Exception {
        byte [] signature=held;
        //writeToFile("comeon",held);
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(pubKey);
        sig.update(data);
        return sig.verify(signature);
    }

    public void writeToFile(String path, byte[] key) throws IOException {

        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),path);
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }
    /*
     public PublicKey getPublic(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),filename).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }




     */
    public byte[] getPub() throws Exception {
      return list.get(1);
    }


    public void getPublic(byte[] data) throws Exception {
        byte[] keyBytes = data;
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        //PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        pubKey= kf.generatePublic(spec);
    }


}