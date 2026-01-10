package com.example.mapandnavigations.data;
import android.content.Context;
import android.content.SharedPreferences;
import com.example.mapandnavigations.model.RoutePoint;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import java.util.ArrayList;
import java.util.List;

public class RouteRepository {

    private static final String PREF_NAME = "offline_routes";
    private static final String KEY_ROUTE = "saved_route";
    private static final String KEY_DEST_LAT = "dest_lat";
    private static final String KEY_DEST_LNG = "dest_lng";

    public static void saveRoute(
            Context context,
            List<RoutePoint> points,
            GeoPoint destination
    ) {
        try {
            JSONArray array = new JSONArray();
            for (RoutePoint p : points) {
                JSONObject obj = new JSONObject();
                obj.put("lat", p.latitude);
                obj.put("lng", p.longitude);
                array.put(obj);
            }

            SharedPreferences prefs =
                    context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

            prefs.edit()
                    .putString(KEY_ROUTE, array.toString())
                    .putFloat(KEY_DEST_LAT, (float) destination.getLatitude())
                    .putFloat(KEY_DEST_LNG, (float) destination.getLongitude())
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<RoutePoint> loadRouteIfMatches(
            Context context,
            GeoPoint requestedDestination
    ) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        float savedLat = prefs.getFloat(KEY_DEST_LAT, Float.NaN);
        float savedLng = prefs.getFloat(KEY_DEST_LNG, Float.NaN);

        if (Float.isNaN(savedLat) || Float.isNaN(savedLng)) {
            return null;
        }

        if (Math.abs(savedLat - requestedDestination.getLatitude()) > 0.0005 ||
                Math.abs(savedLng - requestedDestination.getLongitude()) > 0.0005) {
            return null;
        }

        String json = prefs.getString(KEY_ROUTE, null);
        if (json == null) return null;

        List<RoutePoint> points = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                points.add(
                        new RoutePoint(
                                obj.getDouble("lat"),
                                obj.getDouble("lng")
                        )
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return points;
    }
}
