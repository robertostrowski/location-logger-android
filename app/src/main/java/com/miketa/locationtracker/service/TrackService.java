package com.miketa.locationtracker.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.miketa.locationtracker.service.tasks.TrackCreator;
import com.miketa.locationtracker.service.tasks.TrackPointAddingTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import im.delight.android.location.SimpleLocation;

import static android.content.ContentValues.TAG;

public class TrackService extends Service {

    // high-level location library class for getting locations
    private SimpleLocation location;

    //container for gathered locations
    private List<TrackPoint> trackPoints = new ArrayList<>();

    private String uuid;
    private Integer newTrackId;

    // thread for a task to run asynchronously
    Thread thread;
    private Context context;

    // task to work on asynchronously
    Runnable gpsLocationCollector = new Runnable() {
        @Override
        public void run() {
            while(!thread.isInterrupted()){
                long time = System.currentTimeMillis();

                trackPoints.add(new TrackPoint(location));
                Log.d(TAG, "run: " + trackPoints.get(trackPoints.size() - 1).toJsonObject());

                while(time + 5000 > System.currentTimeMillis());
            }
        }
    };

    @Override
    public void onCreate() {
        context = this;
        location = new SimpleLocation(this, true, false, 2500);
        location.beginUpdates();
        TrackCreator trackCreator = new TrackCreator(this);
        try {
            trackCreator.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        newTrackId = trackCreator.getNewTrackId();
        uuid = trackCreator.getUuid();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Track started", Toast.LENGTH_LONG).show();
        thread = performOnBackgroundThread(gpsLocationCollector);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Track stopped", Toast.LENGTH_LONG).show();
        sendTrackPointsToActivity();
        thread.interrupt();
    }

    private void sendTrackPointsToActivity() {
//        Log.d(TAG, "sendTrackPointsToActivity: uuid: " + uuid + " newTrackId: " + newTrackId + " points: + " + getTrackPointsAsJson())
        Collections.reverse(trackPoints);
        if (uuid == null || newTrackId < 0)
            return;
        Intent intent = new Intent("Locations JSON Array");
        intent.putExtra("JSONArray", getTrackPointsAsJson());
        intent.putExtra("newTrackId", newTrackId);
        intent.putExtra("uuid", uuid);
        sendBroadcast(intent);
        trackPoints.clear();
    }

    public Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        t.start();
        return t;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String getTrackPointsAsJson() {
        JSONArray jjarr = new JSONArray();
        for (TrackPoint tp : trackPoints) {
            try {
                JSONObject tpJSON = tp.toJsonObject();
                if(!tpJSON.get("lng").toString().equals("0")){
                    jjarr.put(tpJSON);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jjarr.toString();
    }
}
