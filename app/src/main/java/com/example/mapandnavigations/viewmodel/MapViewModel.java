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
    public MutableLiveData<List<GeoPoint>> routeLiveData = new MutableLiveData<>();
    public MutableLiveData<GeoPoint> userLocation = new MutableLiveData<>();
    public MutableLiveData<String> navigationMessage = new MutableLiveData<>();

    public MapViewModel(@NonNull Application application) {
        super(application);
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
    }

    private void onUserLocationChanged(GeoPoint userLoc) {

        if (currentRoute == null || currentRoute.isEmpty()) return;
        if (currentIndex >= currentRoute.size()) {
            navigationMessage.postValue("Destination reached 🎉");
            return;
        }

        GeoPoint targetPoint = currentRoute.get(currentIndex);

        double distance =
                userLoc.distanceToAsDouble(targetPoint);

        if (distance < 15) {
            currentIndex++;
            return;
        }

        if (distance > 40) {
            navigationMessage.postValue(
                    "You missed the turn. Re-routing..."
            );
            reroute(userLoc);
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
    }
}
