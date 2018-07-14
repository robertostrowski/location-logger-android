package com.miketa.locationtracker.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.miketa.locationtracker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class TrackCollectorManager extends AsyncTask<Void, Void, Void> {

    private Context context;
    private String points;
    private int newTrackId;
    private String uuid;
    private int userId;

     // "b9080336-776a-466a-9dbb-c76ace43048b"

    public TrackCollectorManager(Context context, String points){
        this.context = context;
        this.points = points;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            getUuid();
            if(uuid == null) {
                return null;
            }
            getUserId();
            addNewTrack();
            sendPoints();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getUserId() throws IOException, JSONException {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String token = sharedPref.getString(context.getString(R.string.token_value), null);

        String url = "https://api-locationtracker.herokuapp.com/api/v1/user/me";

        OkHttpClient client = new OkHttpClient();
        Request request;
        request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .addHeader("Authorization", token)
                .build();
        Response response = client.newCall(request).execute();

        JSONObject json = new JSONObject(response.body().string());
        //System.out.println(response.body().string());
        userId = json.getInt("id");
    }

    private void getUuid() throws IOException, JSONException {
        String deviceName = "Nexus 5X";
        //String deviceName = android.os.Build.MODEL;
        System.out.println("device name: " + android.os.Build.MODEL);

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String token = sharedPref.getString(context.getString(R.string.token_value), null);

        String url = "https://api-locationtracker.herokuapp.com/api/v1/devices/";

        OkHttpClient client = new OkHttpClient();
        Request request;
        request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .addHeader("Authorization", token)
                .build();
        Response response = client.newCall(request).execute();
        JSONArray devices = new JSONArray(response.body().string());
        for(int i = 0 ; i < devices.length(); i++){
            JSONObject jsn = new JSONObject(devices.get(i).toString());
            String name = jsn.getString("name");
            if (name.equalsIgnoreCase(deviceName)){
                this.uuid = jsn.get("uuid").toString();
                return;
            }
        }
    }

    public void addNewTrack() throws IOException, JSONException {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String token = sharedPref.getString(context.getString(R.string.token_value), null);

        // url to request to
        String URI = "https://api-locationtracker.herokuapp.com/api/v1/track/";

        MediaType mediaType = MediaType.parse("application/json");

        // create request body
        String trackName = "Track: " + new SimpleDateFormat("yyyy:MM:dd: HH:mm:ss").format(Calendar.getInstance().getTime());
        JSONObject json = new JSONObject();
        json.put("name", trackName);
        json.put("userId", userId);
        RequestBody postBody = RequestBody.create(mediaType, json.toString());

        // create and execute request
        OkHttpClient client = new OkHttpClient();
        Request request;
        request = new Request.Builder()
                .url(URI)
                .post(postBody)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .addHeader("Authorization", token)
                .build();
        Response response = client.newCall(request).execute();

        // get id of the created track and device id
        JSONObject responseObj = new JSONObject(response.body().string());
        this.newTrackId = Integer.valueOf(responseObj.get("id").toString());
    }

    public void sendPoints() throws IOException, JSONException {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String token = sharedPref.getString(context.getString(R.string.token_value), null);

        String URI = "https://api-locationtracker.herokuapp.com/api/v1/track/" + newTrackId + "/points";

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, points);

        System.out.println("body: " + points);

        OkHttpClient client = new OkHttpClient();
        Request request;
        request = new Request.Builder()
                .url(URI)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .addHeader("Authorization", token)
                .addHeader("Device-uuid", uuid)
                .build();
        Response response = client.newCall(request).execute();
        response.body().string();
    }
}
