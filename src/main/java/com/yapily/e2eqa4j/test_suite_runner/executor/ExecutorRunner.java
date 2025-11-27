package com.yapily.e2eqa4j.test_suite_runner.executor;

import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ExecutorRunner
{
    @Autowired ExecutorStepRunner executorStepRunner;
    @Autowired ExecutorOutputProcessor executorOutputProcessor;


    Map<String, String> runExecutor(Map<String, String> globalVariables, Executor executor, List<Executor> executors, TestSuite.Testcase testCase, TestSuite.Step testCaseStep)
    {
        System.out.println("Running executor: " + executor.executor);
        Executor.StepResult lastStepResult = null;
        for(Executor.Step step : executor.steps)
        {
            lastStepResult = executorStepRunner.runExecutorStep(this, testCase, executor, globalVariables, executors, step, testCaseStep, lastStepResult);
        }
        executorOutputProcessor.process(globalVariables, executor, lastStepResult);
        executor.output.forEach((k, v) -> System.out.println("Executor output var: " + k + " -> " + v));
        return executor.output;
    }
}
