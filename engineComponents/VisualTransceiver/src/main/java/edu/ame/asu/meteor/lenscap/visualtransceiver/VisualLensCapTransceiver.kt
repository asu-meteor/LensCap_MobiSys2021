package edu.ame.asu.meteor.lenscap.visualtransceiver

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.support.annotation.NonNull
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat.startForegroundService
import android.support.v7.app.AppCompatActivity
import android.util.Log
import edu.ame.asu.meteor.lenscap.transceiver.IDataListener
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.nio.file.attribute.FileTime
import java.security.KeyPair
import java.security.Signature
import java.util.*


open class VisualLensCapTransceiver(@NonNull private val context: Context){
    var serviceConnected:Boolean = false
    private var receiverAvailableEmitter: () -> Unit = {}
    lateinit var sign: Signature
    lateinit var pair:KeyPair
    lateinit var gk: KeyGen
    private var first=true
    private var second=false
    var time=" ".toByteArray()

    var cameraPoseStampName = "/storage/emulated/0/" + "cameraPose_stamp_date.txt"
    var lightEstimateStampName = "/storage/emulated/0/" + "lightEstimation_stamp_date.txt"
    var pointCloudStampName = "/storage/emulated/0/" + "pointCloud_stamp_date.txt"
    var faceDetectionStampName = "/storage/emulated/0/" + "face_stamp_date.txt"
    var cameraFrameStampName = "/storage/emulated/0/" + "frame_stamp_date.txt"

    class LensCapPermissionStructVT(private val cp: Boolean, ct: Context){
        private var cameraFramePerm: Boolean = cp
        private var cameraStatePerm: Boolean = cp
        private var lightEstimatePerm: Boolean = cp
        private var faceFeaturePerm: Boolean = cp
        private var pointCloudPerm: Boolean = cp
        private var imageFeaturePerm: Boolean = cp
        private var planeFeaturePerm: Boolean = cp
        private var pointsFeaturePerm: Boolean = cp
        private var passthroughFeaturePerm: Boolean = cp
        private var cameraconfigFeaturePerm: Boolean = cp
        private var cemerageimageintrinsicsFeaturePerm: Boolean = cp
        private var cameratextureintrinsicsFeaturePerm: Boolean = cp
        private var tansformarcoordinates2dFeaturePerm: Boolean = cp
        private var arcorelinetraceFeaturePerm: Boolean = cp
        private var arcoretrackingstateFeaturePerm: Boolean = cp
        private var cameratextureFeaturePerm: Boolean = cp
        private var arcorecameraconfigFeaturePerm: Boolean = cp


        val ctp=ct
        val manager: NotificationManager = ct.getSystemService(NotificationManager::class.java) as NotificationManager
        fun getPermTag(tag: String): Boolean {
            val strinter = tag.split("_".toRegex()).toTypedArray()
            return when (strinter[1]) {
                "cameraFrame" -> {
                    val channel = manager.getNotificationChannel(CHANNEL_CAMERA_FRAME_SERVICE)
                    cameraFramePerm= CAMERA_FRAME_PERM
                    return channel.importance != NotificationManager.IMPORTANCE_NONE
                }
                "cameraPose" -> {
                    val channel = manager.getNotificationChannel(CHANNEL_CAMERA_POSE_SERVICE)
                    cameraStatePerm= CAMERA_POSE_PERM
                    if(cameraStatePerm)
                    {
                    }
                    return cameraStatePerm//channel.importance != NotificationManager.IMPORTANCE_NONE
                }
                "face" -> {
                    val channel = manager.getNotificationChannel(CHANNEL_FACE_TRACK_SERVICE)
                    faceFeaturePerm= FACE_TRACKING_PERM
                    return channel.importance != NotificationManager.IMPORTANCE_NONE
                }
                "lightEstimation" -> {
                    val channel = manager.getNotificationChannel(CHANNEL_LIGHT_ESTIMATE_SERVICE)
                    lightEstimatePerm= LIGHT_ESTIMATE_PERM
                    return channel.importance != NotificationManager.IMPORTANCE_NONE
                }
                "pointCloud" -> {
                    val channel = manager.getNotificationChannel(CHANNEL_POINT_CLOUD_SERVICE)
                    pointCloudPerm= POINT_CLOUD_PERM
                    return channel.importance != NotificationManager.IMPORTANCE_NONE
                }
                "image"->{
                    val channel = manager.getNotificationChannel(CHANNEL_IMAGE_TRACK_SERVICE)
                    imageFeaturePerm= IMAGE_TRACKING_PERM
                    return channel.importance != NotificationManager.IMPORTANCE_NONE
                }
                "passthrough"->{
                    val channel = manager.getNotificationChannel(CHANNEL_PASSTHROUGH_TRACK_SERVICE)
                    passthroughFeaturePerm = PASSTHROUGH_PERM
                    return channel.importance != NotificationManager.IMPORTANCE_NONE
                }
                "plane"->{
                    val channel = manager.getNotificationChannel(CHANNEL_PLANE_TRACK_SERVICE)
                    planeFeaturePerm= PLANE_TRACKING_PERM
                    return channel.importance != NotificationManager.IMPORTANCE_NONE
                }
                "points"->{
                    val channel = manager.getNotificationChannel(CHANNEL_POINTS_TRACK_SERVICE)
                    pointsFeaturePerm= POINT_TRACKING_PERM
                    return channel.importance != NotificationManager.IMPORTANCE_NONE
                }
                "cameraconfig"->{
                    val channel = manager.getNotificationChannel(CHANNEL_CAMERACONFIG_TRACK_SERVICE)
                    cameraconfigFeaturePerm= CAMERACONFIG_TRACKING_PERM
                    return channel.importance != NotificationManager.IMPORTANCE_NONE
                }
                "TransformARCoordinates2D"->{
                    val channel = manager.getNotificationChannel(CHANNEL_TRANSFORMCOORDINATES2D_TRACK_SERVICE)
                    tansformarcoordinates2dFeaturePerm= TRANSFORMCOORDINATES2D_PERM
                    return channel.importance != NotificationManager.IMPORTANCE_NONE
                }
                "cameraimageintrinsics"->{
                    val channel = manager.getNotificationChannel(CHANNEL_CAMERAIMAGEINTRINSICS_SERVICE)
                    cemerageimageintrinsicsFeaturePerm= CAMERAIMAGEINTRINSICS_PERM
                    return channel.importance != NotificationManager.IMPORTANCE_NONE
                }
                "cameratextureintrinsics"->{
                    val channel = manager.getNotificationChannel(CHANNEL_CAMERATEXTUREINTRINSICS_SERVICE)
                    cameratextureintrinsicsFeaturePerm= CAMERATEXTUREINTRINSICS_PERM
                    return channel.importance != NotificationManager.IMPORTANCE_NONE
                }
                "linetrace"->{
                    val channel = manager.getNotificationChannel(CHANNEL_ARCORELINETRACE_SERVICE)
                    arcorelinetraceFeaturePerm= ARCORELINETRACE_PERM
                    return channel.importance != NotificationManager.IMPORTANCE_NONE
                }
                "trackingstate"->{
                    val channel = manager.getNotificationChannel(CHANNEL_ARCORETRACKINGSTATE_SERVICE)
                    arcoretrackingstateFeaturePerm= ARCORETRACKINGSTATE_PERM
                    return channel.importance != NotificationManager.IMPORTANCE_NONE
                }
                "cameratexture"->{
                    val channel = manager.getNotificationChannel(CHANNEL_CAMERATEXTURE_TRACK_SERVICE)
                    cameratextureFeaturePerm= CAMERATEXTURE_PERM
                    return channel.importance != NotificationManager.IMPORTANCE_NONE
                }
                //addhere
//                private var arcorelinetraceFeaturePerm: Boolean = cp
//                private var arcoretrackingstateFeaturePerm: Boolean = cp
//                private var cameratextureFeaturePerm: Boolean = cp
                else -> false
            }
        }
    }

    var lensCapPermissionInfo: LensCapPermissionStructVT = LensCapPermissionStructVT(false, context)

    class LensCapCounterStructVT (private val cc: Int) {
        var cameraFrameCounter: Int = cc
        var cameraStateCounter: Int = cc
        var lightEstimateCounter: Int = cc
        var faceFeatureCounter: Int = cc
        var pointCloudCounter: Int = cc
    }
    var lensCapCounterInfo: LensCapCounterStructVT = LensCapCounterStructVT(0)

    class LensCapTimerStructVT () {
        var cameraFrame_stamp_date: List<Date> = ArrayList()
        var cameraPose_stamp_date: List<Date> = ArrayList()
        var lightEstimation_stamp_date: List<Date> = ArrayList()
        var faceFeature_stamp_date: List<Date> = ArrayList()
        var pointCloud_stamp_date: List<Date> = ArrayList()
    }
    var lensCapTimerInfo: LensCapTimerStructVT = LensCapTimerStructVT()

    lateinit var hold :ByteArray

    lateinit var finalDataToSend:ByteArray

    private var receiverServiceConnection: ReceiverServiceConnection = ReceiverServiceConnection {
        receiverAvailableEmitter()
        serviceConnected = true
        it.onDataReceived(DL())
    }

    var receivers = arrayListOf<Receiver<ByteArray>>()
    private val list: List<ByteArray>? = null
    class PermissionConfigurationException: Exception()

    interface Receiver<T> {
        fun onReceived(identifier: String, data:T)
    }

    inner class DL: IDataListener.Stub() {
        override fun onData(identifier: String,data: ByteArray?) {
            //Timber.d("LensCap VisualTransceiver got something!")
            if(data == null){
                return
            }
            for(rcvr in receivers){
                //Log.d("lenscap1 Network Transceiver onData() bytes with length", data.size.toString())
                finalDataToSend = getAshmemData(String(data).toInt())
                //Log.d("lenscap1 visual transceiver final data to send", String(finalDataToSend))
                rcvr.onReceived(identifier,finalDataToSend)
            }
        }

    }




    private fun startReceiver(){
        val intent = Intent(context, ReceiverService::class.java)
        context.bindService(intent, receiverServiceConnection, AppCompatActivity.BIND_AUTO_CREATE)
    }

    fun onReceiverAvailable(emitter:()->Unit){
        this.receiverAvailableEmitter = emitter
    }

    private fun counterTimer(tag: String) {
        when (tag) {
            "cameraPose" -> {
                lensCapCounterInfo.cameraStateCounter = lensCapCounterInfo.cameraStateCounter + 1
                val currentDate = Date()
                //lensCapTimerInfo.cameraPose_stamp_date += currentDate
                try {
                    Files.write(Paths.get(cameraPoseStampName), (currentDate.toString() + "\n").toByteArray(), StandardOpenOption.APPEND)
                } catch (e: IOException) {
                }
                //Log.d("lenscap1 counterTimer cameraPose:", lensCapTimerInfo.cameraPose_stamp_date.toString())
            }
            "cameraFrame" -> {
                lensCapCounterInfo.cameraFrameCounter = lensCapCounterInfo.cameraFrameCounter + 1
                val currentDate = Date()
                //lensCapTimerInfo.cameraFrame_stamp_date += currentDate
                try {
                    Files.write(Paths.get(cameraFrameStampName), (currentDate.toString() + "\n").toByteArray(), StandardOpenOption.APPEND)
                } catch (e: IOException) {
                }
            }
            "face" -> {
                lensCapCounterInfo.faceFeatureCounter = lensCapCounterInfo.faceFeatureCounter + 1
                val currentDate = Date()
                //lensCapTimerInfo.faceFeature_stamp_date += currentDate
                try {
                    Files.write(Paths.get(faceDetectionStampName), (currentDate.toString() + "\n").toByteArray(), StandardOpenOption.APPEND)
                } catch (e: IOException) {
                }
            }
            "pointCloud" -> {
                lensCapCounterInfo.pointCloudCounter = lensCapCounterInfo.pointCloudCounter + 1
                val currentDate = Date()
                //lensCapTimerInfo.pointCloud_stamp_date += currentDate
                try {
                    Files.write(Paths.get(pointCloudStampName), (currentDate.toString() + "\n").toByteArray(), StandardOpenOption.APPEND)
                } catch (e: IOException) {
                }
            }
            "lightEstimation" -> {
                lensCapCounterInfo.lightEstimateCounter = lensCapCounterInfo.lightEstimateCounter + 1
                val currentDate = Date()
                //lensCapTimerInfo.lightEstimation_stamp_date += currentDate
                try {
                    Files.write(Paths.get(lightEstimateStampName), (currentDate.toString() + "\n").toByteArray(), StandardOpenOption.APPEND)
                } catch (e: IOException) {
                }
            }
            else -> Log.d("lenscap1 VT:", "Counter not supported")
        }
    }

    fun registerReceiver(receiver: Receiver<ByteArray>){
        when (permissionsConfigured()){
            true-> {
                receivers.add(receiver)
            }
            else -> throw PermissionConfigurationException()
        }
    }


    fun permissionsConfigured() = true

    companion object {
        const val CHANNEL_VISUAL_SERVICE = "visual_transmitter_service_channel"
        const val CHANNEL_CAMERA_POSE_SERVICE = "CHANNEL_CAMERA_POSE_SERVICE"
        const val CHANNEL_POINT_CLOUD_SERVICE = "CHANNEL_POINT_CLOUD_SERVICE"
        const val CHANNEL_CAMERA_FRAME_SERVICE = "CHANNEL_CAMERA_FRAME_SERVICE"
        const val CHANNEL_LIGHT_ESTIMATE_SERVICE = "CHANNEL_LIGHT_ESTIMATE_SERVICE"
        const val CHANNEL_FACE_TRACK_SERVICE = "CHANNEL_FACE_TRACK_SERVICE"
        const val CHANNEL_IMAGE_TRACK_SERVICE = "CHANNEL_IMAGE_TRACK_SERVICE"
        const val CHANNEL_PASSTHROUGH_TRACK_SERVICE = "CHANNEL_PASSTHROUGH_TRACK_SERVICE"
        const val CHANNEL_PLANE_TRACK_SERVICE = "CHANNEL_PLANE_TRACK_SERVICE"
        const val CHANNEL_POINTS_TRACK_SERVICE = "CHANNEL_POINTS_TRACK_SERVICE"
        const val CHANNEL_CAMERACONFIG_TRACK_SERVICE = "CHANNEL_CAMERACONFIG_TRACK_SERVICE"

        const val CHANNEL_CAMERAIMAGEINTRINSICS_SERVICE = "CHANNEL_CAMERAIMAGEINTRINSICS_SERVICE"
        const val CHANNEL_CAMERATEXTUREINTRINSICS_SERVICE = "CHANNEL_CAMERATEXTUREINTRINSICS_SERVICE"
        const val CHANNEL_TRANSFORMCOORDINATES2D_TRACK_SERVICE = "CHANNEL_ARCORELINETRACE_SERVICE"
        const val CHANNEL_ARCORELINETRACE_SERVICE = "CHANNEL_ARCORELINETRACE_SERVICE"
        const val CHANNEL_ARCORETRACKINGSTATE_SERVICE = "CHANNEL_ARCORETRACKINGSTATE_SERVICE"
        const val CHANNEL_CAMERATEXTURE_TRACK_SERVICE = "CHANNEL_CAMERATEXTURE_TRACK_SERVICE"

        var CAMERA_POSE_PERM=false;
        var CAMERA_FRAME_PERM=false;
        var LIGHT_ESTIMATE_PERM=false;
        var POINT_CLOUD_PERM=false;
        var FACE_TRACKING_PERM=false;
        var PLANE_TRACKING_PERM=false;
        var IMAGE_TRACKING_PERM=false;
        var PASSTHROUGH_PERM=false;
        var POINT_TRACKING_PERM=false;
        var CAMERACONFIG_TRACKING_PERM=false;
        var CAMERAIMAGEINTRINSICS_PERM=false;
        var CAMERATEXTUREINTRINSICS_PERM=false;
        var TRANSFORMCOORDINATES2D_PERM=false;
        var ARCORELINETRACE_PERM=false;
        var ARCORETRACKINGSTATE_PERM=false;
        var CAMERATEXTURE_PERM=false;
        var ARCORECAMERACONFIG_PERM=false;
    }

    enum class ProcessType{
        TYPE_NETWORK, TYPE_CAMERA, TYPE_NONE
    }

    class TransmitterNotAvailableException: Exception()
    class ReceiverNotAvailableException: Exception()

    private val TYPE = getType()
    private var receiverConnected:Boolean = false;

    private var transmitterService: TransmitterService? = null
    //    private var receiverServiceConnection: ReceiverServiceConnection = ReceiverServiceConnection{
//        receiverConnected = true
//        receiverAvailableEmitter()
//    }
    private var transmitterServiceConnection: ServiceConnection = object: ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TransmitterService.TransmitterServiceBinder
            this@VisualLensCapTransceiver.transmitterService = binder.service
        }

    }

    init {
        //when(TYPE){
        //   ProcessType.TYPE_NETWORK ->{
        createNotificationChannels()


        //Log.d("lenscap1 visual transceiver", "openSharedMem called")
        //ShmLib.OpenSharedMem("sh1", 1000, true)
        //ShmLib.setValue("sh1", 10, 200) //sh1[10] = 200
        //val v = ShmLib.getValue("sh1", 10)
        //Log.d("lenscap1 visual transceiver", "openSharedMem value: " + v)
        System.loadLibrary("gnustl_shared")
        System.loadLibrary("UE4")

        var cameraPoseStampNameDeleted = false;
        if (File(cameraPoseStampName).exists()) {
            val creationTime: FileTime = Files.getAttribute(Paths.get(cameraPoseStampName), "creationTime") as FileTime
            val currentDate = Date().time
            if ((currentDate - creationTime.toMillis()) > 24*60*60*1000) {
                File(cameraPoseStampName).delete()
                cameraPoseStampNameDeleted = true
            }
        }
        if (cameraPoseStampNameDeleted) {
            File(cameraPoseStampName).createNewFile();
        }

        var cameraFrameStampNameDeleted = false;
        if (File(cameraFrameStampName).exists()) {
            val creationTime: FileTime = Files.getAttribute(Paths.get(cameraFrameStampName), "creationTime") as FileTime
            val currentDate = Date().time
            if ((currentDate - creationTime.toMillis()) > 24*60*60*1000) {
                File(cameraFrameStampName).delete()
                cameraFrameStampNameDeleted = true
            }
        }
        if (cameraFrameStampNameDeleted) {
            File(cameraFrameStampName).createNewFile();
        }

        var faceDetectionStampNameDeleted = false;
        if (File(faceDetectionStampName).exists()) {
            val creationTime: FileTime = Files.getAttribute(Paths.get(faceDetectionStampName), "creationTime") as FileTime
            val currentDate = Date().time
            if ((currentDate - creationTime.toMillis()) > 24*60*60*1000) {
                File(faceDetectionStampName).delete()
                faceDetectionStampNameDeleted = true
            }
        }
        if (faceDetectionStampNameDeleted) {
            File(faceDetectionStampName).createNewFile();
        }

        var lightStampNameDeleted = false;
        if (File(lightEstimateStampName).exists()) {
            val creationTime: FileTime = Files.getAttribute(Paths.get(lightEstimateStampName), "creationTime") as FileTime
            val currentDate = Date().time
            if ((currentDate - creationTime.toMillis()) > 24*60*60*1000) {
                File(lightEstimateStampName).delete()
                lightStampNameDeleted = true
            }
        }
        if (lightStampNameDeleted) {
            File(lightEstimateStampName).createNewFile();
        }

        var pointCloudStampNameDeleted = false;
        if (File(pointCloudStampName).exists()) {
            val creationTime: FileTime = Files.getAttribute(Paths.get(pointCloudStampName), "creationTime") as FileTime
            val currentDate = Date().time
            if ((currentDate - creationTime.toMillis()) > 24*60*60*1000) {
                File(pointCloudStampName).delete()
                pointCloudStampNameDeleted = true
            }
        }
        if (pointCloudStampNameDeleted) {
            File(pointCloudStampName).createNewFile();
        }

        if(start()) {
            startTransmitter()
            startReceiver()
        }
        else
        {
            startTransmitter()
            startReceiver()

            //   }else->{
            // check that permissions are correct
            //throw PermissionConfigurationException()
        }
        //}
        //}
        //Log.e("chips","chips")
//        val dialogIntent = Intent(context, dialogDemo::class.java)
//        //dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        context.startActivity(dialogIntent)
//        startActivityForResult(dialogIntent,123)
//        var checker=intent.extras
//        if(checker==null) {
//            val dialogIntent = Intent(context, dialogDemo::class.java)
//            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            context.startActivity(dialogIntent)
    }


    open external fun nativeValidateVisualTransceiverFloatData(inTag: String?, data: FloatArray?): Boolean

    private fun getType(): ProcessType {
        val hasInternet = context.checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
        val hasCamera = context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        return if(hasInternet && !hasCamera){
            // Network process
            ProcessType.TYPE_NETWORK
        }else{
            ProcessType.TYPE_NONE
        }
    }
    private fun  start(): Boolean {
        val hasPerm = context.checkSelfPermission("edu.ame.asu.meteor.lenscap.TRANSCEIVE") == PackageManager.PERMISSION_GRANTED
        return hasPerm
    }
    fun send(identifier:String, content:ByteArray){
        //Log.d("lenscap1 Visual Transceiver send() bytes with length", content.size.toString())
        if(transmitterService == null){
            throw TransmitterNotAvailableException()
        }
        counterTimer(identifier)
        val interct: Array<Int> = arrayOf(lensCapCounterInfo.cameraFrameCounter, lensCapCounterInfo.cameraStateCounter,
                lensCapCounterInfo.lightEstimateCounter, lensCapCounterInfo.faceFeatureCounter, lensCapCounterInfo.pointCloudCounter)
        //Log.d("lenscap1 send intercounter array: ", interct.contentToString())
        transmitterService?.send(identifier,("lenscapCount|" + interct.contentToString()).toByteArray())
        val receivedData = content.toString(UTF_8)
        val shortened = receivedData.substring(1, receivedData.length - 1)
        //Log.d("lenscap1 check no data send: ", identifier + " " + shortened)
        if (shortened.isEmpty()) {
            Log.d("lenscap1 check no data send: ", identifier)
        } else if (identifier == "cameraFrame") {
            Log.d("lenscap1 no need to check: ", identifier)
        } else {
            //val dataToValidate = (shortened.split(",").map{it.toFloat()}).toFloatArray()
            val currentTimer1: Long = System.nanoTime()
            //if (nativeValidateVisualTransceiverFloatData(identifier,shortened)){
            //Log.d("lenscap1 check data cost nano: ", identifier + " " + (System.nanoTime() - currentTimer1).toString())
            transmitterService?.send(identifier,content)
            //}
        }
    }

    fun getAshmemData(inputsize:Int) : ByteArray{
        if(transmitterService == null){
            throw TransmitterNotAvailableException()
        }
        hold = transmitterService?.getAshmemData(inputsize)!!
        return hold
    }


    private fun startTransmitter(){

        context.startService(Intent(context, TransmitterService::class.java))
        context.bindService(Intent(context, TransmitterService::class.java), transmitterServiceConnection, Context.BIND_AUTO_CREATE)
    }


    fun createNotificationChannels(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val btChannel = NotificationChannel(
                    CHANNEL_VISUAL_SERVICE,
                    "Visual interface",
                    NotificationManager.IMPORTANCE_HIGH
            )
            val CPChannel = NotificationChannel(
                    CHANNEL_CAMERA_POSE_SERVICE,
                    "Camera Pose",
                    NotificationManager.IMPORTANCE_HIGH
            )
            val CFChannel = NotificationChannel(
                    CHANNEL_CAMERA_FRAME_SERVICE,
                    "Camera Frame",
                    NotificationManager.IMPORTANCE_HIGH
            )
            val LEChannel = NotificationChannel(
                    CHANNEL_LIGHT_ESTIMATE_SERVICE,
                    "Light Estimation",
                    NotificationManager.IMPORTANCE_HIGH
            )
            val PCChannel = NotificationChannel(
                    CHANNEL_POINT_CLOUD_SERVICE,
                    "Point Cloud",
                    NotificationManager.IMPORTANCE_HIGH
            )
            val FTChannel = NotificationChannel(
                    CHANNEL_FACE_TRACK_SERVICE,
                    "Face Tracking",
                    NotificationManager.IMPORTANCE_HIGH
            )
            val manager: NotificationManager = context.getSystemService(NotificationManager::class.java) as NotificationManager

            // Actually create the channels
            manager.createNotificationChannel(btChannel)
            manager.createNotificationChannel(CPChannel)
            manager.createNotificationChannel(CFChannel)
            manager.createNotificationChannel(LEChannel)
            manager.createNotificationChannel(PCChannel)
            manager.createNotificationChannel(FTChannel)
        }
    }

}