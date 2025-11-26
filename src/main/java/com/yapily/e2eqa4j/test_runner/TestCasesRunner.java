package com.yapily.e2eqa4j.test_runner;

import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class TestCasesRunner
{
    @Autowired TestCaseRunner testCaseRunner;


    void runTestCases(Map<String, String> globalVariables, TestSuite testSuite, List<Executor> executors)
    {
        for(TestSuite.Testcase testcase : testSuite.testcases)
        {
            testCaseRunner.runTestCase(globalVariables, executors, testcase);
        }
    }
}
