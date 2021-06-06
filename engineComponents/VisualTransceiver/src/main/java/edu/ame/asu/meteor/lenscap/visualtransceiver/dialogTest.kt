package edu.ame.asu.meteor.lenscap.visualtransceiver

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment

class dialogTest : DialogFragment() {
    var permArray = arrayOf("Camera Frame", "Camera Pose","Light Estimate","Point Cloud","Face Tracking")
    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment,selected:ArrayList<Int>)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }
    internal lateinit var listener: NoticeDialogListener
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val selectedItems = ArrayList<Int>() // Where we track the selected items
            val builder = AlertDialog.Builder(it)
            // Set the dialog title
            builder.setTitle("Choose which permissions to allow")
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setMultiChoiceItems(permArray, null,
                            DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    selectedItems.add(which)
                                } else if (selectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    selectedItems.remove(Integer.valueOf(which))
                                }
                            })
                    // Set the action buttons
                    .setPositiveButton("Finish",
                            DialogInterface.OnClickListener { dialog, id ->
                                listener.onDialogPositiveClick(this,selectedItems)

                            })
                    .setNegativeButton("Cancel",
                            DialogInterface.OnClickListener { dialog, id ->
                                listener.onDialogNegativeClick(this)

                            })

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as NoticeDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }
}