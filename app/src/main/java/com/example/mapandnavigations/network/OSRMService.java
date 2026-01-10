package com.example.mapandnavigations.network;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OSRMService {

    public static String fetchRoutePolyline(
            double startLat, double startLon,
            double endLat, double endLon
    ) {
        try {
            String urlStr =
                    "https://router.project-osrm.org/route/v1/driving/"
                            + startLon + "," + startLat + ";"
                            + endLon + "," + endLat
                            + "?overview=full&geometries=polyline";

            URL url = new URL(urlStr);
            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(conn.getInputStream())
                    );

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject json = new JSONObject(sb.toString());
            JSONArray routes = json.getJSONArray("routes");

            return routes
                    .getJSONObject(0)
                    .getString("geometry");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
