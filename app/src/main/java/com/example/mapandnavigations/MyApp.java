package com.example.mapandnavigations;
import org.osmdroid.config.Configuration;
import java.io.File;
import android.app.Application;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Configuration.getInstance().setUserAgentValue(getPackageName());
    }
}
