package com.example.mapandnavigations.viewmodel;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.mapandnavigations.data.RouteRepository;
import com.example.mapandnavigations.model.RoutePoint;
import com.example.mapandnavigations.navigation.PolylineDecoder;
import com.example.mapandnavigations.navigation.RouteCalculator;
import com.example.mapandnavigations.network.OSRMService;
import com.example.mapandnavigations.utils.NetworkUtils;
import org.osmdroid.util.GeoPoint;
import java.util.List;

public class MapViewModel extends AndroidViewModel {

    private List<GeoPoint> currentRoute;
    private int currentIndex = 0;
    private long lastRerouteTime = 0;
    public MutableLiveData<List<GeoPoint>> routeLiveData = new MutableLiveData<>();
    public MutableLiveData<GeoPoint> userLocation = new MutableLiveData<>();
    public MutableLiveData<String> navigationMessage = new MutableLiveData<>();
    public MutableLiveData<Double> remainingDistance = new MutableLiveData<>();


    public MapViewModel(@NonNull Application application) {
        super(application);
        remainingDistance.setValue(0.0);

    }

    public void setDestination(GeoPoint start, GeoPoint end) {

        new Thread(() -> {

            boolean internet =
                    NetworkUtils.isInternetAvailable(getApplication());

            if (internet) {
                String encoded =
                        OSRMService.fetchRoutePolyline(
                                start.getLatitude(),
                                start.getLongitude(),
                                end.getLatitude(),
                                end.getLongitude()
                        );

                if (encoded != null) {
                    List<GeoPoint> route =
                            PolylineDecoder.decode(encoded);

                    currentRoute = route;
                    currentIndex = 0;

                    routeLiveData.postValue(route);

                    RouteRepository.saveRoute(
                            getApplication(),
                            RouteCalculator.toRoutePoints(route),
                            end
                    );
                }

            } else {
                List<RoutePoint> saved =
                        RouteRepository.loadRouteIfMatches(
                                getApplication(),
                                end
                        );

                if (saved == null) {
                    navigationMessage.postValue(
                            "No offline route available for this destination"
                    );
                    routeLiveData.postValue(null);
                } else {
                    currentRoute =
                            RouteCalculator.toGeoPoints(saved);
                    currentIndex = 0;

                    routeLiveData.postValue(currentRoute);
                }
            }

        }).start();
    }

    public void updateUserLocation(GeoPoint loc) {
        userLocation.postValue(loc);
        onUserLocationChanged(loc);
        calculateRemainingDistance(loc);
    }

    private void onUserLocationChanged(GeoPoint userLoc) {

        if (currentRoute == null || currentRoute.isEmpty()) return;
        GeoPoint last = currentRoute.get(currentRoute.size() - 1);
        if (userLoc.distanceToAsDouble(last) < 20) {
            navigationMessage.postValue("You have arrived");
            currentRoute = null;
            remainingDistance.postValue(0.0);
        }

        GeoPoint targetPoint = currentRoute.get(currentIndex);

        double distance =
                userLoc.distanceToAsDouble(targetPoint);

        if (distance < 15) {
            currentIndex++;
            return;
        }

        if (distance > 40) {
            long now = System.currentTimeMillis();
            if (now - lastRerouteTime > 8000) {
                lastRerouteTime = now;
                navigationMessage.postValue("You missed the turn. Re-routing...");
                reroute(userLoc);
            }
        }
    }

    private void reroute(GeoPoint userLoc) {

        if (currentRoute == null || currentRoute.isEmpty()) return;

        GeoPoint destination =
                currentRoute.get(currentRoute.size() - 1);

        setDestination(userLoc, destination);
    }

    public void resetRouteState() {
        currentIndex = 0;
        currentRoute = null;
        remainingDistance.postValue(0.0);
    }

    private void calculateRemainingDistance(GeoPoint userLoc) {

        if (currentRoute == null || currentIndex >= currentRoute.size()) {
            remainingDistance.postValue(0.0);
            return;
        }

        double distance = 0;

        distance += userLoc.distanceToAsDouble(currentRoute.get(currentIndex));

        for (int i = currentIndex; i < currentRoute.size() - 1; i++) {
            distance += currentRoute.get(i)
                    .distanceToAsDouble(currentRoute.get(i + 1));
        }

        remainingDistance.postValue(distance);
    }
}
