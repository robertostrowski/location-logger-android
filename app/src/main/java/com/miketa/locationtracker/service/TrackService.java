package com.miketa.locationtracker.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.miketa.locationtracker.service.tasks.TrackCreator;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import im.delight.android.location.SimpleLocation;

import static android.content.ContentValues.TAG;

public class TrackService extends Service {

    // how often points are collected in milliseconds
    private int getLocationInterval = 5000;

    // high-level location library class for getting locations
    private SimpleLocation location;

    //container for gathered locations
    private List<TrackPoint> trackPoints = new ArrayList<>();

    private Integer trackID;
    private String uuid;
    private Integer newTrackId;

    // task to work on asynchronously
    Runnable gpsLocationCollector;

    // thread for a task to run asynchronously
    Thread thread;

    @Override
    public void onCreate() {
        gpsLocationCollector = locationCollectorRunnable();
        location = new SimpleLocation(this);
        if (!location.hasLocationEnabled()) {
            SimpleLocation.openSettings(this);
        }
        TrackCreator trackCreator = new TrackCreator(this);
        try {
            trackCreator.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        newTrackId = trackCreator.getNewTrackId();
        uuid = trackCreator.getUuid();
        Log.d(TAG, "onCreate: " + uuid);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        thread = performOnBackgroundThread(gpsLocationCollector);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Track stopped", Toast.LENGTH_LONG).show();
        thread.interrupt();
    }

    Runnable locationCollectorRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    trackPoints.add(new TrackPoint(location));
                    sendTrackPointsToActivity();
                    //wait some time
                    long tmp = System.currentTimeMillis();
                    while (tmp + 5000 > System.currentTimeMillis()) ;
                }
            }
        };
    }

    private void sendTrackPointsToActivity(){
        if (uuid == null || newTrackId < 0)
            return;
        Intent intent = new Intent("Locations JSON Array");
        intent.putExtra("JSONArray", getTrackPointsAsJson());
        intent.putExtra("newTrackId", newTrackId);
        intent.putExtra("uuid", uuid);
        trackPoints.clear();
        sendBroadcast(intent);
    }

    public Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {
                    sendTrackPointsToActivity();
                }
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
        for(TrackPoint tp : trackPoints) {
            jjarr.put(tp.toJsonObject());
        }
        return jjarr.toString();
    }
}
