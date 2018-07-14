package com.miketa.locationtracker.service.tasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TrackPointBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String points = intent.getStringExtra("JSONArray");
        String uuid = intent.getStringExtra("uuid");
        Integer newTrackId = intent.getIntExtra("newTrackId", -1);
        new TrackPointAddingTask(points, newTrackId, uuid, context).execute();
    }
}
