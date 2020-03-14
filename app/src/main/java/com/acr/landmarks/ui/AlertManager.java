package com.acr.landmarks.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.acr.landmarks.R;

import static com.acr.landmarks.ui.RequestCodes.PERMISSIONS_REQUEST_ENABLE_GPS;

public class AlertManager {

    public static void buildAlertMessageNoGps(Activity activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(R.string.alert_no_gps_message))
                .setCancelable(false)
                .setPositiveButton(R.string.alert_yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public static void buildAlertMessageConnectionLost(Activity activity) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(activity.getString(R.string.alert_los_title));
        alertDialog.setMessage(activity.getString(R.string.alert_los_message));
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(R.string.alert_los_continue), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, activity.getString(R.string.alert_los_continue), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                activity.startActivity(intent);
            }
        });
        alertDialog.show();
    }
}
