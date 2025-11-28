package com.yapily.e2eqa4j.command.run_tests;

import com.yapily.e2eqa4j.test_suite_runner.TestSuiteRunner;
import com.yapily.e2eqa4j.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RunTestsCommand
{
    @Autowired PathOfTestsProcessor pathOfTestsProcessor;
    @Autowired PathOfTestLibraryProcessor pathOfTestLibraryProcessor;
    @Autowired TestSuiteRunner testSuiteRunner;


    public void runTests(String path,
                    String testLibraryPath) throws IOException
    {
        System.out.println("Running tests: " + path);
        String currentDir = System.getProperty("user.dir");
        System.out.println("Current working directory: " + currentDir);
        String pathOfTestsOrTestFile = pathOfTestsProcessor.processPath(path, currentDir);
        String pathOfLibrary = pathOfTestLibraryProcessor.processLibraryPath(testLibraryPath, currentDir);
        List<File> libraryFiles = FileUtils.getFilesInPathAndSubpaths(pathOfLibrary);
        System.out.println("Using library files: " + libraryFiles);
        if(pathOfTestsProcessor.isPathAFile())
        {
            testSuiteRunner.runTest(pathOfTestsOrTestFile, libraryFiles);
        }
        else
        {
            runTestsInPath(pathOfTestsOrTestFile, libraryFiles);
        }
    }


    private void runTestsInPath(String pathOfTestsOrTestFile, List<File> libraryFiles) throws IOException
    {
        List<File> testFiles = FileUtils.getFilesInPathAndSubpaths(pathOfTestsOrTestFile);
        System.out.println("Running test files: " + testFiles);
        testFiles.forEach(testFile -> {
            try
            {
                testSuiteRunner.runTest(testFile, libraryFiles);
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
        });
    }
}
