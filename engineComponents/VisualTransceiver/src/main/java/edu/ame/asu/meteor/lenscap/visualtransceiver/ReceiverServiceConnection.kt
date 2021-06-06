package edu.ame.asu.meteor.lenscap.visualtransceiver

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import edu.ame.asu.meteor.lenscap.transceiver.IReceiverService
import timber.log.Timber

class ReceiverServiceConnection(private val onServiceConnectedEmitter:(IReceiverService)->Unit): ServiceConnection{
    var receiverService: IReceiverService? = null
    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        Timber.d("Service connected.")
        receiverService = IReceiverService.Stub.asInterface(service)
        if(receiverService != null) {
            onServiceConnectedEmitter(receiverService!!)
        }
    }

    override fun onServiceDisconnected(name: ComponentName) {
        Timber.d("Service disconnected.")
    }
}