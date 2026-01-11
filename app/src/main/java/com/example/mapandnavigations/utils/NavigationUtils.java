package com.example.mapandnavigations.utils;
import org.osmdroid.util.GeoPoint;
import java.util.List;

public class NavigationUtils {

    public static int findClosestPointIndex(
            GeoPoint user,
            List<GeoPoint> route
    ) {
        double min = Double.MAX_VALUE;
        int index = 0;

        for (int i = 0; i < route.size(); i++) {
            double d = user.distanceToAsDouble(route.get(i));
            if (d < min) {
                min = d;
                index = i;
            }
        }
        return index;
    }
}
