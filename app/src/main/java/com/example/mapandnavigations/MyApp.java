package com.example.mapandnavigations;
import org.osmdroid.config.Configuration;
import java.io.File;
import android.app.Application;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Configuration.getInstance().setUserAgentValue(getPackageName());

        File basePath = new File(getCacheDir(), "osmdroid");
        File tileCache = new File(basePath, "tiles");

        if (!basePath.exists()) {
            basePath.mkdirs();
        }
        if (!tileCache.exists()) {
            tileCache.mkdirs();
        }

        Configuration.getInstance().setOsmdroidBasePath(basePath);
        Configuration.getInstance().setOsmdroidTileCache(tileCache);
    }
}
