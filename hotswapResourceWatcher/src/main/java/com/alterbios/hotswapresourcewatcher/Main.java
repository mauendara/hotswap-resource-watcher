package com.alterbios.hotswapresourcewatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Properties;

/**
 *
 * @author mauendara
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        String workingDir;
        String propertiesPath;
        if (args.length > 0) {
            workingDir = args[0];
        } else {
            workingDir = new File(".").getCanonicalPath() + "/";
        }

        propertiesPath = workingDir + "config.properties";
        Properties properties = new Properties();

        if (new File(propertiesPath).exists()) {
            System.out.println("Using properties: " + propertiesPath);
            properties.load(new FileInputStream(propertiesPath));

            String originalDir = properties.getProperty("original");
            String deploymentDir = properties.getProperty("deploy");

            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                Path path = Paths.get(originalDir);
                System.out.println("Watching dir: " + originalDir);
                path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
                WatchKey key;
                while ((key = watchService.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE
                                || event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            Files.copy(Paths.get(originalDir + '/' + event.context()), Paths.get(deploymentDir + '/' + event.context()), StandardCopyOption.REPLACE_EXISTING);
                        } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                            Files.delete(Paths.get(deploymentDir + '/' + event.context()));
                        }
                        System.out.println(event.kind() + ":File:" + event.context());
                    }
                    key.reset();
                }
            }
        } else {
            System.out.println("Couldn't find config.properties in " + workingDir);
            System.out.println("You can : ");
            System.out.println("java -jar watcher.jar /home/ubuntu/ ");
        }
    }

}
