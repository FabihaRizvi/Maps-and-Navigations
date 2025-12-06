package com.example.mapandnavigations;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import org.osmdroid.views.MapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_main);

        MapView mapView = findViewById(R.id.map);
        mapView.setMultiTouchControls(true);

        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(new GeoPoint(24.8607, 67.0011));
    }
}
