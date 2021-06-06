package edu.ame.asu.meteor.lenscap.visualtransceiver

//import edu.ame.asu.meteor.lenscap.R

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.*
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.PRIORITY_HIGH
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import edu.ame.asu.meteor.lenscap.transceiver.IReceiverService
import edu.ame.asu.meteor.lenscap.visualtransceiver.VisualLensCapTransceiver.Companion.CAMERA_POSE_PERM
import edu.ame.asu.meteor.lenscap.visualtransceiver.VisualLensCapTransceiver.Companion.CHANNEL_CAMERA_POSE_SERVICE
import edu.ame.asu.meteor.lenscap.visualtransceiver.VisualLensCapTransceiver.Companion.CHANNEL_VISUAL_SERVICE
import timber.log.Timber
import java.io.ByteArrayOutputStream


class TransmitterService:Service(){
    companion object {
        const val ACTION_SEND_REF = "action_send_reference"
    }

    var ioService: IReceiverService? = null
    private var list: List<ByteArray>? = null
    lateinit var p: ParcelFileDescriptor
    lateinit var wtf123 :ByteArray

    inner class SendReferenceReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(this@TransmitterService,intent?.action.toString(), LENGTH_LONG)
            if(intent != null && intent.action == ACTION_SEND_REF && checkSelfPermission("edu.ame.asu.meteor.lenscap.TRANSCEIVE")== PERMISSION_GRANTED){
                //ioService?.takeReferenceImage(getReferenceImage())
                Toast.makeText(this@TransmitterService, "got broadcast", LENGTH_LONG).show()
            }
            if(intent != null && intent.action == "TEST" && checkSelfPermission("edu.ame.asu.meteor.lenscap.TRANSCEIVE")== PERMISSION_GRANTED){
                //ioService?.takeReferenceImage(getReferenceImage())
                CAMERA_POSE_PERM=true;
                Toast.makeText(this@TransmitterService, "Permission Allowed", LENGTH_LONG).show()
            }
        }
    }

    fun send(identifier:String,bytes:ByteArray){
        //Log.d("lenscap1 Visual Transceiver TransmitterService send() bytes with length", bytes.size.toString())
        //var ppp = "123"
        //Log.d("lenscap1 visual transceiver", "transmitter service size of data: " + bytes.size)
        ShmLib.setValue("sh1", bytes) //sh1[10] = 200
        //Log.d("lenscap1 visual transceiver", "transmitter service value set + " + ppp.toByteArray().size)
        //var btttt123: ByteArray
        //btttt123 = ShmLib.getVal()
        //Log.d("lenscap1 visual transceiver", "transmitter service value get + " + String(btttt123))
        var sizein = bytes.size.toString()
        ioService?.takeReferenceImage(identifier,sizein.toByteArray())
    }

    fun getAshmemData(inputsize: Int) : ByteArray{
        //ShmClientLib.setMap(p.fd,1064595)
        //p?.fd?.let { ShmClientLib.setMap(it, 1000) }
        wtf123 = ShmLib.getVal(inputsize)
        return wtf123
        //Log.d("lenscap1 visual transceiver", "transmitter service value: " + String(wtf123))
    }

    private val ioServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Timber.d("Service connected.")
            Toast.makeText(this@TransmitterService, "Connected", LENGTH_LONG).show()
            ioService = IReceiverService.Stub.asInterface(service)
            try {
                p = ioService?.OpenSharedMem("sh2", 1100000, false)!!
                ShmLib.setMap(p.fd,1100000)
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
        val ppppp = ShmLib.OpenSharedMem("sh1", 1100000, true)
        //var ppp = "123"
        //ShmLib.setValue("sh1", ppp.toByteArray()) //sh1[10] = 200
        //Log.d("lenscap1 visual transceiver", "transmitter service fileDescriptor: " + ppppp)
        intentFilter.addAction(ACTION_SEND_REF)
        intentFilter.addAction("TEST")
        registerReceiver(SendReferenceReceiver(), intentFilter)
        super.onCreate()
//        val dialogIntent = Intent(this, dialogDemo::class.java)
//        dialogIntent.addFlags(FLAG_ACTIVITY_NEW_TASK)
//        startActivity(dialogIntent)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("Starting service...")
        val ioServiceIntent = Intent("edu.ame.asu.meteor.lenscap.networktransceiver.ReceiverService")
            ioServiceIntent.setPackage("com.meteor.ARtest_2Network")

//        val ioServiceIntent = Intent(this, IReceiverService::class.java)
        ioServiceIntent.action = "testAction"

        val manager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val contentIntent = Intent()
        contentIntent.action = ACTION_SEND_REF
        val sendReferenceIntent = PendingIntent.getBroadcast(
            this@TransmitterService,
            3040,
            contentIntent,
            0
        )
        bindService(ioServiceIntent, ioServiceConnection, Context.BIND_AUTO_CREATE)
        val notification = NotificationCompat.Builder(this, CHANNEL_VISUAL_SERVICE)
            .setContentTitle("LensCap App Development Framework")
            .setContentText("Visual Process Running")
            .setContentIntent(sendReferenceIntent)
            .setSmallIcon(R.drawable.ic_network_check_black_24dp)
            .setPriority(PRIORITY_HIGH)
            .build()

        manager.notify(1234,notification)
        //startForeground(2293, notification)

        val contentIntent1 = Intent()
        contentIntent1.action ="TEST"
        val sendReferenceIntent1 = PendingIntent.getBroadcast(
                this@TransmitterService,
                3070,
                contentIntent1,
                0
        )
        val notification1 = NotificationCompat.Builder(this, CHANNEL_CAMERA_POSE_SERVICE)
                .setContentTitle("Camera Pose Permission Needed")
                .setContentText("Click Here to Allow")
                .setContentIntent(sendReferenceIntent1)
                .setSmallIcon(R.drawable.te)
                .setPriority(PRIORITY_HIGH)
                .build()
        manager.notify(1235,notification1)

        return super.onStartCommand(ioServiceIntent, flags, startId)

    }

    override fun onDestroy() {
        unbindService(ioServiceConnection)
        super.onDestroy()
    }

    fun getReferenceImage():ByteArray{

        //Timber.d("getReferenceImage()")
        //Log.d("lenscap1 Visual Transmitter Service ", "getReferenceImage called")
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