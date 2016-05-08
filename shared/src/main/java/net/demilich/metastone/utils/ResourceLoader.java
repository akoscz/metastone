package net.demilich.metastone.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

public class ResourceLoader implements IResourceLoader{

    private static Logger logger = LoggerFactory.getLogger(ResourceLoader.class);

    private static IResourceLoader INSTANCE;

    public static void init(IResourceLoader resourceLoader) {
        if(resourceLoader == null) {
            throw new NullPointerException("ResourceLoader.init(resourceLoader) cannot be initialized with null!");
        }
        INSTANCE = resourceLoader;
    }

    public static IResourceLoader getInstance() {
        if(INSTANCE == null) {
            throw new RuntimeException("ResourceLoader must first be initialized!");
        }
        return INSTANCE;
    }

    /**
     * Loads all the json files from the given rootDir into a collection of ResourceInputStreams
     * @param rootDir the root dir from where to start traversing to load the json files
     * @param fromFileSystem True if the rootDir is on the filesystem, False if the rootDir is in the Resources dir
     * @return Collections of ResourceInputStreams pointing to the json files
     * @throws URISyntaxException
     * @throws IOException
     */
    @Override
    public Collection<ResourceInputStream> loadJsonInputStreams(String rootDir, boolean fromFileSystem) throws URISyntaxException, IOException {
        if (rootDir == null) {
            throw new RuntimeException("rootDir cannot be null");
        }

        Path dirPath;
        boolean fromJar = false;
        if (fromFileSystem) {
            dirPath = Paths.get(rootDir);
        } else { // from resources
            URI uri;
            try {
                uri = Object.class.getResource("/" + rootDir).toURI();
            } catch (NullPointerException ex) {
                logger.error(rootDir + " directory not found in resources");
                throw new RuntimeException(rootDir + " directory not found in resources");
            }

            // handle case where resources are on the filesystem instead of jar. ie: running form within IntelliJ
            fromJar = uri.getScheme().equals("jar");
            if (fromJar) { // from jar file on the classpath
                FileSystem fileSystem;
                try {
                    fileSystem = FileSystems.getFileSystem(uri);
                } catch (FileSystemNotFoundException ex) {
                    fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                }
                dirPath = fileSystem.getPath(rootDir);
            } else { // from resources folder on the filesystem
                dirPath = Paths.get(uri);
            }
        }

        Collection<ResourceInputStream> inputStreams = new ArrayList<>();

        Path filePath;
        Stream<Path> walk = Files.walk(dirPath, DIR_LEVELS);
        for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
            filePath = it.next();

            // skip over non-json files and directories
            if (!filePath.toString().endsWith("json")) continue;

            InputStream inputStream;
            if (fromJar) {
                inputStream = Object.class.getResourceAsStream(filePath.toString());
            } else {
                inputStream = new FileInputStream(new File(filePath.toString()));
            }

            inputStreams.add(new ResourceInputStream(filePath.getFileName().toString(), inputStream, fromFileSystem));
        }
        walk.close();
        return inputStreams;
    }
}
