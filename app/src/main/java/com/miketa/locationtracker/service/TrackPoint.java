package com.miketa.locationtracker.service;

import org.json.JSONException;
import org.json.JSONObject;

import im.delight.android.location.SimpleLocation;

class TrackPoint {
    private float timestamp;
    private float longitude;
    private float latitude;
    private float altitude;
    private float speed;

    public TrackPoint(SimpleLocation location) {
        this.longitude = (float) location.getLongitude();
        this.latitude = (float) location.getLatitude();
        this.altitude = (float) location.getAltitude();
        this.speed = location.getSpeed();
        this.timestamp = System.currentTimeMillis();
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
