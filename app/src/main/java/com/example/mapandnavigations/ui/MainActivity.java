package com.example.mapandnavigations.ui;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.mapandnavigations.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.views.overlay.MapEventsOverlay;
import com.example.mapandnavigations.navigation.RouteManager;
import com.example.mapandnavigations.model.RoutePoint;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;

    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private GeoPoint destinationPoint;
    private Marker destinationMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_main);

        checkLocationEnabled();

        mapView = findViewById(R.id.map);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(new GeoPoint(24.8607, 67.0011));

        requestLocationPermissions();
        enableDestinationTap();
    }

    private void setupLocationOverlay() {
        GpsMyLocationProvider provider = new GpsMyLocationProvider(this);
        provider.addLocationSource("gps");
        provider.addLocationSource("network");

        locationOverlay = new MyLocationNewOverlay(provider, mapView);
        locationOverlay.enableMyLocation();
        locationOverlay.enableFollowLocation();
        locationOverlay.setDrawAccuracyEnabled(true);

        mapView.getOverlays().add(locationOverlay);
        locationOverlay.runOnFirstFix(() -> {

            runOnUiThread(() -> {

                GeoPoint userPoint = locationOverlay.getMyLocation();
                if (userPoint == null || destinationPoint == null) return;

                float[] results = new float[1];

                android.location.Location.distanceBetween(
                        userPoint.getLatitude(),
                        userPoint.getLongitude(),
                        destinationPoint.getLatitude(),
                        destinationPoint.getLongitude(),
                        results
                );

                float distanceMeters = results[0];


                destinationMarker.setSnippet(
                        "Distance: " + (int) distanceMeters + " m"
                );

                mapView.invalidate();
            });
        });

    }

    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST
            );
        } else {
            setupLocationOverlay();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            setupLocationOverlay();
        }
    }

    private void checkLocationEnabled() {
        LocationManager locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean gpsEnabled =
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            new AlertDialog.Builder(this)
                    .setTitle("Location Required")
                    .setMessage("Please turn on location services to continue")
                    .setCancelable(false)
                    .setPositiveButton("Turn On", (dialog, which) -> {
                        startActivity(
                                new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        );
                    })
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (locationOverlay != null) {
            locationOverlay.enableMyLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        if (locationOverlay != null) {
            locationOverlay.disableMyLocation();
        }
    }

    private void enableDestinationTap() {

        MapEventsReceiver receiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {

                setDestination(p);
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };

        MapEventsOverlay eventsOverlay = new MapEventsOverlay(receiver);
        mapView.getOverlays().add(eventsOverlay);
    }

    private void setDestination(GeoPoint point) {

        destinationPoint = point;

        if (destinationMarker != null) {
            mapView.getOverlays().remove(destinationMarker);
        }

        destinationMarker = new Marker(mapView);
        destinationMarker.setPosition(point);
        destinationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        destinationMarker.setTitle("Destination");

        mapView.getOverlays().add(destinationMarker);

//        saveDemoRoute();
        testRoute();

    }

    private void saveDemoRoute(GeoPoint start, GeoPoint end) {

        List<RoutePoint> route = new ArrayList<>();

        route.add(new RoutePoint(start.getLatitude(), start.getLongitude()));
        route.add(new RoutePoint(end.getLatitude(), end.getLongitude()));

        RouteManager.saveRoute(this, route);
    }

    private void drawRoute(List<GeoPoint> points) {
        Polyline routeLine = new Polyline();
        routeLine.setPoints(points);
        routeLine.setWidth(8f);

        mapView.getOverlays().add(routeLine);
        mapView.invalidate();
    }


    private void testRoute() {
        List<GeoPoint> testPoints = new ArrayList<>();

        testPoints.add(new GeoPoint(24.8607, 67.0011));
        testPoints.add(new GeoPoint(24.8620, 67.0030));
        testPoints.add(new GeoPoint(24.8640, 67.0055));
        testPoints.add(destinationPoint);

        drawRoute(testPoints);
    }



}
