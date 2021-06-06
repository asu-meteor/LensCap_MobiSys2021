package edu.ame.asu.meteor.lenscap.visualtransceiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static edu.ame.asu.meteor.lenscap.visualtransceiver.VisualLensCapTransceiver.CHANNEL_CAMERA_FRAME_SERVICE;
import static edu.ame.asu.meteor.lenscap.visualtransceiver.VisualLensCapTransceiver.CHANNEL_CAMERA_POSE_SERVICE;
import static edu.ame.asu.meteor.lenscap.visualtransceiver.VisualLensCapTransceiver.CHANNEL_FACE_TRACK_SERVICE;
import static edu.ame.asu.meteor.lenscap.visualtransceiver.VisualLensCapTransceiver.CHANNEL_LIGHT_ESTIMATE_SERVICE;
import static edu.ame.asu.meteor.lenscap.visualtransceiver.VisualLensCapTransceiver.CHANNEL_POINT_CLOUD_SERVICE;
import static edu.ame.asu.meteor.lenscap.visualtransceiver.VisualLensCapTransceiver.CHANNEL_VISUAL_SERVICE;


public class dialogDemo extends AppCompatActivity implements dialogTest.NoticeDialogListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.popupwindow);

        showEditDialog();
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment test = new dialogTest();

        test.show(fm, "fragment_edit_name");

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, ArrayList<Integer> selected) {
        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        finish();
    }

}