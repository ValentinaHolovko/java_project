package io;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ReadFileSingleton {
    private static ReadFileSingleton instance;

    private static final Logger logger = Logger.getLogger(ReadFileSingleton.class.getName());

    private ReadFileSingleton(){
    }

    public static synchronized ReadFileSingleton getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ReadFileSingleton();
        }
        return instance;
    }

    public List<String> readFileSource(URI fileName) {
        List<String> fileSource = new ArrayList<>();
        fileSource = readFile(Paths.get(fileName), fileSource);
        return fileSource;
    }

    public List<String> readFileSource(String fileName) {
        List<String> fileSource = new ArrayList<>();
        fileSource = readFile(Paths.get(fileName), fileSource);
        return fileSource;
    }

    private List<String> readFile(Path filePath, List<String> fileSource) {
        try (Stream<String> stringStream = Files.lines(filePath)){
            fileSource = stringStream.toList();
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, "Read file error");
        }
        return fileSource;
    }

}
