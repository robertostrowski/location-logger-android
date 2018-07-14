package com.miketa.locationtracker;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap myGoogleMap;
    MapView myMapView;
    Location lastKnownLocation;
    View myView;
    List<LatLng> mMarks = new ArrayList<>();
    GMapV2DirectionAsyncTask asyncTask;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_map, container, false);
        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        myMapView = (MapView) myView.findViewById(R.id.map);
        if (myMapView != null) {
            myMapView.onCreate(null);
            myMapView.onResume();
            myMapView.getMapAsync(this);
        }

    }

    // Initializing the map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        myGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //User has previously accepted this permission
            if (ActivityCompat.checkSelfPermission(this.getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                myGoogleMap.setMyLocationEnabled(true);
            }
        } else {
            //Not in api-23, no need to prompt
            myGoogleMap.setMyLocationEnabled(true);
        }



        if(mMarks.size() > 1) {
            addMarkerOnMap((float) mMarks.get(0).latitude, (float) mMarks.get(0).longitude, "Starting position", null);
            addMarkerOnMap((float) mMarks.get(mMarks.size() - 1).latitude, (float) mMarks.get(mMarks.size() - 1).longitude, "Destination", null);


            PolylineOptions PoOp = new PolylineOptions();
            PoOp.clickable(false);
            for (LatLng mark : mMarks) {
                PoOp.add(mark);
            }

            Polyline polyline = googleMap.addPolyline(PoOp);
            polyline.setColor(Color.RED);

            CameraPosition myCamPos = CameraPosition.builder().target(mMarks.get(0)).zoom(16).bearing(0).tilt(45).build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(myCamPos));
        }
        else
        {
            if(lastKnownLocation != null) {
                addMarkerOnMap((float) mMarks.get(0).latitude, (float) mMarks.get(0).longitude, "Destination", null);
                CameraPosition myCamPos = CameraPosition.builder().target(mMarks.get(0)).zoom(16).bearing(0).tilt(45).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(myCamPos));
                route(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), new LatLng(mMarks.get(0).latitude, mMarks.get(0).longitude));
            }


        }

       // route(new LatLng(50.288517,18.677574), new LatLng(50.258517,18.677574));

    }



    protected void route(LatLng sourcePosition, LatLng destPosition)  {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    Document doc = (Document) msg.obj;
                    GMapV2Direction md = new GMapV2Direction();
                    ArrayList<LatLng> directionPoint = md.getDirection(doc);
                    PolylineOptions rectLine = new PolylineOptions().width(15).color(Color.RED);

                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    Polyline polylin = myGoogleMap.addPolyline(rectLine);
                    md.getDurationText(doc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };

        asyncTask = new GMapV2DirectionAsyncTask(handler, sourcePosition, destPosition, GMapV2Direction.MODE_WALKING);
        asyncTask.execute();
    }


    protected void AddToMarkerList(LatLng marker)
    {
        mMarks.add(marker);
    }

    protected void DeleteAllMarkers()
    {
        mMarks.clear();
    }


    // Adding new marker in a given Geoposition
    public void addMarkerOnMap(float latitude, float longitude){
        myGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)));
    }

    public void addMarkerOnMap(float latitude, float longitude, String title, String snippet){
        myGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title(title).snippet(snippet));
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //  TODO: Prompt with explanation!

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    if (ActivityCompat.checkSelfPermission(this.getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        myGoogleMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this.getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
}
