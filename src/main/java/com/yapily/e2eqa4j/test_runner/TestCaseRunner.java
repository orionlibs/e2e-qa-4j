package com.yapily.e2eqa4j.test_runner;

import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import com.yapily.e2eqa4j.model.TestSuite.Testcase;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class TestCaseRunner
{
    @Autowired TestStepRunner testStepRunner;


    void runTestCase(Map<String, String> globalVariables, List<Executor> executors, Testcase testcase)
    {
        System.out.println("Testcase: " + testcase.name);
        TestSuite.StepResult lastStepResult = null;
        for(TestSuite.Step step : testcase.steps)
        {
            lastStepResult = testStepRunner.runStep(globalVariables, executors, step, lastStepResult);
        }
    }
}
