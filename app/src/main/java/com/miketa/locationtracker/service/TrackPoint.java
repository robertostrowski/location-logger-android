package com.miketa.locationtracker.service;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import im.delight.android.location.SimpleLocation;

import static android.content.ContentValues.TAG;

class TrackPoint {

    private long timestamp;
    private float longitude;
    private float latitude;
    private float altitude;
    private float speed;

    public TrackPoint(SimpleLocation location) {
        this.longitude = (float) location.getLongitude();
        this.latitude = (float) location.getLatitude();
        this.altitude = (float) location.getAltitude();
        this.speed = location.getSpeed() * 3.6f; //m/s to km/h
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        Date date = cal.getTime();
        this.timestamp = date.getTime();
    }

    JSONObject toJsonObject(){
        JSONObject json = new JSONObject();
        try {
            json.put("timestamp", timestamp)
                    .put("lat", latitude)
                    .put("lng", longitude)
                    .put("altitude", altitude)
                    .put("speed", speed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
