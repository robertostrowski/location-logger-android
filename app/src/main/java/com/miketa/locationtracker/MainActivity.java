package com.miketa.locationtracker;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.miketa.locationtracker.service.TrackService;
import com.miketa.locationtracker.service.TrackCollectorManager;
import com.miketa.locationtracker.service.tasks.TrackCreator;
import com.miketa.locationtracker.service.tasks.TrackPointAddingTask;
import com.miketa.locationtracker.service.tasks.TrackPointBroadcastReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RouteFragment.OnListFragmentInteractionListener, DeviceFragment.OnListFragmentInteractionListener,
        AddDeviceFragment.OnFragmentInteractionListener, RouteDetailsFragment.OnFragmentInteractionListener, ChangeRouteNameDialogFragment.ChangeRouteNameDialogListener {


    private UserTask mUserTask = null;
    private MapFragment myMap;
    private boolean serviceRunning = false;
    private BroadcastReceiver broadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String points = intent.getStringExtra("JSONArray");
            String uuid = intent.getStringExtra("uuid");
            Integer newTrackId = intent.getIntExtra("newTrackId", -1);
            new TrackPointAddingTask(points, newTrackId, uuid, context).execute();
        }
    };
    private IntentFilter filter = new IntentFilter("Locations JSON Array");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFloatingButtonClick();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Create MapFragment and add it to main panel
        myMap = new MapFragment();
        //Starting fragment is routes fragment
        mUserTask = new UserTask(1, 0, this, 0, null, null);
        mUserTask.execute((Void) null);
    }

    void handleFloatingButtonClick(){
        if (!serviceRunning) {
            startService(new Intent(getBaseContext(), TrackService.class));
            serviceRunning = !serviceRunning;
        } else {
            stopService(new Intent(getBaseContext(), TrackService.class));
            serviceRunning = !serviceRunning;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

       // FragmentManager fragmentManager = getSupportFragmentManager();
      //  FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        int id = item.getItemId();

        if (id == R.id.nav_add_device) {
            AppCompatActivity activity = (AppCompatActivity) this;
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            AddDeviceFragment fragment = AddDeviceFragment.newInstance(this);
            fragmentTransaction.replace(R.id.routesHolder, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_del_device) {
            mUserTask = new UserTask(2, 0, this, 0, null, null);
            mUserTask.execute((Void) null);
        } else if (id == R.id.nav_manage_route) {
            mUserTask = new UserTask(1, 0, this, 0, null, null);
            mUserTask.execute((Void) null);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addMapFragment(){
        // create fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        // create transaction that will add the fragment to the activity
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // add the fragment to frameLayout specified by name
        fragmentTransaction.add(R.id.routesHolder,myMap);
        // end transaction
        fragmentTransaction.commit();

    }

    public void AddMarksToMap(String queryResult)
    {
        try {
            JSONObject jsonObject_1 = new JSONObject(queryResult);

            JSONArray jsonArray = jsonObject_1.getJSONArray("points");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                myMap.AddToMarkerList(new LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("lng")));
            }
        }
        catch(Exception e)
        {

        }
    }

    public void AddMarkToMap(String queryResult)
    {
        try{
        JSONObject jsonObject = new JSONObject(queryResult);
        myMap.AddToMarkerList(new LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("lng")));
        }
        catch(Exception e)
        {

        }
    }



    public void onListFragmentInteraction(Routes item)
    {
        mUserTask = new UserTask(6, 0, this, Integer.parseInt(item.id), null, null);
        mUserTask.execute((Void) null);
    }

    public void onListFragmentInteraction(int ID)
    {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    myMap.DeleteAllMarkers();
                    AddMarksToMap((String) msg.obj);
                    addMapFragment();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };
        UserTask mUserTask = new UserTask(6, 0, getApplicationContext(), ID, null, handler);
        mUserTask.execute((Void) null);
    }

    public void onListFragmentInteraction(Devices item)
    {

    }

    public void onFragmentInteraction(Uri uri)
    {

    }

    public void onRouteDetailsFragmentInteraction(Uri uri)
    {

    }

    public void onNavigateImageButtonInteraction(int ID)
    {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    myMap.DeleteAllMarkers();
                    AddMarkToMap((String) msg.obj);
                    addMapFragment();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };
        getDeviceLocation();
        UserTask mUserTask = new UserTask(7, 0, getApplicationContext(), ID, null, handler);
        mUserTask.execute((Void) null);
    }

    public void onDialogPositiveClick(DialogFragment dialog, String name)
    {
        try {
            JSONObject jsonObj = new JSONObject(dialog.getArguments().getString("wholeJson"));
            jsonObj.put("name", name);
            mUserTask = new UserTask(8, 0, this, dialog.getArguments().getInt("ID"), jsonObj.toString(), null);
            mUserTask.execute((Void) null);
        }
        catch(JSONException e)
        {

        }
    }

    public void onDialogNegativeClick(DialogFragment dialog)
    {

    }

    public void onChangeNameButtonInteraction(int ID, String wholeJson)
    {
        DialogFragment dialog = new ChangeRouteNameDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("ID", ID);
        bundle.putString("wholeJson", wholeJson);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "ChangeRouteNameDialogFragment");
    }

    protected void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            Task locationResult = LocationServices.getFusedLocationProviderClient(this).getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        if(task.getResult() != null)
                            myMap.lastKnownLocation = (Location)task.getResult();
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                    }
                }
            });
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
        catch(Exception e)
        {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcast, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcast);
    }
}


