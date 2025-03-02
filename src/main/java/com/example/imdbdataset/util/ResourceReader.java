package com.example.imdbdataset.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public final class ResourceReader {

    /**
     * This utility function reads a resource file from the classpath and returns a BufferedReader for reading the content.
     * If the resource is compressed in gzip format, the function can decompress it on-the-fly.
     *
     * @param resourcePath The path to the resource file, relative to the classpath.
     * @param isGzipped    A flag indicating whether the resource is compressed in gzip format.
     * @return A BufferedReader for reading the content of the resource file.
     * @throws IOException If the resource file is not found or an error occurs during reading or decompression.
     */
    public static BufferedReader getReader(String resourcePath, boolean isGzipped) throws IOException {
        InputStream inputStream = ResourceReader.class.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }

        if (isGzipped) {
            inputStream = new GZIPInputStream(inputStream);
        }

        return new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        );
    }
}