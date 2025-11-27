package com.yapily.e2eqa4j.test_suite_runner.test_case;

import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import com.yapily.e2eqa4j.model.TestSuite.Testcase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestCaseRunner
{
    @Autowired TestStepRunner testStepRunner;


    public void runTestCase(Map<String, String> globalVariables, Map<String, String> testSuiteVars, List<Executor> executors, Testcase testCase)
    {
        TestSuite.StepResult lastStepResult = null;
        for(TestSuite.Step step : testCase.steps)
        {
            lastStepResult = testStepRunner.runStep(globalVariables, testSuiteVars, executors, testCase, step, lastStepResult);
        }
        TestLIVEData.stepNamesThatHaveExecuted.put(testCase.name, new HashMap<>(testCase.result));
    }
}
