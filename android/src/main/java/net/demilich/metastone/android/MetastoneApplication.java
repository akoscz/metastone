package net.demilich.metastone.android;

import android.app.Application;

import net.demilich.metastone.utils.ResourceLoader;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MetastoneApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        ButterKnife.setDebug(BuildConfig.DEBUG);
        ResourceLoader.init(new AssetResourceLoader(this));
    }
}

