package net.demilich.metastone.android;

import android.app.Application;

import net.demilich.metastone.utils.ResourceLoader;
import net.demilich.metastone.utils.UserHomeMetastone;

import java.io.File;

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

        // initialize with the Android Asset Resource Loader
        ResourceLoader.init(new AssetResourceLoader(this));
        // initialize with the Android Application internal files dir
        File metastoneHomeDir = new File(getFilesDir().getAbsolutePath() + File.separator + net.demilich.metastone.BuildConfig.NAME);
        metastoneHomeDir.mkdirs();
        UserHomeMetastone.init(metastoneHomeDir.getAbsolutePath());
    }
}

