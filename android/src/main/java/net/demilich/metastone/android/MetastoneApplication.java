package net.demilich.metastone.android;

import android.app.Application;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MetastoneApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

}

