package com.yapily.e2eqa4j.test_suite_runner.test_case;

import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestCasesRunner
{
    @Autowired TestCaseRunner testCaseRunner;


    public void runTestCases(TestSuite testSuite, List<Executor> executors)
    {
        for(TestSuite.Testcase testCase : testSuite.testcases)
        {
            testCaseRunner.runTestCase(testSuite.vars, executors, testCase);
        }
    }
}
