package edu.ame.asu.meteor.lenscap.networktransceiver

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.support.design.widget.FloatingActionButton
import android.os.IBinder
import android.support.annotation.NonNull
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Switch
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import edu.ame.asu.meteor.lenscap.transceiver.IDataListener
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.ArrayList


class LensCapNetworkTransceiver(@NonNull private val context: Context) {
    var serviceConnected:Boolean = false
    //private var floatyView: View? = null
    var bytesPerm:ByteArray? = null
    private var receiverAvailableEmitter: () -> Unit = {}
    lateinit var hold :ByteArray
    private var accept=false
    lateinit var finalDataToSend:ByteArray
    var time=" ".toByteArray()
    lateinit var Verify: Verifier
    var myCameraFrameSwith:Switch? = null
    var myCameraStateSwith:Switch? = null
    var myCameraFaceSwith:Switch? = null
    var myCameraPointCloudSwith:Switch? = null
    var myCamereLightEstimateloudSwith:Switch? = null
    var rl:RelativeLayout?=null
    private var chartToDraw:BarChart?=null
    private var receiverServiceConnection: ReceiverServiceConnection = ReceiverServiceConnection {
        receiverAvailableEmitter()
        serviceConnected = true
        it.onDataReceived(DL())
    }

    var receivers = arrayListOf<Receiver<ByteArray>>()

    class PermissionConfigurationException: Exception()

    interface Receiver<T> {
        fun onReceived(identifier: String, data:T)
    }

    inner class DL: IDataListener.Stub() {
        override fun onData(identifier: String,data: ByteArray?) {
            //Timber.d("LensCap NetworkTransceiver got something!")
            if (data == null) {
                return
            }
            for (rcvr in receivers) {
                //Log.d("lenscap1 Network Transceiver onData() bytes with length", data.size.toString())
                finalDataToSend = getAshmemData(String(data).toInt())
                //Log.d("lenscap1 network transceiver final data to send", String(finalDataToSend))
                rcvr.onReceived(identifier, finalDataToSend)
                /*val returns= String(finalDataToSend)
                val pre = returns.split("|")
                if (pre[0].equals("lenscapCount")) {
                    val strs = pre[1].substring(1, pre[1].length - 1).split(",")
                    myCameraFrameSwith?.setText("Camera Frame: " + strs[0])
                    myCameraStateSwith?.setText("Camera Pose: " + strs[1])
                    myCamereLightEstimateloudSwith?.setText("Light Estimation: " + strs[2])
                    myCameraFaceSwith?.setText("Face Tracking: " + strs[3])
                    myCameraPointCloudSwith?.setText("Point Cloud: " + strs[4])
                }*/
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
        const val CHANNEL_NETWORK_SERVICE = "network_service_channel"
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
            this@LensCapNetworkTransceiver.transmitterService = binder.service
        }

    }

    init {
        //when(TYPE){
         //   ProcessType.TYPE_NETWORK ->{
        createNotificationChannels()
        if(start()) {
            Verify=Verifier("SignedData.txt","publicKey")
            startTransmitter()
            startReceiver()
        }
        else
        {
            //Verify=Verifier("SignedData.txt","publicKey")
            //startTransmitter()
            startReceiver()
         //   }else->{
            // check that permissions are correct
            //throw PermissionConfigurationException()
            }
        //}
        //}
    }


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
        if(transmitterService == null){
            throw TransmitterNotAvailableException()
        }
//        receiverServiceConnection.receiverService?.takeReferenceImage(content)
        transmitterService?.send(identifier,content)
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
                    CHANNEL_NETWORK_SERVICE,
                    "Network Interface",
                    NotificationManager.IMPORTANCE_HIGH
            )
            val manager: NotificationManager = context.getSystemService(NotificationManager::class.java) as NotificationManager

            // Actually create the channels
            manager.createNotificationChannel(btChannel)
        }
    }

    fun addOverlayView(floatyView: View) {
        //val myTextView = floatyView?.findViewById(R.id.textView1) as TextView
        //myTextView.setText("123")
        /*
        myCameraFrameSwith = floatyView?.findViewById(R.id.switchCameraFrame) as Switch
        myCameraFrameSwith!!.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                //myCameraFrameSwith.setText(lastText)
                val cameraFramePermission = "cameraFrame,false"
                myCameraFrameSwith!!.setText("Camera Frame Transaction OFF")
                try{
                    bytesPerm = cameraFramePermission.toByteArray()
                    send("cameraFrame|off", this.bytesPerm!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                val cameraFramePermission = "cameraFrame,true"
                myCameraFrameSwith!!.setText("Camera Frame Transaction ON")
                try{
                    bytesPerm = cameraFramePermission.toByteArray()
                    send("cameraFrame|on", this.bytesPerm!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        myCameraStateSwith = floatyView?.findViewById(R.id.switchCameraState) as Switch
        myCameraStateSwith!!.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                val cameraStatePermission = "cameraPose,false"
                myCameraStateSwith!!.setText("Camera Pose Transaction OFF")
                try{
                    bytesPerm = cameraStatePermission.toByteArray()
                    send("cameraPose|off", this.bytesPerm!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                val cameraStatePermission = "cameraPose,true"
                myCameraStateSwith!!.setText("Camera Pose Transaction ON")
                try{
                    bytesPerm = cameraStatePermission.toByteArray()
                    send("cameraPose|on", this.bytesPerm!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        myCameraFaceSwith = floatyView?.findViewById(R.id.switchFace) as Switch
        myCameraFaceSwith!!.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                val facePermission = "face,false"
                myCameraFaceSwith!!.setText("Face Tracking Transaction OFF")
                try{
                    bytesPerm = facePermission.toByteArray()
                    send("face|off", this.bytesPerm!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                val facePermission = "face,true"
                myCameraFaceSwith!!.setText("Face Tracking Transaction ON")
                try{
                    bytesPerm = facePermission.toByteArray()
                    send("face|on", this.bytesPerm!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        myCameraPointCloudSwith = floatyView?.findViewById(R.id.switchPointCloud) as Switch
        myCameraPointCloudSwith!!.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                val pointCloudPermission = "pointCloud,false"
                myCameraPointCloudSwith!!.setText("Point Cloud Transaction OFF")
                try{
                    bytesPerm = pointCloudPermission.toByteArray()
                    send("pointCloud|off", this.bytesPerm!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                val pointCloudPermission = "pointCloud,true"
                myCameraPointCloudSwith!!.setText("Point Cloud Transaction ON")
                try{
                    bytesPerm = pointCloudPermission.toByteArray()
                    send("pointCloud|on", this.bytesPerm!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        myCamereLightEstimateloudSwith = floatyView?.findViewById(R.id.switchLightEstimate) as Switch
        myCamereLightEstimateloudSwith!!.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                val lightEstimatePermission = "lightEstimation,false"
                myCamereLightEstimateloudSwith!!.setText("Light Estimate Transaction OFF")
                try{
                    bytesPerm = lightEstimatePermission.toByteArray()
                    send("lightEstimation|off", this.bytesPerm!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                val lightEstimatePermission = "lightEstimation,true"
                myCamereLightEstimateloudSwith!!.setText("Light Estimate Transaction ON")
                try{
                    bytesPerm = lightEstimatePermission.toByteArray()
                    send("lightEstimation|on", this.bytesPerm!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }*/
        rl = floatyView?.findViewById(R.id.relativelaout1) as RelativeLayout
        chartToDraw = floatyView?.findViewById(R.id.chart1) as BarChart
        val myNetworkMetterFloatingButton = floatyView?.findViewById(R.id.floatingActionButton1) as FloatingActionButton
        myNetworkMetterFloatingButton.setOnClickListener{
            lenscapAnalyzeTimeStamp(rl!!, chartToDraw!!)
        }
    }

    private fun lenscapAnalyzeTimeStamp(rrrlll: RelativeLayout, ctdraw: BarChart) {
        val ft = SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz")
        var cameraPoseStampName = "/storage/emulated/0/" + "cameraPose_stamp_date.txt"
        var lightEstimateStampName = "/storage/emulated/0/" + "lightEstimation_stamp_date.txt"
        var pointCloudStampName = "/storage/emulated/0/" + "pointCloud_stamp_date.txt"
        var faceDetectionStampName = "/storage/emulated/0/" + "face_stamp_date.txt"
        var cameraFrameStampName = "/storage/emulated/0/" + "frame_stamp_date.txt"
        val dateOfCameraPose: ArrayList<Int> = ArrayList()
        val dateOfLight: ArrayList<Int> = ArrayList()
        val dateOfPointCloud: ArrayList<Int> = ArrayList()
        val dateOfFace: ArrayList<Int> = ArrayList()
        val dateOfCameraFrame: ArrayList<Int> = ArrayList()
        if (File(cameraPoseStampName).exists()) {
            val bufReader = BufferedReader(FileReader(cameraPoseStampName))
            var line = bufReader.readLine()
            while (line != null) {
                var internalStr1 = line.split(" ")
                var internalFurther = internalStr1[3].split(":")
                var secondtoAdd = internalFurther[0].toInt()*60*60 + internalFurther[1].toInt()*60 + internalFurther[2].toInt()
                dateOfCameraPose.add(secondtoAdd)
                line = bufReader.readLine()
            }
            bufReader.close();
            Log.d("lenscap1 network process read camera pose time stamp: ", dateOfCameraPose.toString())
        } else {
            Log.d("lenscap1 network process read camera pose time stamp: ", "File Does Not Exist")
        }

        if (File(lightEstimateStampName).exists()) {
            val bufReader = BufferedReader(FileReader(lightEstimateStampName))
            var line = bufReader.readLine()
            while (line != null) {
                var internalStr2 = line.split(" ")
                var internalFurther = internalStr2[3].split(":")
                var secondtoAdd = internalFurther[0].toInt()*60*60 + internalFurther[1].toInt()*60 + internalFurther[2].toInt()
                dateOfLight.add(secondtoAdd)
                line = bufReader.readLine()
            }
            bufReader.close();
            Log.d("lenscap1 network process read light estimation time stamp: ", dateOfLight.toString())
        } else {
            Log.d("lenscap1 network process read light estimation time stamp: ", "File Does Not Exist")
        }

        if (File(pointCloudStampName).exists()) {
            val bufReader = BufferedReader(FileReader(pointCloudStampName))
            var line = bufReader.readLine()
            while (line != null) {
                var internalStr3 = line.split(" ")
                var internalFurther = internalStr3[3].split(":")
                var secondtoAdd = internalFurther[0].toInt()*60*60 + internalFurther[1].toInt()*60 + internalFurther[2].toInt()
                dateOfPointCloud.add(secondtoAdd)
                line = bufReader.readLine()
            }
            bufReader.close();
            Log.d("lenscap1 network process read point cloud time stamp: ", dateOfPointCloud.toString())
        } else {
            Log.d("lenscap1 network process read point cloud time stamp: ", "File Does Not Exist")
        }

        if (File(faceDetectionStampName).exists()) {
            val bufReader = BufferedReader(FileReader(faceDetectionStampName))
            var line = bufReader.readLine()
            while (line != null) {
                var internalStr4 = line.split(" ")
                var internalFurther = internalStr4[3].split(":")
                var secondtoAdd = internalFurther[0].toInt()*60*60 + internalFurther[1].toInt()*60 + internalFurther[2].toInt()
                dateOfFace.add(secondtoAdd)
                line = bufReader.readLine()
            }
            bufReader.close();
            Log.d("lenscap1 network process read face detection time stamp: ", dateOfFace.toString())
        } else {
            Log.d("lenscap1 network process read face detection time stamp: ", "File Does Not Exist")
        }

        if (File(cameraFrameStampName).exists()) {
            val bufReader = BufferedReader(FileReader(cameraFrameStampName))
            var line = bufReader.readLine()
            while (line != null) {
                var internalStr5 = line.split(" ")
                var internalFurther = internalStr5[3].split(":")
                var secondtoAdd = internalFurther[0].toInt()*60*60 + internalFurther[1].toInt()*60 + internalFurther[2].toInt()
                dateOfCameraFrame.add(secondtoAdd)
                line = bufReader.readLine()
            }
            bufReader.close();
            Log.d("lenscap1 network process read camera frame time stamp: ", dateOfCameraFrame.toString())
        } else {
            Log.d("lenscap1 network process read camera frame time stamp: ", "File Does Not Exist")
        }
        getHistogram("cameraPose", dateOfCameraPose, rrrlll, ctdraw)
        //getHistogram("lightEstimation", dateOfLight, rrrlll, ctdraw)
        //getHistogram("pointCloud", dateOfPointCloud, rrrlll, ctdraw)
        //getHistogram("faceDetection", dateOfFace, rrrlll, ctdraw)
        //getHistogram("cameraFrame", dateOfCameraFrame, rrrlll, ctdraw)
    }

    private fun getHistogram (Tag: String, inputArrayList: ArrayList<Int>, rl:RelativeLayout, chart: BarChart) {
        val date0_1: ArrayList<Int> = ArrayList()
        val date1_2: ArrayList<Int> = ArrayList()
        val date2_3: ArrayList<Int> = ArrayList()
        val date3_4: ArrayList<Int> = ArrayList()
        val date4_5: ArrayList<Int> = ArrayList()
        val date5_6: ArrayList<Int> = ArrayList()
        val date6_7: ArrayList<Int> = ArrayList()
        val date7_8: ArrayList<Int> = ArrayList()
        val date8_9: ArrayList<Int> = ArrayList()
        val date9_10: ArrayList<Int> = ArrayList()
        val date10_11: ArrayList<Int> = ArrayList()
        val date11_12: ArrayList<Int> = ArrayList()
        val date12_13: ArrayList<Int> = ArrayList()
        val date13_14: ArrayList<Int> = ArrayList()
        val date14_15: ArrayList<Int> = ArrayList()
        val date15_16: ArrayList<Int> = ArrayList()
        val date16_17: ArrayList<Int> = ArrayList()
        val date17_18: ArrayList<Int> = ArrayList()
        val date18_19: ArrayList<Int> = ArrayList()
        val date19_20: ArrayList<Int> = ArrayList()
        val date20_21: ArrayList<Int> = ArrayList()
        val date21_22: ArrayList<Int> = ArrayList()
        val date22_23: ArrayList<Int> = ArrayList()
        val date23_24: ArrayList<Int> = ArrayList()
        for (it in inputArrayList){
            if (it in 0..3599) {
                date0_1.add(it)
            } else if (it in 3600..7199) {
                date1_2.add(it)
            } else if (it in 7200..10799) {
                date2_3.add(it)
            } else if (it in 10800..14399) {
                date3_4.add(it)
            } else if (it in 14400..17999) {
                date4_5.add(it)
            } else if (it in 18000..21599) {
                date5_6.add(it)
            } else if (it in 21600..25199) {
                date6_7.add(it)
            } else if (it in 25200..28799) {
                date7_8.add(it)
            } else if (it in 28800..32399) {
                date8_9.add(it)
            } else if (it in 32400..35999) {
                date9_10.add(it)
            } else if (it in 36000..39599) {
                date10_11.add(it)
            } else if (it in 39600..43199) {
                date11_12.add(it)
            } else if (it in 43200..46799) {
                date12_13.add(it)
            } else if (it in 46800..50399) {
                date13_14.add(it)
            } else if (it in 50400..53999) {
                date14_15.add(it)
            } else if (it in 54000..57599) {
                date15_16.add(it)
            } else if (it in 57600..61199) {
                date16_17.add(it)
            } else if (it in 61200..64799) {
                date17_18.add(it)
            } else if (it in 64800..68399) {
                date18_19.add(it)
            } else if (it in 68400..71999) {
                date19_20.add(it)
            } else if (it in 72000..75599) {
                date20_21.add(it)
            } else if (it in 75600..79199) {
                date21_22.add(it)
            } else if (it in 79200..82799) {
                date22_23.add(it)
            } else if (it in 82800..86400) {
                date23_24.add(it)
            }
        }
        Log.d("lenscap1 " + Tag, date0_1.size.toString() + " " + date1_2.size.toString() + " " + date2_3.size.toString() + " " + date3_4.size.toString() + " " + date4_5.size.toString() + " "
                + date5_6.size.toString() + " " + date6_7.size.toString() + " " + date7_8.size.toString() + " " + date8_9.size.toString() + " " + date9_10.size.toString() + " "
                + date10_11.size.toString() + " " + date11_12.size.toString() + " " + date12_13.size.toString()+ " " + date13_14.size.toString() + " " + date14_15.size.toString() + " "
                + date15_16.size.toString() + " " + date16_17.size.toString() + " " + date17_18.size.toString() + " " + date18_19.size.toString() + " " + date19_20.size.toString() + " "
                + date20_21.size.toString() + " " + date21_22.size.toString() + " " + date22_23.size.toString() + " " + date23_24.size.toString())

        var dataSets: ArrayList<BarDataSet>? = null
        val valueSet2: ArrayList<BarEntry> = ArrayList()
        val v2e1 = BarEntry(1.0f, date0_1.size.toFloat())
        valueSet2.add(v2e1)
        val v2e2 = BarEntry(2f, date1_2.size.toFloat())
        valueSet2.add(v2e2)
        val v2e3 = BarEntry(3f, date2_3.size.toFloat())
        valueSet2.add(v2e3)
        val v2e4 = BarEntry(4f, date3_4.size.toFloat())
        valueSet2.add(v2e4)
        val v2e5 = BarEntry(5f, date4_5.size.toFloat())
        valueSet2.add(v2e5)
        val v2e6 = BarEntry(6f, date5_6.size.toFloat())
        valueSet2.add(v2e6)
        val v2e7 = BarEntry(7f, date6_7.size.toFloat())
        valueSet2.add(v2e7)
        val v2e8 = BarEntry(8f, date7_8.size.toFloat())
        valueSet2.add(v2e8)
        val v2e9 = BarEntry(9f, date8_9.size.toFloat())
        valueSet2.add(v2e9)
        val v2e10 = BarEntry(10f, date9_10.size.toFloat())
        valueSet2.add(v2e10)
        val v2e11 = BarEntry(11f, date10_11.size.toFloat())
        valueSet2.add(v2e11)
        val v2e12 = BarEntry(12f, date11_12.size.toFloat())
        valueSet2.add(v2e12)
        val v2e13 = BarEntry(13f, date12_13.size.toFloat())
        valueSet2.add(v2e13)
        val v2e14 = BarEntry(14f, date13_14.size.toFloat())
        valueSet2.add(v2e14)
        val v2e15 = BarEntry(15f, date14_15.size.toFloat())
        valueSet2.add(v2e15)
        val v2e16 = BarEntry(16f, date15_16.size.toFloat())
        valueSet2.add(v2e16)
        val v2e17 = BarEntry(17f, date16_17.size.toFloat())
        valueSet2.add(v2e17)
        val v2e18 = BarEntry(18f, date17_18.size.toFloat())
        valueSet2.add(v2e18)
        val v2e19 = BarEntry(19f, date18_19.size.toFloat())
        valueSet2.add(v2e19)
        val v2e20 = BarEntry(20f, date19_20.size.toFloat())
        valueSet2.add(v2e20)
        val v2e21 = BarEntry(21f, date20_21.size.toFloat())
        valueSet2.add(v2e21)
        val v2e22 = BarEntry(22f, date21_22.size.toFloat())
        valueSet2.add(v2e22)
        val v2e23 = BarEntry(23f, date22_23.size.toFloat())
        valueSet2.add(v2e23)
        val v2e24 = BarEntry(24f, date23_24.size.toFloat())
        valueSet2.add(v2e24)
        val barDataSet2 = BarDataSet(valueSet2, Tag)
        barDataSet2.setColors(Color.rgb(0, 155, 155))
        dataSets = ArrayList()
        dataSets.add(barDataSet2)
/*
        val valueSet3: ArrayList<BarEntry> = ArrayList()
        val v3e1 = BarEntry(1.5f, date0_1.size.toFloat())
        valueSet2.add(v3e1)
        val v3e2 = BarEntry(2.5f, date1_2.size.toFloat())
        valueSet2.add(v3e2)
        val v3e3 = BarEntry(3.5f, date2_3.size.toFloat())
        valueSet2.add(v3e3)
        val v3e4 = BarEntry(4.5f, date3_4.size.toFloat())
        valueSet2.add(v3e4)
        val v3e5 = BarEntry(5.5f, date4_5.size.toFloat())
        valueSet2.add(v3e5)
        val v3e6 = BarEntry(6.5f, date5_6.size.toFloat())
        valueSet2.add(v3e6)
        val v3e7 = BarEntry(7.5f, date6_7.size.toFloat())
        valueSet2.add(v3e7)
        val v3e8 = BarEntry(8.5f, date7_8.size.toFloat())
        valueSet2.add(v3e8)
        val v3e9 = BarEntry(9.5f, date8_9.size.toFloat())
        valueSet2.add(v3e9)
        val v3e10 = BarEntry(10.5f, date9_10.size.toFloat())
        valueSet2.add(v3e10)
        val v3e11 = BarEntry(11.5f, date10_11.size.toFloat())
        valueSet2.add(v3e11)
        val v3e12 = BarEntry(12.5f, date11_12.size.toFloat())
        valueSet2.add(v3e12)
        val v3e13 = BarEntry(13.5f, date12_13.size.toFloat())
        valueSet2.add(v3e13)
        val v3e14 = BarEntry(14.5f, date13_14.size.toFloat())
        valueSet2.add(v3e14)
        val v3e15 = BarEntry(15.5f, date14_15.size.toFloat())
        valueSet2.add(v3e15)
        val v3e16 = BarEntry(16.5f, date15_16.size.toFloat())
        valueSet2.add(v3e16)
        val v3e17 = BarEntry(17.5f, date16_17.size.toFloat())
        valueSet2.add(v3e17)
        val v3e18 = BarEntry(18.5f, date17_18.size.toFloat())
        valueSet2.add(v3e18)
        val v3e19 = BarEntry(19.5f, date18_19.size.toFloat())
        valueSet2.add(v3e19)
        val v3e20 = BarEntry(20.5f, date19_20.size.toFloat())
        valueSet2.add(v3e20)
        val v3e21 = BarEntry(21.5f, date20_21.size.toFloat())
        valueSet2.add(v3e21)
        val v3e22 = BarEntry(22.5f, date21_22.size.toFloat())
        valueSet2.add(v3e22)
        val v3e23 = BarEntry(23.5f, date22_23.size.toFloat())
        valueSet2.add(v3e23)
        val v3e24 = BarEntry(24.5f, date23_24.size.toFloat())
        valueSet2.add(v3e24)
        val barDataSet3 = BarDataSet(valueSet3, "TEST")
        barDataSet3.setColors(Color.rgb(0, 0, 0))
        dataSets.add(barDataSet3)
*/
        //val xAxis: ArrayList<String> = ArrayList<String>()
        //xAxis.add("JAN");
        //xAxis.add("FEB");
        //xAxis.add("MAR");
        //xAxis.add("APR");
        //xAxis.add("MAY");
        //xAxis.add("JUN");
        rl.setVisibility(View.VISIBLE)
        //val chart: BarChart = floatyView?.findViewById(R.id.chart1) as BarChart
        val data: BarData = BarData(dataSets as List<IBarDataSet>?)
        chart.setData(data)
        chart.setDescription(null)
        //chart.animateXY(2000, 2000)
        chart.invalidate()
    }
}