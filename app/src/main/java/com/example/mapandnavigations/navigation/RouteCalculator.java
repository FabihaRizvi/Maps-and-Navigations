package com.example.mapandnavigations.navigation;
import com.example.mapandnavigations.model.RoutePoint;
import org.osmdroid.util.GeoPoint;
import java.util.ArrayList;
import java.util.List;

public class RouteCalculator {

    public static List<GeoPoint> demoRoute(GeoPoint start, GeoPoint end) {
        List<GeoPoint> points = new ArrayList<>();

        points.add(start);
        points.add(new GeoPoint(
                (start.getLatitude() + end.getLatitude()) / 2,
                (start.getLongitude() + end.getLongitude()) / 2
        ));
        points.add(end);

        return points;
    }

    public static List<RoutePoint> toRoutePoints(List<GeoPoint> geoPoints) {
        List<RoutePoint> list = new ArrayList<>();
        for (GeoPoint g : geoPoints) {
            list.add(new RoutePoint(g.getLatitude(), g.getLongitude()));
        }
        return list;
    }

    public static List<GeoPoint> toGeoPoints(List<RoutePoint> points) {
        List<GeoPoint> geoPoints = new ArrayList<>();

        for (RoutePoint p : points) {
            geoPoints.add(new GeoPoint(p.latitude, p.longitude));
        }

        return geoPoints;
    }

}
