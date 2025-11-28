package com.yapily.e2eqa4j.executor.custom;

import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import com.yapily.e2eqa4j.test_suite_runner.executor.ExecutorRunner;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CustomExecutor
{
    public void run(ExecutorRunner executorRunner, Executor executorToRun, List<Executor> executors, Executor.Step step, TestSuite.Step testCaseStep, TestSuite.Testcase testCase)
    {
        Map<String, String> nestedExecutorOutput = executorRunner.runExecutor(executorToRun, executors, testCase, testCaseStep);
        step.result.result.putAll(nestedExecutorOutput);
        if(testCase != null)
        {
            testCase.result.putAll(nestedExecutorOutput);
        }
    }
}
