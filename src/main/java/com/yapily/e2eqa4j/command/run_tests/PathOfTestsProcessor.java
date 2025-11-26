package com.yapily.e2eqa4j.command.run_tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Component;

@Component
class PathOfTestsProcessor
{
    private boolean isPathAFile;


    String processPath(String path, String currentDir) throws IOException
    {
        if(path.startsWith("/"))
        {
            validateTestsPath(path);
        }
        else
        {
            currentDir = getAbsolutePathOfTestsDirectory(path, currentDir);
        }
        return currentDir;
    }


    private String getAbsolutePathOfTestsDirectory(String path, String currentDir) throws IOException
    {
        currentDir += "/" + path;
        File file = new File(currentDir);
        if(!file.exists())
        {
            throw new IOException("Path/file not found: " + currentDir);
        }
        else
        {
            if(file.isFile())
            {
                isPathAFile = true;
            }
        }
        return currentDir;
    }


    private static void validateTestsPath(String path) throws IOException
    {
        Path p = Path.of(path);
        if(!Files.exists(p))
        {
            throw new IOException("Path not found: " + path);
        }
    }


    boolean isPathAFile()
    {
        return isPathAFile;
    }
}
