package com.example.mapandnavigations.viewmodel;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.mapandnavigations.data.RouteRepository;
import com.example.mapandnavigations.model.RoutePoint;
import com.example.mapandnavigations.navigation.RouteCalculator;

import org.osmdroid.util.GeoPoint;

import java.util.List;

public class MapViewModel extends AndroidViewModel {

    public MutableLiveData<List<GeoPoint>> routeLiveData = new MutableLiveData<>();

    public MapViewModel(@NonNull Application application) {
        super(application);
    }

    public void setDestination(GeoPoint start, GeoPoint end) {
        List<GeoPoint> route = RouteCalculator.demoRoute(start, end);
        routeLiveData.setValue(route);

        RouteRepository.saveRoute(
                getApplication(),
                RouteCalculator.toRoutePoints(route)
        );
    }
}
