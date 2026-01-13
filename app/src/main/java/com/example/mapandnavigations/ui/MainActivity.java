package com.example.mapandnavigations.ui;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.mapandnavigations.R;
import com.example.mapandnavigations.network.NominatimService;
import com.example.mapandnavigations.viewmodel.MapViewModel;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 100;

    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;
    private Marker destinationMarker;

    private MapViewModel mapViewModel;
    private Polyline currentRouteLine;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_main);

        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        setupMap();
        checkLocationEnabled();
        requestLocationPermissions();
        enableDestinationTap();

        observeRoute();

        TextView tvDistance = findViewById(R.id.tvDistance);

        mapViewModel.remainingDistance.observe(this, distance -> {

            if (distance == null) return;

            if (distance < 1000) {
                tvDistance.setText("Remaining: " + distance.intValue() + " m");
            } else {
                tvDistance.setText(
                        String.format("Remaining: %.2f km", distance / 1000)
                );
            }
        });


        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                new Thread(() -> {
                    GeoPoint userLoc = locationOverlay.getMyLocation();
                    if (userLoc == null) return;

                    GeoPoint point =
                            NominatimService.searchPlace(query, userLoc);

                    runOnUiThread(() -> {
                        if (point != null) {
                            setDestination(point);
                        } else {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Location not found",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
                }).start();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        locationOverlay.runOnFirstFix(() -> {

            new Thread(() -> {
                while (true) {
                    GeoPoint userLoc = locationOverlay.getMyLocation();
                    if (userLoc != null) {
                        mapViewModel.updateUserLocation(userLoc);
                    }
                    try { Thread.sleep(2000); } catch (Exception e) {}
                }
            }).start();
        });

        mapViewModel.navigationMessage.observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void setupMap() {
        mapView = findViewById(R.id.map);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(
                new GeoPoint(24.8607, 67.0011)
        );
    }

    private void setupLocationOverlay() {
        GpsMyLocationProvider provider =
                new GpsMyLocationProvider(this);

        locationOverlay =
                new MyLocationNewOverlay(provider, mapView);

        locationOverlay.enableMyLocation();
        locationOverlay.enableFollowLocation();
        locationOverlay.setDrawAccuracyEnabled(true);

        mapView.getOverlays().add(locationOverlay);
    }

    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

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
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults
        );

        if (requestCode == LOCATION_PERMISSION_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            setupLocationOverlay();
        }
    }

    private void checkLocationEnabled() {
        LocationManager lm =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertDialog.Builder(this)
                    .setTitle("Location Required")
                    .setMessage("Please turn on location services")
                    .setCancelable(false)
                    .setPositiveButton("Turn On", (d, w) ->
                            startActivity(
                                    new Intent(
                                            Settings.ACTION_LOCATION_SOURCE_SETTINGS
                                    )
                            )
                    )
                    .show();
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

        mapView.getOverlays().add(new MapEventsOverlay(receiver));
    }

    private void setDestination(GeoPoint destinationPoint) {

        if (locationOverlay == null ||
                locationOverlay.getMyLocation() == null) return;

        GeoPoint startPoint = locationOverlay.getMyLocation();

        if (destinationMarker != null) {
            mapView.getOverlays().remove(destinationMarker);
        }

        if (currentRouteLine != null) {
            mapView.getOverlays().remove(currentRouteLine);
            currentRouteLine = null;
        }

        mapViewModel.resetRouteState();

        destinationMarker = new Marker(mapView);
        destinationMarker.setPosition(destinationPoint);
        destinationMarker.setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
        );
        destinationMarker.setTitle("Destination");

        mapView.getOverlays().add(destinationMarker);

        mapViewModel.setDestination(startPoint, destinationPoint);
    }


    private void observeRoute() {
        mapViewModel.routeLiveData.observe(this, route -> {

            if (route == null) {
                Toast.makeText(
                        this,
                        "No internet connection. Route not available offline.",
                        Toast.LENGTH_LONG
                ).show();
                return;
            }

            if (route.isEmpty()) return;

            drawRoute(route);
        });
    }

    private void drawRoute(List<GeoPoint> points) {
        if (currentRouteLine != null) {
            mapView.getOverlays().remove(currentRouteLine);
        }
        Polyline polyline = new Polyline();
        polyline.setPoints(points);
        polyline.setWidth(8f);

        currentRouteLine = polyline;

        mapView.getOverlays().add(polyline);
        mapView.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
