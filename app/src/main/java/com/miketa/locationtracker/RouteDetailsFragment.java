package com.miketa.locationtracker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;


public class RouteDetailsFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    public RouteDetailsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RouteDetailsFragment newInstance(String response) {
        RouteDetailsFragment fragment = new RouteDetailsFragment();;
        fragment.setArguments(GetBundleFromJson(response));
        return fragment;
    }


    public static Bundle GetBundleFromJson(String response)
    {
        Bundle args = new Bundle();
        try {
            JSONObject jsonObject = new JSONObject(response);
            args.putString("distance", jsonObject.getString("distance"));
            args.putString("duration", jsonObject.getString("duration"));
            args.putString("maxSpeed", jsonObject.getString("maxSpeed"));
            args.putString("avgSpeed", jsonObject.getString("avgSpeed"));
            args.putString("maxAltitude", jsonObject.getString("maxAltitude"));
            args.putString("avgAltitude", jsonObject.getString("avgAltitude"));
            args.putString("minAltitude", jsonObject.getString("minAltitude"));
            }
        catch(Exception e)
        {
        }
        return args;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route_details, container, false);

        Bundle bundle = getArguments();
        TextView distance = (TextView) view.findViewById(R.id.distanceValue_textview);
        distance.setText(bundle.getString("distance"));
        TextView duration = (TextView) view.findViewById(R.id.durationValue_textview);
        duration.setText(bundle.getString("duration"));
        TextView maxSpeed = (TextView) view.findViewById(R.id.maxSpeedValue_textview);
        maxSpeed.setText(bundle.getString("maxSpeed"));
        TextView avgSpeed = (TextView) view.findViewById(R.id.avgSpeedValue_textview);
        avgSpeed.setText(bundle.getString("avgSpeed"));
        TextView minAltitude = (TextView) view.findViewById(R.id.minAltitudeValue_textview);
        minAltitude.setText(bundle.getString("minAltitude"));
        TextView maxAltitude = (TextView) view.findViewById(R.id.maxAltitudeValue_textview);
        maxAltitude.setText(bundle.getString("maxAltitude"));
        TextView avgAltitude = (TextView) view.findViewById(R.id.avgAltitudeValue_textview);
        avgAltitude.setText(bundle.getString("avgAltitude"));

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onRouteDetailsFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRouteDetailsFragmentInteraction(Uri uri);
    }
}
