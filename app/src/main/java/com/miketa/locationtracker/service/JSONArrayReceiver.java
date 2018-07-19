package com.miketa.locationtracker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.miketa.locationtracker.service.tasks.TrackPointAddingTask;

import static android.content.ContentValues.TAG;

public class JSONArrayReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String points = intent.getStringExtra("JSONArray");
        Integer newTrackId = intent.getIntExtra("newTrackId", -1);
        String uuid = intent.getStringExtra("uuid");
        new TrackPointAddingTask(points, newTrackId, uuid, context).execute();
    }
}
