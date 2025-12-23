package com.example.mapandnavigations.navigation;
import android.content.Context;
import android.content.SharedPreferences;
import com.example.mapandnavigations.model.RoutePoint;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
public class RouteLoader {
    private static final String PREF_NAME = "offline_route";
    private static final String KEY_ROUTE = "saved_route";
    public static List<RoutePoint> loadRoute(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String json = prefs.getString(KEY_ROUTE, null);

        List<RoutePoint> points = new ArrayList<>();

        if (json == null) return points;

        try {
            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                double lat = obj.getDouble("lat");
                double lon = obj.getDouble("lon");

                points.add(new RoutePoint(lat, lon));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return points;
    }
}
