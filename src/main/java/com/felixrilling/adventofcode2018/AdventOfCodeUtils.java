package com.felixrilling.adventofcode2018;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AdventOfCodeUtils {
    public static String getInput(String inputPath) {
        URL resource = AdventOfCodeUtils.class.getClassLoader().getResource(inputPath);

        if (resource == null)
            throw new AdventOfCodeRuntimeException("Could not find resource: " + inputPath);

        try {
            return Files.readString(Paths.get(resource.toURI()));
        } catch (IOException e) {
            throw new AdventOfCodeRuntimeException("Could not read resource: " + inputPath);
        } catch (URISyntaxException e) {
            throw new AdventOfCodeRuntimeException("Could create resource URI: " + inputPath);
        }
    }
}
