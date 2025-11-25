package com.yapily.e2eqa4j.utils;

import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.dataformat.yaml.YAMLMapper;

@Component
public class YAMLUtils
{
    @Autowired YAMLMapper yamlMapper;


    public List<Executor> loadLibraries(List<File> libraryFiles) throws IOException
    {
        List<Executor> libraries = new ArrayList<>();
        for(File libraryFile : libraryFiles)
        {
            try(FileInputStream fis = new FileInputStream(libraryFile);
                            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8))
            {
                libraries.add(yamlMapper.readValue(isr, Executor.class));
            }
        }
        return libraries;
    }


    public TestSuite loadTestSuite(InputStreamReader testFile)
    {
        return yamlMapper.readValue(testFile, TestSuite.class);
    }
}
