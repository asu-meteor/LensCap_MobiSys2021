package edu.ame.asu.meteor.lenscap.visualtransceiver

import android.content.Context
import android.content.IntentFilter
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.PublicKey

class Verify{
    companion object {
        private var fos:FileOutputStream? = null

         fun setup(context: Context){

            val outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
             try {
                val tmpFile:File = File.createTempFile("key" , ".txt", outputDir)
                fos = FileOutputStream(tmpFile)
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to create cache file", Toast.LENGTH_SHORT).show()
            }

        }

        fun write(deltaTime:PublicKey){
            if(fos == null){
                Log.d("FrameRateMetrics", "FIleOutputStream not initialized")
                return
            }

            try {
                fos!!.write(String.format("%s \n", deltaTime.toString()).toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun close(){
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}
