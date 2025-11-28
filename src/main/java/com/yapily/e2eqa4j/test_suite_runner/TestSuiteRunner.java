package com.yapily.e2eqa4j.test_suite_runner;

import com.yapily.e2eqa4j.TestLIVEData;
import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import com.yapily.e2eqa4j.test_suite_runner.executor.SetupExecutorsRunner;
import com.yapily.e2eqa4j.test_suite_runner.test_case.TestCasesRunner;
import com.yapily.e2eqa4j.utils.YAMLUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestSuiteRunner
{
    @Autowired TestCasesRunner testCasesRunner;
    @Autowired SetupExecutorsRunner setupExecutorsRunner;
    @Autowired YAMLUtils yamlUtils;


    public void runTest(File testFile, List<File> libraryFiles) throws IOException
    {
        List<Executor> executors = yamlUtils.loadLibraries(libraryFiles);
        try(FileInputStream fis = new FileInputStream(testFile);
                        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8))
        {
            TestSuite testSuite = yamlUtils.loadTestSuite(isr);
            setupExecutorsRunner.runSetupExecutors(executors, testSuite);
            TestLIVEData.globalVariables.putAll(testSuite.vars);
            testCasesRunner.runTestCases(testSuite, executors);
        }
    }


    public void runTest(String testFilePath, List<File> libraryFiles) throws IOException
    {
        runTest(new File(testFilePath), libraryFiles);
    }
}
