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

    public MutableLiveData<List<GeoPoint>> routeLiveData = new MutableLiveData<>();

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
                    routeLiveData.postValue(null); // 🚫 NO ROUTE
                } else {
                    routeLiveData.postValue(
                            RouteCalculator.toGeoPoints(saved)
                    );
                }
            }

        }).start();
    }

}
