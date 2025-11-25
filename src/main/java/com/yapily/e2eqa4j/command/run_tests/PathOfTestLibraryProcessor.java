package com.yapily.e2eqa4j.command.run_tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Component;

@Component
class PathOfTestLibraryProcessor
{
    String processLibraryPath(String testLibraryPath, String currentDir) throws IOException
    {
        if(testLibraryPath.startsWith("/"))
        {
            Path p = Path.of(testLibraryPath);
            if(!Files.exists(p))
            {
                throw new IOException("Library path not found: " + testLibraryPath);
            }
        }
        else
        {
            currentDir += "/" + testLibraryPath;
            File file = new File(currentDir);
            if(!file.exists())
            {
                throw new IOException("Library path not found: " + currentDir);
            }
            else
            {
                if(!file.isDirectory())
                {
                    throw new IOException("Library path has to be a directory and not a file: " + currentDir);
                }
            }
        }
        return currentDir;
    }
}
