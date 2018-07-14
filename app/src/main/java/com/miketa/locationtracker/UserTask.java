package com.miketa.locationtracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Mietek on 2018-05-21.
 */

public class UserTask extends AsyncTask<Void, Void, String> {
    private final int mQueryID;
    private final int mTrackID;
    private final Context mContext;
    private final int mID;
    private final String mPostBody;
    private final Handler mHandler;


    UserTask(int queryID, int trackID, Context context, int id, String postBody, Handler handler) {
        mQueryID = queryID;
        mTrackID = trackID;
        mContext = context;
        mID = id;
        mPostBody = postBody;
        mHandler = handler;

    }

    @Override
    protected String doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.

        switch (mQueryID) {
            case 1:
                return GetTrackList();
            case 2:
                return GetDeviceList();
            case 3:
                return GetDeleteDeviceInfo(mID);
            case 4:
                return AddDevice();
            case 5:
                return GetDeleteRouteInfo(mID);
            case 6:
                return GetTrackPoints(mID);
            case 7:
                return GetLastRoutePoint(mID);
            case 8:
                return ChangeRouteName(mID);
        }
        return null;
    }

    @Override
    protected void onPostExecute(final String response) {
        switch(mQueryID)
        {
            case 1:
                AppCompatActivity activity = (AppCompatActivity) mContext;
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                RouteFragment fragment = RouteFragment.newInstance(1, response);
                fragmentTransaction.replace(R.id.routesHolder, fragment);
                fragmentTransaction.commit();
                break;
            case 2:
                AppCompatActivity activity_2 = (AppCompatActivity) mContext;
                FragmentManager fragmentManager_2 = activity_2.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction_2 = fragmentManager_2.beginTransaction();
                DeviceFragment fragment_2 = DeviceFragment.newInstance(1, response);
                fragmentTransaction_2.replace(R.id.routesHolder, fragment_2);
                fragmentTransaction_2.commit();
                break;
            case 3:
                String text = response;
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(mContext, text, duration).show();
                break;
            case 4:
                String text_4 = response;
                int duration_4 = Toast.LENGTH_SHORT;
                Toast.makeText(mContext, text_4, duration_4).show();
                break;
            case 5:
                String text_5 = response;
                int duration_5 = Toast.LENGTH_SHORT;
                Toast.makeText(mContext, text_5, duration_5).show();
                break;
            case 6:
                if(mHandler != null) {
                    Message message = new Message();
                    message.obj = response;
                    mHandler.dispatchMessage(message);
                }
                else
                {
                    AppCompatActivity activity_3 = (AppCompatActivity) mContext;
                    FragmentManager fragmentManager_3 = activity_3.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction_3 = fragmentManager_3.beginTransaction();
                    RouteDetailsFragment fragment_3 = RouteDetailsFragment.newInstance(response);
                    fragmentTransaction_3.replace(R.id.routesHolder, fragment_3);
                    fragmentTransaction_3.commit();
                }
                break;
            case 7:
                if(mHandler != null) {
                    Message message = new Message();
                    message.obj = response;
                    mHandler.dispatchMessage(message);
                }
                break;
            case 8:
                String text_6 = response;
                int duration_6 = Toast.LENGTH_SHORT;
                Toast.makeText(mContext, text_6, duration_6).show();
                break;
        }
    }


    protected String GetLastRoutePoint(int ID)
    {
        try {
            SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String token = sharedPref.getString(mContext.getString(R.string.token_value), null);

            String URI = new String("https://api-locationtracker.herokuapp.com/api/v1/track/");
            URI += Integer.toString(ID);
            URI += "/navigate";

            OkHttpClient client = new OkHttpClient();
            Request request;
            request = new Request.Builder()
                    .url(URI)
                    .get()
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Authorization", token)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (java.io.IOException e) {
            return null;
        }
    }

    protected String ChangeRouteName(int ID)
    {
        try {
            SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String token = sharedPref.getString(mContext.getString(R.string.token_value), null);

            String URI = new String("https://api-locationtracker.herokuapp.com/api/v1/track/");
            URI += Integer.toString(ID);

            OkHttpClient client = new OkHttpClient();
            Request request;
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, mPostBody);
            request = new Request.Builder()
                    .url(URI)
                    .put(body)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Authorization", token)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (java.io.IOException e) {
            return null;
        }
    }



    protected String GetDeleteDeviceInfo(int ID)
    {
        try {
            SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String token = sharedPref.getString(mContext.getString(R.string.token_value), null);

            String URI = new String("https://api-locationtracker.herokuapp.com/api/v1/device/");
            URI += Integer.toString(ID);
            OkHttpClient client = new OkHttpClient();
            Request request;
            request = new Request.Builder()
                    .url(URI)
                    .delete()
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Authorization", token)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (java.io.IOException e) {
            return e.getMessage();
        }
    }

    protected String GetDeleteRouteInfo(int ID)
    {
        try {
            SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String token = sharedPref.getString(mContext.getString(R.string.token_value), null);

            String URI = new String("https://api-locationtracker.herokuapp.com/api/v1/track/");
            URI += Integer.toString(ID);
            OkHttpClient client = new OkHttpClient();
            Request request;
            request = new Request.Builder()
                    .url(URI)
                    .delete()
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Authorization", token)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (java.io.IOException e) {
            return e.getMessage();
        }
    }

    protected String AddDevice()
    {
        try {
            SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String token = sharedPref.getString(mContext.getString(R.string.token_value), null);

            OkHttpClient client = new OkHttpClient();
            Request request;
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, mPostBody);
            request = new Request.Builder()
                    .url("https://api-locationtracker.herokuapp.com/api/v1/devices")
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Authorization", token)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (java.io.IOException e) {
            return null;
        }
    }

    protected String GetTrackList() {
        try {
            SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String token = sharedPref.getString(mContext.getString(R.string.token_value), null);

            OkHttpClient client = new OkHttpClient();
            Request request;
            request = new Request.Builder()
                    .url("https://api-locationtracker.herokuapp.com/api/v1/tracks")
                    .get()
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Authorization", token)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (java.io.IOException e) {
            return null;
        }
    }

    protected String GetTrackPoints(int ID){
        try {
            SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String token = sharedPref.getString(mContext.getString(R.string.token_value), null);

            String URI = new String("https://api-locationtracker.herokuapp.com/api/v1/track/");
            URI += Integer.toString(ID);

            OkHttpClient client = new OkHttpClient();
            Request request;
            request = new Request.Builder()
                    .url(URI)
                    .get()
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Authorization", token)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (java.io.IOException e) {
            return null;
        }
    }

    protected String GetDeviceList() {
        try {
            SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String token = sharedPref.getString(mContext.getString(R.string.token_value), null);

            OkHttpClient client = new OkHttpClient();
            Request request;
            request = new Request.Builder()
                    .url("https://api-locationtracker.herokuapp.com/api/v1/devices")
                    .get()
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Authorization", token)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            return null;
        }
    }
}