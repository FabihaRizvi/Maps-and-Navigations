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

    public static GeoPoint searchPlace(String query) {

        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");

            String urlStr =
                    "https://nominatim.openstreetmap.org/search" +
                            "?q=" + encodedQuery +
                            "&format=json" +
                            "&limit=1";

            URL url = new URL(urlStr);
            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();

            conn.setRequestProperty(
                    "User-Agent",
                    "MapAndNavigationsApp/1.0 (fabiharizvi2888@gmail.com)"
            );

            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(conn.getInputStream())
                    );

            StringBuilder json = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            reader.close();
            conn.disconnect();

            JSONArray arr = new JSONArray(json.toString());

            if (arr.length() == 0) return null;

            JSONObject obj = arr.getJSONObject(0);

            double lat = obj.getDouble("lat");
            double lon = obj.getDouble("lon");

            return new GeoPoint(lat, lon);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
