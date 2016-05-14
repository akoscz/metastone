package net.demilich.metastone.utils;

/**
 * Data class that holds the platform specific path to the metastone user home dir.
 * Note that desktop and mobile platform initialize this data class with different values.
 */
public class UserHomeMetastone {

    private static UserHomeMetastone INSTANCE;
    private String dirPath;

    private UserHomeMetastone(String path) {
        dirPath = path;
    }

    public static void init(String path) {
        if(path == null) {
            throw new NullPointerException("UserHomeMetastone.init(path) cannot be initialized with null!");
        }

        if (INSTANCE == null) {
            INSTANCE = new UserHomeMetastone(path);
        } else {
            INSTANCE.dirPath = path;
        }
    }

    public static String getPath() {
        if (INSTANCE == null) {
            throw new RuntimeException("UserHomeMetastone must first be initialized!");
        }

        return INSTANCE.dirPath;
    }
}
