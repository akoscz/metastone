package net.demilich.metastone.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

public interface IResourceLoader {
    // the number of dirs levels to traverse on the given path
    int DIR_LEVELS = 5;

    Collection<ResourceInputStream> loadJsonInputStreams(String rootDir, boolean fromFileSystem) throws URISyntaxException, IOException;

    void copyFromResources(final String sourceDir, final String targetdir) throws URISyntaxException, IOException;
}
