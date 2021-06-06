package com.epicgames.ue4Network

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FrameRateMetrics{
    companion object {
        private var fos:FileOutputStream? = null

         fun setup(context: Context){

            val outputDir = context.cacheDir
            try {
                val tmpFile:File = File.createTempFile("frame_output" + context.applicationInfo.name, ".txt", outputDir)
                fos = FileOutputStream(tmpFile)
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to create cache file", Toast.LENGTH_SHORT).show()
            }

        }

        fun write(deltaTime:Long){
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
        fun writes(deltaTime:String){
            if(fos == null){
                Log.d("FrameRateMetrics", "FIleOutputStream not initialized")
                return
            }

            try {
                fos!!.write(String.format("%s \n", deltaTime).toByteArray())
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
