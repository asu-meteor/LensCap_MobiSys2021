package com.epicgames.ue4Network

//import edu.ame.asu.meteor.arcorelenscap_network.Service.FileDownloadClient
//import edu.ame.asu.meteor.lenscap.transmitter.LensCapTransmitter
//import okhttp3.ResponseBody
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import retrofit2.Retrofit
//import timber.log.Timber
import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.*
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.ar.core.exceptions.NotYetAvailableException
import com.meteor.ARtest_2.R
import edu.ame.asu.meteor.lenscap.networktransceiver.LensCapNetworkTransceiver
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class GameActivity : AppCompatActivity(),View.OnTouchListener {
    companion object {
        const val ACTION_SEND_MODEL:String = "action_send_model"
        const val CHANNEL_SEND_MESSAGE: String = "channel_send_message"
        private const val REQUEST_CODE =10101;
        private val TAG = GameActivity::class.java.simpleName


    }

    private lateinit var windowManager: ViewManager
    private var floatyView: View? = null
    //private var floatyViewNT: View? = null
    lateinit var lensCap: LensCapNetworkTransceiver

    var bytes:ByteArray? = null
    var bytesForTest:ByteArray? = null
    private var download=0
    private var lastTime:Long=0
    var timerAverageCP: List<Long> = ArrayList()
    var timerAverageLE: List<Long> = ArrayList()
    var timerAveragePC: List<Long> = ArrayList()
    var timerAverageEF: List<Long> = ArrayList()
    var timerAverageCF: List<Long> = ArrayList()
    //lateinit var kronosClock: KronosClock

    val receiver:BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent?.action == ACTION_SEND_MODEL && bytes != null){
                lensCap.send("face", bytes!!)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        isStoragePermissionGranted();
        FrameRateMetrics.setup(this)
        super.onCreate(savedInstanceState)
        //kronosClock = AndroidClockFactory.createKronosClock(applicationContext)
        //kronosClock.syncInBackground()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        lensCap = LensCapNetworkTransceiver(applicationContext)
        // lensCapOriginal = LensCapTransmitter(applicationContext)
        lensCap.registerReceiver(object : LensCapNetworkTransceiver.Receiver<ByteArray> {

            override fun onReceived(identifier: String, data: ByteArray) {

                val currentTimer1: Long = System.nanoTime()
                val returns= String(data)
                val pre = returns.split("|")
                Log.e("Received from visual",identifier)
                //Log.d("lenscap1 Network Process Received: ",pre[0] + " data in length " + data.size + " return string in length: " + returns.length + " return in byteArray length: " + returns.toByteArray().size)
                //val ttt = pre[2].substring(1, (pre[2].length-1)).split(",")
                //for (itt in ttt) {
                //    Log.d("lenscap1 Network Process Received Pre[2]: ",itt.toFloat().toString())
                //}
                //Log.d("lenscap1 Network Process Received: ",pre[0] + " | " + pre[1] + " | " + pre[2].substring(1, pre[2].length))
                if (pre[0].equals("cameraPose")){
                    val currentTimer22: Long = System.nanoTime()
                    val currentDataSend = "cameraPose" + "," + currentTimer22 + "," + returns
                    bytesForTest = currentDataSend.toByteArray()
                    val currentTimer33: Long = System.nanoTime()
                    var image: Bitmap? = null
                    /*try {
                        //image = BitmapFactory.decodeFile("/storage/emulated/0/" + "Classified.jpg")
                        //val output = FileOutputStream("/storage/emulated/0/" + "Classified123.png")
                        //image.compress(Bitmap.CompressFormat.PNG, 100, output)
                        //image = ImageIO.read()
                        //FileUploader.writeImageInformation(image, "tmp_image.png", this@GameActivity)
                        //FileUploader.uploadFile(File("/storage/emulated/0/" + "Classified.jpg"), this@GameActivity)
                        //FileUploader.writeByteToTXT(data, "/storage/emulated/0/" + "cameraPose.txt", this@GameActivity)
                        //Log.d("Lenscap1 upload cameraPose start kronosclock time: ", kronosClock.getCurrentTimeMs().toString())
                        //Log.d("Lenscap1 upload cameraPose start system time: ", System.currentTimeMillis().toString())
                        //Log.d("Lenscap1 upload cameraPose time difference: ", (kronosClock.getCurrentTimeMs()-System.currentTimeMillis()).toString())
                        //FileUploader.uploadFile(File("/storage/emulated/0/" + "cameraPose.txt"), this@GameActivity)
                    } catch (e: NotYetAvailableException) {
                        Log.e("Lenscap1 Network Process GameActivity", "Image not available")
                        e.printStackTrace()
                    } catch (e: FileNotFoundException) {
                        Log.e("Lenscap1 Network Process GameActivity", "File not found")
                        e.printStackTrace()
                    } catch (e: IOException) {
                        Log.e("Lenscap1 Network Process GameActivity", "IOException")
                        e.printStackTrace()
                    }*/
                    lensCap.send("NtoV", bytesForTest!!)
                    //Log.d("lenscap1 network process cameraPose to send size: ", bytesForTest!!.size.toString() + " toBytes latency cost " + (currentTimer33-currentTimer22))
                    //timerAverageCP += (currentTimer1 - (pre[1]).toLong())
                    timerAverageCP += (currentTimer33 - currentTimer22)
                    if (timerAverageCP.size > 39){
                        //timerAverageCP.drop(0);
                        //Log.d("lenscap NetworkTransceiver cameraPose latency average",timerAverage.average().toString())
                        //Log.d("lenscap1 Network Process cameraPose ToByte latency average",timerAverageCP.average().toString())
                    }
                    val strs = pre[2].substring(1, pre[2].length - 1).split(",")
                } else if (pre[0].equals("face")){
                    val currentTimer22: Long = System.nanoTime()
                    val currentDataSend = "face" + "," + currentTimer22 + "," + returns
                    bytesForTest = currentDataSend.toByteArray()
                    val currentTimer33: Long = System.nanoTime()
                    //var image: Bitmap? = null
                    /*try {
                        //image = BitmapFactory.decodeFile("/storage/emulated/0/" + "Classified.jpg")
                        //val output = FileOutputStream("/storage/emulated/0/" + "Classified123.png")
                        //image.compress(Bitmap.CompressFormat.PNG, 100, output)
                        //image = ImageIO.read()
                        //FileUploader.writeImageInformation(image, "tmp_image.png", this@GameActivity)
                        //Log.d("Lenscap1 upload face start time: ", System.currentTimeMillis().toString())
                        FileUploader.uploadFile(File("/storage/emulated/0/" + "face.txt"), this@GameActivity)
                    } catch (e: NotYetAvailableException) {
                        Log.e("Lenscap1 Network Process GameActivity", "Image not available")
                        e.printStackTrace()
                    } catch (e: FileNotFoundException) {
                        Log.e("Lenscap1 Network Process GameActivity", "File not found")
                        e.printStackTrace()
                    } catch (e: IOException) {
                        Log.e("Lenscap1 Network Process GameActivity", "IOException")
                        e.printStackTrace()
                    }*/
                    lensCap.send("NtoV", bytesForTest!!)
                    //Log.d("lenscap1 network process face to send size: ", bytesForTest!!.size.toString() + " toBytes latency cost " + (currentTimer33-currentTimer22))
                    //timerAverageEF += (currentTimer1 - (pre[1]).toLong())
                    //Log.d("lenscap1 NetworkTransceiver face latency",((currentTimer1 - (pre[1]).toLong()).toString()))
                    timerAverageEF += (currentTimer33 - currentTimer22)
                    if (timerAverageEF.size > 199){
                        timerAverageEF.drop(0);
                        //Log.d("lenscap1 NetworkTransceiver face latency average",timerAverageEF.average().toString())
                        Log.d("lenscap1 Network Process face ToByte latency average",timerAverageEF.average().toString())
                    }
                    val strs = pre[2].substring(1, pre[2].length - 1).split(",")
                    //Log.e("lenscap NetworkTransceiver latency: ", faceStart.toString())
                } else if (pre[0].equals("pointCloud")) {
                    val currentTimer22: Long = System.nanoTime()
                    val currentDataSend = "pointCloud" + "," + currentTimer22 + "," + returns
                    bytesForTest = currentDataSend.toByteArray()
                    val currentTimer33: Long = System.nanoTime()
                    var image: Bitmap? = null
                    /*try {
                        //image = BitmapFactory.decodeFile("/storage/emulated/0/" + "Classified.jpg")
                        //val output = FileOutputStream("/storage/emulated/0/" + "Classified123.png")
                        //image.compress(Bitmap.CompressFormat.PNG, 100, output)
                        //image = ImageIO.read()
                        //FileUploader.writeImageInformation(image, "tmp_image.png", this@GameActivity)
                        //Log.d("Lenscap1 upload pointcloud start time: ", System.currentTimeMillis().toString())
                        FileUploader.uploadFile(File("/storage/emulated/0/" + "pointcloud.txt"), this@GameActivity)
                    } catch (e: NotYetAvailableException) {
                        Log.e("Lenscap1 Network Process GameActivity", "Image not available")
                        e.printStackTrace()
                    } catch (e: FileNotFoundException) {
                        Log.e("Lenscap1 Network Process GameActivity", "File not found")
                        e.printStackTrace()
                    } catch (e: IOException) {
                        Log.e("Lenscap1 Network Process GameActivity", "IOException")
                        e.printStackTrace()
                    }*/
                    lensCap.send("NtoV", bytesForTest!!)
                    //Log.d("lenscap1 network process pointCloud to send size: ", bytesForTest!!.size.toString() + " toBytes latency cost " + (currentTimer33-currentTimer22))
                    //timerAveragePC += (currentTimer1 - (pre[1]).toLong())
                    timerAveragePC += (currentTimer33 - currentTimer22)
                    if (timerAveragePC.size > 39){
                        //timerAveragePC.drop(0);
                        //Log.d("lenscap NetworkTransceiver pointCloud latency average",timerAverage.average().toString())
                        //Log.d("lenscap1 Network Process pointCloud ToByte latency average",timerAveragePC.average().toString())
                    }
                    val strs = pre[2].substring(1, pre[2].length - 1).split(",")
                } else if (pre[0].equals("lightEstimation")) {
                    val currentTimer22: Long = System.nanoTime()
                    val currentDataSend = "lightEstimation" + "," + currentTimer22 + "," + returns
                    bytesForTest = currentDataSend.toByteArray()
                    val currentTimer33: Long = System.nanoTime()
                    var image: Bitmap? = null
                    /*try {
                        //image = BitmapFactory.decodeFile("/storage/emulated/0/" + "Classified.jpg")
                        //val output = FileOutputStream("/storage/emulated/0/" + "Classified123.png")
                        //image.compress(Bitmap.CompressFormat.PNG, 100, output)
                        //image = ImageIO.read()
                        //FileUploader.writeImageInformation(image, "tmp_image.png", this@GameActivity)
                        //Log.d("Lenscap1 upload light start time: ", System.currentTimeMillis().toString())
                        FileUploader.uploadFile(File("/storage/emulated/0/" + "light.txt"), this@GameActivity)
                    } catch (e: NotYetAvailableException) {
                        Log.e("Lenscap1 Network Process GameActivity", "Image not available")
                        e.printStackTrace()
                    } catch (e: FileNotFoundException) {
                        Log.e("Lenscap1 Network Process GameActivity", "File not found")
                        e.printStackTrace()
                    } catch (e: IOException) {
                        Log.e("Lenscap1 Network Process GameActivity", "IOException")
                        e.printStackTrace()
                    }*/
                    lensCap.send("NtoV", bytesForTest!!)
                    //Log.d("lenscap1 network process lightEstimation to send size: ", bytesForTest!!.size.toString() + " toBytes latency cost " + (currentTimer33-currentTimer22))
                    //timerAverageLE += (currentTimer1 - (pre[1]).toLong())
                    timerAverageLE += (currentTimer33 - currentTimer22)
                    if (timerAverageLE.size > 39){
                        //timerAverageLE.drop(0)
                        //Log.d("lenscap NetworkTransceiver lightEstimation latency average",timerAverage.average().toString())
                        //Log.d("lenscap1 Network Process lightEstimationToByte latency average",timerAverageLE.average().toString())
                    }
                    val strs = pre[2].substring(1, pre[2].length - 1).split(",")
                } else if (pre[0].equals("cameraFrame")) {
                    val currentTimer22: Long = System.nanoTime()
                    val currentDataSend = "cameraFrame" + "," + currentTimer22 + "," + returns
                    bytesForTest = currentDataSend.toByteArray()
                    val currentTimer33: Long = System.nanoTime()
                    var image: Bitmap? = null
                    /*try {
                        //image = BitmapFactory.decodeFile("/storage/emulated/0/" + "Classified.jpg")
                        //val output = FileOutputStream("/storage/emulated/0/" + "Classified123.png")
                        //image.compress(Bitmap.CompressFormat.PNG, 100, output)
                        //image = ImageIO.read()
                        //FileUploader.writeImageInformation(image, "tmp_image.png", this@GameActivity)
                        //Log.d("Lenscap1 upload frame start time: ", System.currentTimeMillis().toString())
                        FileUploader.uploadFile(File("/storage/emulated/0/" + "frame.txt"), this@GameActivity)
                    } catch (e: NotYetAvailableException) {
                        Log.e("Lenscap1 Network Process GameActivity", "Image not available")
                        e.printStackTrace()
                    } catch (e: FileNotFoundException) {
                        Log.e("Lenscap1 Network Process GameActivity", "File not found")
                        e.printStackTrace()
                    } catch (e: IOException) {
                        Log.e("Lenscap1 Network Process GameActivity", "IOException")
                        e.printStackTrace()
                    }*/
                    lensCap.send("NtoV", bytesForTest!!)
                    //Log.d("lenscap1 network process camera frame to send size: ", bytesForTest!!.size.toString() + " toBytes latency cost " + (currentTimer33-currentTimer22))
                    //timerAverageCF += (currentTimer1 - (pre[1]).toLong())
                    timerAverageCF += (currentTimer33 - currentTimer22)
                    if (timerAverageCF.size > 39){
                        //timerAverageCF.drop(0);
                        //Log.d("lenscap NetworkTransceiver Camera Frame latency average",timerAverage.average().toString())
                        //Log.d("lenscap1 Network Process Camera Frame ToByte latency average",timerAverageCF.average().toString())
                    }
                    val strs = pre[2].substring(1, pre[2].length - 1).split(",")
                } else if (pre[0].equals("lenscapCount")){
                    runOnUiThread {
                    }
                }
                //Log.e("lenscap NetworkTransceiver","Received val pre0 " + pre[0] + " pre1 " + pre[1])
                //Log.e("lenscap NetworkTransceiver","Received val returns" + returns)
                //Log.e("lenscap NetworkTransceiver","Received val strs" + strs)
            }
        })

        //val switchCamFrame:Switch = floatyView?.findViewById(R.id.switchCameraFrame) as TextView
        //myCameraFrameSwith.setOnCheckedChangeListener { _, isChecked ->
        //    if (isChecked) {
        //        // The toggle is enabled
        //    } else {
        //        myCameraFrameSwith.setText("LensCap is OFF for this feature")
        //    }
        //}

        //        lensCap.onReceiverAvailable {
        // Transfer to byte array (for transmision)

    }
    override fun onResume() {
        super.onResume()
        if (Settings.canDrawOverlays(this)) {
            // Launch service right away - the user has already previously granted permission
            addOverlayView()
            Log.d("lenscap1", "add overlay here")


        } else {

            // Check that the user has granted permission, and prompt them if not
            checkDrawOverlayPermission()
        }

    }

    //init {
    //    System.loadLibrary("gnustl_shared")
    //    System.loadLibrary("UE4")
    //}

    //open external fun nativeValidateLensCapFloatData(inTag: String?, data: FloatArray?): Boolean

    private fun checkDrawOverlayPermission() {

        // Checks if app already has permission to draw overlays
        if (!Settings.canDrawOverlays(this)) {

            // If not, form up an Intent to launch the permission request
            val intents = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))

            // Launch Intent, with the supplied request code
            startActivityForResult(intents, REQUEST_CODE)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, datas: Intent?) {

        super.onActivityResult(requestCode, resultCode, datas)

        // Check if a request code is received that matches that which we provided for the overlay draw request
        if (requestCode == REQUEST_CODE) {

            // Double-check that the user granted it, and didn't just dismiss the request
            if (Settings.canDrawOverlays(this)) {
                // Launch the service
                addOverlayView()

            } else {


            }
        }

    }
    fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }
    }

    private fun addOverlayView() {

        val params: WindowManager.LayoutParams
        val layoutParamsType: Int = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutParamsType,
                0,
                PixelFormat.TRANSLUCENT)

        params.gravity = Gravity.CENTER or Gravity.START
        params.x = 0
        params.y = 0

        val interceptorLayout = object : FrameLayout(this) {

            override fun dispatchKeyEvent(event: KeyEvent): Boolean {

                // Only fire on the ACTION_DOWN event, or you'll get two events (one for _DOWN, one for _UP)
                if (event.action == KeyEvent.ACTION_DOWN) {

                    // Check if the HOME button is pressed
                    if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                        onDestroy()
                        Log.v(TAG, "BACK Button Pressed")

                        // As we've taken action, we'll return true to prevent other apps from consuming the event as well
                        return true
                    }
                }

                // Otherwise don't intercept the event
                return super.dispatchKeyEvent(event)
            }
        }

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        floatyView = inflater.inflate(R.layout.overlayv, interceptorLayout)
        val floatyViewNT = inflater.inflate(edu.ame.asu.meteor.lenscap.networktransceiver.R.layout.overlaynt, interceptorLayout)
        lensCap.addOverlayView(floatyViewNT)
        //val myNetworkMetterFloatingButton = floatyView?.findViewById(R.id.floatingActionButton1) as FloatingActionButton
        //myNetworkMetterFloatingButton.setOnClickListener{
        //    lenscapAnalyzeTimeStamp()
        //}
        //val searchB = floatyView?.findViewById(R.id.searchBar) as SearchView
        //searchB.setQuery("Search Your Dream Car", false)

        val startbuttons =  floatyView?.findViewById(R.id.start) as Button
        //startbuttons.setVisibility(View.INVISIBLE)
        startbuttons.setOnClickListener {
            val viewy= floatyView?.findViewById(R.id.imageView) as ImageView
            startAR()
            startbuttons.setVisibility(View.INVISIBLE)
        }

        //val chartxxx: BarChart = floatyView?.findViewById(R.id.chart1) as Button
        //chartxxx

        val downldbuttons =  floatyView?.findViewById(R.id.downloadbtn) as Button
        downldbuttons.setVisibility(View.INVISIBLE)

        downldbuttons.setOnClickListener {
            /*
            when (download) {
                0 -> {
                    //lensCap.send("cameraPS", ("cameraPose" + "," + File("/storage/emulated/0/" + "pose.txt").inputStream().readBytes().toString(Charsets.UTF_8)).toByteArray())
                    val ddd = "cameraPose" + "," + File("/storage/emulated/0/" + "pose.txt").inputStream().readBytes().toString(Charsets.UTF_8)
                    lensCap.send("cameraPS", ddd.toByteArray())
                    //lensCap.send("cameraPS", File("/storage/emulated/0/" + "pose.txt").inputStream().readBytes())
                    download += 1
                }
                1 -> {
                    val ddd = "lightEstimation" + "," + File("/storage/emulated/0/" + "light.txt").inputStream().readBytes().toString(Charsets.UTF_8)
                    lensCap.send("cameraLight", ddd.toByteArray())
                    //lensCap.send("cameraLight", ("lightEstimation" + "," + File("/storage/emulated/0/" + "light.txt").inputStream().readBytes().toString(Charsets.UTF_8)).toByteArray())
                    //lensCap.send("cameraPS", File("/storage/emulated/0/" + "light.txt").inputStream().readBytes())
                    download += 1
                }
                2-> {
                    val ddd = "pointCloud" + "," + File("/storage/emulated/0/" + "pointcloud.txt").inputStream().readBytes().toString(Charsets.UTF_8)
                    lensCap.send("cameraPC", ddd.toByteArray())
                    //lensCap.send("cameraPC", ("pointCloud" + "," + File("/storage/emulated/0/" + "pointcloud.txt").inputStream().readBytes().toString(Charsets.UTF_8)).toByteArray())
                    //lensCap.send("cameraPS", File("/storage/emulated/0/" + "pointcloud.txt").inputStream().readBytes())
                    download = 0
                }
            }*/
        }
        floatyView?.let {
            it.setOnTouchListener(this)
            windowManager.addView(floatyView, params)

        } ?: run {
            Log.e(TAG, "Layout Inflater Service is null; can't inflate and display R.layout.floating_view")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        floatyView?.let {
            windowManager.removeView(it)
            floatyView = null
        }
    }
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        view.performClick()
        //val touchTimer: Long = System.currentTimeMillis()
        var x=motionEvent.x
        var y=motionEvent.y
        //Log.d("LensCap NetworkTransceiver onTouch x", x.toString())
        //Log.d("LensCap NetworkTransceiver onTouch y", y.toString())
        var held="x"+x.toString()+"y"+y.toString()
        if(motionEvent.action==MotionEvent.ACTION_DOWN )
        {
            held=held+"D"
            //val deltaTime: Long = SystemClock.elapsedRealtimeNanos()
            //s.writes("t1="+deltaTime.toString())
        }
        else if(motionEvent.action==MotionEvent.ACTION_MOVE )
        {
            held=held+"M"
        }
        else if(motionEvent.action==MotionEvent.ACTION_UP)
        {
            held=held+"U"
        }
        val touchTimer2: Long = System.currentTimeMillis()
        val boo=held + '|' + touchTimer2.toString()
        //Log.d("lenscap1 NetworkTransceiver Send: ", boo)
        try {
            bytes = boo.toByteArray()
            lensCap.send("TouchCoords", this.bytes!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val intentFilter:IntentFilter = IntentFilter().also {
            it.addAction(ACTION_SEND_MODEL)
        }
        val contentIntent = Intent()
        contentIntent.action = ACTION_SEND_MODEL
        val sendReferenceIntent = PendingIntent.getBroadcast(
                this,
                2020,
                contentIntent,
                0
        )
        sendReferenceIntent.send()
        return true
    }


    //private fun getXAxisValues(): IBarDataSet? {
    //    val xAxis = IBarDataSet<>()
    //    xAxis.add("JAN")
    //    xAxis.add("FEB")
    //    xAxis.add("MAR")
    //    xAxis.add("APR")
    //    xAxis.add("MAY")
    //    xAxis.add("JUN")
    //    return xAxis
    //}


    private fun startAR() {
        val launchIntent = packageManager.getLaunchIntentForPackage("com.meteor.ARtest_2")
        launchIntent?.let { startActivity(it) }
    }


}

