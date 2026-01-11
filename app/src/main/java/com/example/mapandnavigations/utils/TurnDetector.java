package com.example.mapandnavigations.utils;
import org.osmdroid.util.GeoPoint;

public class TurnDetector {

    public static String detectTurn(
            GeoPoint prev,
            GeoPoint current,
            GeoPoint next
    ) {
        double bearing1 = bearing(prev, current);
        double bearing2 = bearing(current, next);

        double diff = bearing2 - bearing1;
        diff = (diff + 360) % 360;

        if (diff > 160 && diff < 200)
            return "U-TURN";

        if (diff > 45 && diff <= 160)
            return "RIGHT TURN";

        if (diff < 315 && diff >= 200)
            return "LEFT TURN";

        return "STRAIGHT";
    }

    private static double bearing(GeoPoint a, GeoPoint b) {
        double lat1 = Math.toRadians(a.getLatitude());
        double lat2 = Math.toRadians(b.getLatitude());
        double dLon = Math.toRadians(
                b.getLongitude() - a.getLongitude()
        );

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x =
                Math.cos(lat1) * Math.sin(lat2)
                        - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);

        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }
}
