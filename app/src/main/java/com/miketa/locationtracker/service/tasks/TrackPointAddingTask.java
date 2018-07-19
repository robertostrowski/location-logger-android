package com.miketa.locationtracker.service.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.miketa.locationtracker.R;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class TrackPointAddingTask extends AsyncTask<Void, Void, Void>
{
    private String points;
    private Context context;
    private Integer newTrackId;
    private String uuid;

    public TrackPointAddingTask(String points, Integer newTrackId, String uuid, Context context) {
        this.points = points;
        this.newTrackId = newTrackId;
        this.uuid = uuid;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Log.d(TAG, "doInBackground: hello");
            sendPoints();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendPoints() throws IOException, JSONException {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String token = sharedPref.getString(context.getString(R.string.token_value), null);

        String URI = "https://api-locationtracker.herokuapp.com/api/v1/track/" + newTrackId + "/points";
//        String URI = "157.158.201.204:9090";

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, points);

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
        Log.d("post on /track/" + newTrackId + "/points", points);

    }
}
