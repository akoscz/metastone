package net.demilich.metastone.android;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import net.demilich.metastone.utils.IResourceLoader;
import net.demilich.metastone.utils.ResourceInputStream;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AssetResourceLoader implements IResourceLoader {

    private static final String TAG = "AssetResourceLoader";
    private final Context mContext;

    public AssetResourceLoader(Context context) {
        mContext = context;
    }

    @Override
    public Collection<ResourceInputStream> loadJsonInputStreams(String rootDir, boolean fromFileSystem) throws URISyntaxException, IOException {
        Stream<String> filesStream;

        if (fromFileSystem) {
            filesStream = FileUtils.listFiles(new File(rootDir), null, true)
                .stream()
                .map(file -> file.getAbsolutePath());
        } else {
            filesStream = listAssetFiles(mContext.getAssets(), rootDir, true)
                .stream();
        }

        return filesStream
            .filter(f -> f.endsWith(".json"))
            .map(f -> {
                try {
                    return new ResourceInputStream(f, fileToInputStream(f, fromFileSystem), fromFileSystem);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            })
            .filter(i -> i != null)
            .collect(Collectors.toList());
    }

    @Override
    public void copyFromResources(String sourceDir, String targetDir) throws URISyntaxException, IOException {
        final AssetManager assetManager = mContext.getAssets();
        copyAssetFolder(assetManager, sourceDir, targetDir);
    }

    /**
     * List all the files in the directory specified in the given path.
     * @param path The path to the directory from to list files.
     * @param recursive True if you want files listed in subdirecoties, False otherwise
     * @return List of all files found in the given directory path.
     */
    private List<String> listAssetFiles(AssetManager assetManager, String path, boolean recursive) {
        List<String> filelist = new ArrayList<>();
        try {
            String list[] = assetManager.list(path);
            if (list != null) {
                for (String f : list) {
                    if (f.contains(".")) {
                        filelist.add(path + "/" + f);
                    } else if (recursive){
                        filelist.addAll(listAssetFiles(assetManager, path + "/" + f, recursive));
                    }
                }
            }
        } catch (IOException e) {
            Log.v(TAG,"List error: can't list " + path);
            return Collections.EMPTY_LIST;
        }

        return filelist;
    }

    /**
     * Utility nethod to abstract out the logic of how to convert the given file to an InputStream
     * based on whether the file in on the filesystem or in the resources asset folder
     * @param f The path to the file
     * @param fromFileSystem True if the file is on the filesyste, False if its in the resources asset folder.
     * @return An InputStream instance to the given file.
     * @throws IOException
     */
    private InputStream fileToInputStream(String f, boolean fromFileSystem) throws IOException {
        if (fromFileSystem) {
            return new FileInputStream(new File(f));
        } else {
            return mContext.getResources().getAssets().open(f);
        }
    }

    /**
     * Utility method to recursively copy all the files from the specified asset directory path to a path on the filesystem.  
     * @param fromAssetPath Path to an assets directory
     * @param toPath Destination path on the filesystem
     * @return True if files were successfully copied from the asset direcotry to the filesystem, False otherwise.
     */
    private boolean copyAssetFolder(AssetManager assetManager, String fromAssetPath, String toPath) {
        try {
            String[] files = assetManager.list(fromAssetPath);

            File targetDirFile = new File(toPath);
            if (!targetDirFile.exists()) {
                targetDirFile.mkdirs();
            }

            if (!targetDirFile.isDirectory()) {
                throw new RuntimeException(targetDirFile.getAbsolutePath() + " is not a valid directory path!");
            }

            boolean res = true;
            for (String file : files)
                if (file.contains("."))
                    res &= copyAsset(assetManager,
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
                else
                    res &= copyAssetFolder(assetManager,
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Utility method to perform the actual copying of the asset file to the filesystem path. 
     * @param fromAssetPath Asset file path.
     * @param toPath Destination file path.
     * @return True if the copy was successfully. False otherwise.
     */
    private boolean copyAsset(AssetManager assetManager, String fromAssetPath, String toPath) {
        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(fromAssetPath);
            File toFile = new File(toPath);
            toFile.createNewFile();
            out = new FileOutputStream(toFile);
            copyFile(in, out);
            in.close();
            out.flush();
            out.close();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

}
