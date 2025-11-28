package com.yapily.e2eqa4j.test_suite_runner.executor;

import com.yapily.e2eqa4j.TestLIVEData;
import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExecutorStepRunner
{
    @Autowired ExecutorStepInputPreparator executorStepInputPreparator;
    @Autowired ExecutorStepExecutorRunner executorStepExecutorRunner;
    @Autowired ExecutorStepLogProcessor executorStepLogProcessor;


    public Executor.StepResult runExecutorStep(ExecutorRunner executorRunner, TestSuite.Testcase testCase, Executor executor, List<Executor> executors, Executor.Step step, TestSuite.Step testCaseStep, Executor.StepResult lastStepResult)
    {
        executorStepInputPreparator.prepare(executor, step, lastStepResult);
        System.out.println("Step input vars: " + step.input);
        System.out.println("Running step: " + step.type);
        executorStepExecutorRunner.run(executorRunner, executors, executor, step, testCaseStep, testCase, lastStepResult);
        executorStepLogProcessor.process(step, executor);
        Map<String, String> stepResultPlusVars = new HashMap<>();
        stepResultPlusVars.putAll(step.vars);
        stepResultPlusVars.putAll(step.result.result);
        TestLIVEData.executorStepNamesThatHaveExecuted.put(step.name, stepResultPlusVars);
        return step.result;
    }
}
