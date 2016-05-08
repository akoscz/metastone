package net.demilich.metastone.android;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import net.demilich.metastone.utils.IResourceLoader;
import net.demilich.metastone.utils.ResourceInputStream;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AssetResourceLoader implements IResourceLoader {

    private static final String TAG = "AssetResourceLoader";
    private final Context mContext;

    public AssetResourceLoader(Context context) {
        mContext = context;
    }

    private List<String> getJsonFiles (AssetManager mgr, String path, int level) {

        if(level >= DIR_LEVELS) {
            return Collections.EMPTY_LIST;
        }

        List<String> filelist = new ArrayList<>();
        Log.v(TAG,"enter displayFiles("+path+")");
        try {
            String list[] = mgr.list(path);
            Log.v(TAG,"L"+level+": list:"+ Arrays.asList(list));
            if (list != null) {
                String f;
                for (int i = 0; i < list.length; ++i) {
                    f = list[i];
                    if (f.endsWith(".json")) {
                        filelist.add(path + "/" + f);
                    } else {
                        filelist.addAll(getJsonFiles(mgr, path + "/" + f, level + 1));
                    }
                }
            }
        } catch (IOException e) {
            Log.v(TAG,"List error: can't list" + path);
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
