package com.yapily.e2eqa4j.command.run_tests;

import com.yapily.e2eqa4j.test_suite_runner.TestSuiteRunner;
import com.yapily.e2eqa4j.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class RunTestsCommand
{
    @Autowired PathOfTestsProcessor pathOfTestsProcessor;
    @Autowired PathOfTestLibraryProcessor pathOfTestLibraryProcessor;
    @Autowired TestSuiteRunner testSuiteRunner;


    @ShellMethod("Run given test file or tests in the given path")
    public void runTests(@ShellOption() String path,
                    @ShellOption() String testLibraryPath,
                    @ShellOption() Map<String, String> globalVariables) throws IOException
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
            testSuiteRunner.runTest(pathOfTestsOrTestFile, libraryFiles, globalVariables);
        }
        else
        {
            runTestsInPath(globalVariables, pathOfTestsOrTestFile, libraryFiles);
        }
    }


    private void runTestsInPath(Map<String, String> globalVariables, String pathOfTestsOrTestFile, List<File> libraryFiles) throws IOException
    {
        List<File> testFiles = FileUtils.getFilesInPathAndSubpaths(pathOfTestsOrTestFile);
        System.out.println("Running test files: " + testFiles);
        testFiles.forEach(testFile -> {
            try
            {
                testSuiteRunner.runTest(testFile, libraryFiles, globalVariables);
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
        });
    }
}
