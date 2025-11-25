package com.yapily.e2eqa4j.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class FileUtils
{
    public static List<File> getFilesInPathAndSubpaths(Path path) throws IOException
    {
        if(path == null)
        {
            throw new IllegalArgumentException("path must not be null");
        }
        if(!Files.exists(path))
        {
            return List.of();
        }
        if(Files.isRegularFile(path))
        {
            return List.of(new File(path.toFile().getAbsolutePath()));
        }
        try(Stream<Path> stream = Files.walk(path))
        {
            return stream.filter(Files::isRegularFile)
                            .map(p -> new File(p.toFile().getAbsolutePath()))
                            .toList();
        }
    }


    public static List<File> getFilesInPathAndSubpaths(String path) throws IOException
    {
        return getFilesInPathAndSubpaths(Path.of(path));
    }
}
