package com.yapily.e2eqa4j.test_suite_runner.executor;

import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import com.yapily.e2eqa4j.TestLIVEData;
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


    public Executor.StepResult runExecutorStep(ExecutorRunner executorRunner, TestSuite.Testcase testCase, Executor executor, Map<String, String> globalVariables, List<Executor> executors, Executor.Step step, TestSuite.Step testCaseStep, Executor.StepResult lastStepResult)
    {
        executorStepInputPreparator.prepare(globalVariables, executor, step, lastStepResult);
        System.out.println("Step input vars: " + step.input);
        System.out.println("Running step: " + step.type);
        executorStepExecutorRunner.run(executorRunner, executors, executor, step, testCaseStep, testCase, globalVariables, lastStepResult);
        executorStepLogProcessor.process(step, globalVariables, executor);
        Map<String, String> stepResultPlusVars = new HashMap<>();
        stepResultPlusVars.putAll(step.vars);
        stepResultPlusVars.putAll(step.result.result);
        TestLIVEData.executorStepNamesThatHaveExecuted.put(step.name, stepResultPlusVars);
        return step.result;
    }
}
