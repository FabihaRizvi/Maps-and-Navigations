package com.example.mapandnavigations.network;
import org.osmdroid.util.GeoPoint;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class NominatimService {

    public static GeoPoint searchPlace(
            String query,
            GeoPoint userLocation
    ) {
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");

            String urlStr =
                    "https://nominatim.openstreetmap.org/search" +
                            "?q=" + encodedQuery +
                            "&format=json" +
                            "&limit=10" +
                            "&lat=" + userLocation.getLatitude() +
                            "&lon=" + userLocation.getLongitude();

            URL url = new URL(urlStr);
            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();

            conn.setRequestProperty(
                    "User-Agent",
                    "MapAndNavigationsApp/1.0 (fabiharizvi2888@gmail.com)"
            );

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(conn.getInputStream())
                    );

            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            JSONArray arr = new JSONArray(json.toString());
            if (arr.length() == 0) return null;

            GeoPoint bestPoint = null;
            double minDistance = Double.MAX_VALUE;

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                double lat = obj.getDouble("lat");
                double lon = obj.getDouble("lon");

                GeoPoint candidate = new GeoPoint(lat, lon);

                double distance =
                        userLocation.distanceToAsDouble(candidate);

                if (distance < minDistance) {
                    minDistance = distance;
                    bestPoint = candidate;
                }
            }

            if (minDistance > 50_000) {
                return null;
            }

            return bestPoint;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

