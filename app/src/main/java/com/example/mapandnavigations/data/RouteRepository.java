package com.example.mapandnavigations.data;
import android.content.Context;
import android.content.SharedPreferences;
import com.example.mapandnavigations.model.RoutePoint;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class RouteRepository {

    private static final String PREF_NAME = "offline_routes";
    private static final String KEY_ROUTE = "saved_route";

    public static void saveRoute(Context context, List<RoutePoint> points) {
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

            prefs.edit().putString(KEY_ROUTE, array.toString()).apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<RoutePoint> loadRoute(Context context) {
        List<RoutePoint> points = new ArrayList<>();

        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String json = prefs.getString(KEY_ROUTE, null);
        if (json == null) return points;

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
