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
            Path p = Path.of(path);
            if(!Files.exists(p))
            {
                throw new IOException("Path not found: " + path);
            }
        }
        else
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
        }
        return currentDir;
    }


    boolean isPathAFile()
    {
        return isPathAFile;
    }
}
