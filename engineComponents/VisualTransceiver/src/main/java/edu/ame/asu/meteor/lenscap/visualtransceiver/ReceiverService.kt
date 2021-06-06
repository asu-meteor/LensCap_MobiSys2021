package edu.ame.asu.meteor.lenscap.visualtransceiver

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.PixelFormat
import android.os.*
import android.view.Gravity
import android.view.WindowManager
import edu.ame.asu.meteor.lenscap.transceiver.IReceiverService
import edu.ame.asu.meteor.lenscap.transceiver.IDataListener
import java.io.IOException


class ReceiverService : Service() {

    private lateinit var params:WindowManager.LayoutParams

    private var refImage:ByteArray? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //Timber.d("Starting service...")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        //Timber.d("Overlays available:...%s", Settings.canDrawOverlays(this))

        val type = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        else
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

        params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT)

        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = 0
        params.y = 100
        return binder
    }

    fun getTime():String = "10:46"

    private val binder = object: IReceiverService.Stub(){
        var listener: IDataListener? = null
        override fun onDataReceived(listener: IDataListener?) {
            this.listener = listener
        }

        override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String?) {
            // Nothing
        }

        override fun getPid(): Int = Process.myPid()

        override fun getPackageName(): String {
            return this@ReceiverService.packageName
        }

        override fun hasInternet(): Boolean{
            return checkSelfPermission("android.permission.INTERNET") == PERMISSION_GRANTED
        }

        override fun getReferenceImage(): ByteArray? {

            //Timber.d("getReferenceImage()");
            return refImage
        }

        override fun takeReferenceImage(identifier:String,inputImage: ByteArray?) {
            if(inputImage == null){
                // handle
            }else{
                //save input image.
                //Timber.d("Got reference image!")

                //Timber.d(String.format("First few bytes: [%d,%d,%d,%d]", inputImage[0], inputImage[1], inputImage[2], inputImage[3]))
                refImage = inputImage
                listener?.onData(identifier,refImage)
            }
        }

        override fun OpenSharedMem(name: String?, size: Int, create: Boolean): ParcelFileDescriptor? {
            val fd = ShmLib.OpenSharedMem(name, size, create)
            try {
                return ParcelFileDescriptor.fromFd(fd)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }
}
