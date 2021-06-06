package edu.ame.asu.meteor.lenscap.networktransceiver

//import edu.ame.asu.meteor.lenscap.R

import android.app.PendingIntent
import android.app.Service
import android.content.*
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Binder
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.PRIORITY_HIGH
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import edu.ame.asu.meteor.lenscap.networktransceiver.LensCapNetworkTransceiver.Companion.CHANNEL_NETWORK_SERVICE
import edu.ame.asu.meteor.lenscap.transceiver.IReceiverService
import timber.log.Timber
import java.io.ByteArrayOutputStream


class TransmitterService:Service(){
    companion object {
        const val ACTION_SEND_REF = "action_send_reference"
    }

    var ioService: IReceiverService? = null
    lateinit var p: ParcelFileDescriptor
    lateinit var wtf123 :ByteArray

    inner class SendReferenceReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent != null && intent.action == ACTION_SEND_REF && checkSelfPermission("edu.ame.asu.meteor.lenscap.TRANSCEIVE")== PERMISSION_GRANTED){
                //ioService?.takeReferenceImage(getReferenceImage())
                Toast.makeText(this@TransmitterService, "got broadcast", LENGTH_LONG).show()
            }
        }
    }

    fun send(identifier:String,bytes:ByteArray){
        //Log.d("lenscap1 network transceiver", "transmitter service value set + " + String(bytes))
        //Log.d("lenscap1 network transceiver", "transmitter service size of data: " + bytes.size)
        ShmClientLib.setValue("sh2", bytes)
        var sizein = bytes.size.toString()
        ioService?.takeReferenceImage(identifier,sizein.toByteArray())
    }

    fun getAshmemData(inputsize: Int) : ByteArray{
        //ShmClientLib.setMap(p.fd,1064595)
        //p?.fd?.let { ShmClientLib.setMap(it, 1000) }
        wtf123 = ShmClientLib.getVal(inputsize)
        return wtf123
        //Log.d("lenscap1 network transceiver", "transmitter service value: " + String(wtf123))
    }

    private val ioServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Timber.d("Service connected.")
            Toast.makeText(this@TransmitterService, "Connected", LENGTH_LONG).show()
            ioService = IReceiverService.Stub.asInterface(service)
            try {
                p = ioService?.OpenSharedMem("sh1", 1100000, false)!!
                ShmClientLib.setMap(p.fd,1100000)
                //ShmClientLib.setMap(p.fd,1064595)
                //Log.d("lenscap1 network transceiver", "transmitter service parcelfiledescriptor: " + p.fd)
            } catch (e: java.lang.Exception) {
                e.printStackTrace();
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Timber.d("Service disconnected.")
        }
    }

    override fun onCreate() {
        val intentFilter = IntentFilter()
        val ntp = ShmClientLib.OpenSharedMem("sh2", 1100000, true)
        intentFilter.addAction(ACTION_SEND_REF)
        registerReceiver(SendReferenceReceiver(), intentFilter)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //Timber.d("Starting service...")
        val ioServiceIntent = Intent("edu.ame.asu.meteor.lenscap.visualtransceiver.ReceiverService")
            ioServiceIntent.setPackage("com.meteor.ARtest_2")

//        val ioServiceIntent = Intent(this, IReceiverService::class.java)
        ioServiceIntent.action = "testAction"


        val contentIntent = Intent()
        contentIntent.action = ACTION_SEND_REF
        val sendReferenceIntent = PendingIntent.getBroadcast(
            this@TransmitterService,
            3040,
            contentIntent,
            0
        )
        bindService(ioServiceIntent, ioServiceConnection, Context.BIND_AUTO_CREATE)
        val notification = NotificationCompat.Builder(this, CHANNEL_NETWORK_SERVICE)
            .setContentTitle("LensCap App Development Framework")
            .setContentText("Network Process Running")
            .setContentIntent(sendReferenceIntent)
            .setSmallIcon(R.drawable.ic_network_check_black_24dp)
            .setPriority(PRIORITY_HIGH)
            .build()
        startForeground(2293, notification)
        return super.onStartCommand(ioServiceIntent, flags, startId)
    }

    override fun onDestroy() {
        unbindService(ioServiceConnection)
        super.onDestroy()
    }

    fun getReferenceImage():ByteArray{

        //Timber.d("getReferenceImage()")
        try {
            val inputStream = resources.openRawResource(R.raw.stones)

            val bb = ByteArrayOutputStream()

            val buffer = ByteArray(4096)
            var bytesRead: Int = inputStream.read(buffer)
            while(bytesRead != -1) {
                bb.write(buffer)
                bytesRead = inputStream.read(buffer)
            }

            inputStream.close()
            //Timber.d("getReferenceImage() finished with %s bytes", bb.size())
            return bb.toByteArray()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ByteArray(0)
    }

    override fun onBind(intent: Intent?): IBinder? = TransmitterServiceBinder()

    fun hasInternet(): Boolean{
        return checkSelfPermission("android.permission.INTERNET") == PERMISSION_GRANTED
    }
    inner class TransmitterServiceBinder: Binder() {
        val service: TransmitterService
            get() = this@TransmitterService
    }
}