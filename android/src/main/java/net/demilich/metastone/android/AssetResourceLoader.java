package net.demilich.metastone.android;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import net.demilich.metastone.utils.IResourceLoader;
import net.demilich.metastone.utils.ResourceInputStream;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AssetResourceLoader implements IResourceLoader {

    private static final String TAG = "AssetResourceLoader";
    private final Context mContext;

    public AssetResourceLoader(Context context) {
        mContext = context;
    }

    private List<String> getJsonFiles (AssetManager assetManager, String path, int level) {

        if(level >= DIR_LEVELS) {
            return Collections.EMPTY_LIST;
        }

        List<String> filelist = new ArrayList<>();
        try {
            String list[] = assetManager.list(path);
            if (list != null) {
                String f;
                for (int i = 0; i < list.length; ++i) {
                    f = list[i];
                    if (f.endsWith(".json")) {
                        filelist.add(path + "/" + f);
                    } else {
                        filelist.addAll(getJsonFiles(assetManager, path + "/" + f, level + 1));
                    }
                }
            }
        } catch (IOException e) {
            Log.v(TAG,"List error: can't list " + path);
        }

        return filelist;
    }

    @Override
    public Collection<ResourceInputStream> loadJsonInputStreams(String rootDir, boolean fromFileSystem) throws URISyntaxException, IOException {
        final AssetManager mgr = mContext.getAssets();
        List<String> filesList = getJsonFiles(mgr, rootDir, 0);

        return filesList.stream()
            .filter(f -> f.endsWith(".json"))
            .map(f -> {
                try {
                    return new ResourceInputStream(f, mContext.getResources().getAssets().open(f), false);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            })
            .filter(i -> i != null)
            .collect(Collectors.toList());
    }
}
