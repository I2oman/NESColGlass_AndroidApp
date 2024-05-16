package com.example.nescolglass.fragments;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.nescolglass.MainActivity;
import com.example.nescolglass.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private ConstraintLayout mainLayout;
    private GoogleMap mapFragment;
    private ConstraintLayout inProgressCover;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        mainLayout = view.findViewById(R.id.mainLayout);
        mainLayout.setOnTouchListener(this::onScrollViewTouch);

        inProgressCover = view.findViewById(R.id.inProgressCover);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapFragment = googleMap;
        mapFragment.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        ((MainActivity) getActivity()).accessPermission();
        // Enable location layer of the map
//        mapFragment.setMyLocationEnabled(true);

        // Get last known location of the device
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        fusedLocationClient.getLastLocation().addOnSuccessListener(this::onSuccess);
    }

    public void onSuccess(Location location) {
        if (location != null) {
            // Create a LatLng object from the retrieved location
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            // Add a marker to the map at the current location
//            mapFragment.addMarker(new MarkerOptions().position(currentLatLng).title("Your Current Location"));

            // Move the camera to the current location with a zoom level
            mapFragment.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12));
        }
    }

    public boolean onScrollViewTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ((MainActivity) getActivity()).viewPager2.setUserInputEnabled(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                ((MainActivity) getActivity()).viewPager2.setUserInputEnabled(false);
                break;
        }
        return false;
    }
}