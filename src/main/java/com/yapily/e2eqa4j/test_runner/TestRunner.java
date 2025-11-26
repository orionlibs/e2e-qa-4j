package com.yapily.e2eqa4j.test_runner;

import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import com.yapily.e2eqa4j.utils.YAMLUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestRunner
{
    @Autowired TestCasesRunner testCasesRunner;
    @Autowired SetupExecutorsRunner setupExecutorsRunner;
    @Autowired YAMLUtils yamlUtils;
    Map<String, String> allVars = new HashMap<>();


    public void runTest(File testFile, List<File> libraryFiles, Map<String, String> globalVariables) throws IOException
    {
        List<Executor> executors = yamlUtils.loadLibraries(libraryFiles);
        try(FileInputStream fis = new FileInputStream(testFile);
                        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8))
        {
            TestSuite testSuite = yamlUtils.loadTestSuite(isr);
            System.out.println("Running test suite: " + testSuite.name);
            testSuite.setup.steps.forEach(s -> System.out.println("setup steps: " + s.type));
            setupExecutorsRunner.runSetupExecutors(executors, testSuite, globalVariables);
            System.out.println("Running test suite: " + testSuite.setup);
            testCasesRunner.runTestCases(globalVariables, testSuite, executors);
        }
    }


    public void runTest(String testFilePath, List<File> libraryFiles, Map<String, String> globalVariables) throws IOException
    {
        runTest(new File(testFilePath), libraryFiles, globalVariables);
    }
}
