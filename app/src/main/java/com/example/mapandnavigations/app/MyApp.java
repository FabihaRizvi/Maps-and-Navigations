package com.example.mapandnavigations.app;
import android.app.Application;
import org.osmdroid.config.Configuration;
import java.io.File;
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Configuration.getInstance().setUserAgentValue(getPackageName());

        File osmdroidBasePath = new File(getCacheDir(), "osmdroid");
        if (!osmdroidBasePath.exists()) {
            osmdroidBasePath.mkdirs();
        }

        File tileCache = new File(osmdroidBasePath, "tiles");
        if (!tileCache.exists()) {
            tileCache.mkdirs();
        }

        Configuration.getInstance().setOsmdroidBasePath(osmdroidBasePath);
        Configuration.getInstance().setOsmdroidTileCache(tileCache);
    }
}
