package com.yapily.e2eqa4j.test_runner;

import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ExecutorStepRunner
{
    @Autowired ExecutorStepInputPreparator executorStepInputPreparator;
    @Autowired ExecutorStepExecutorRunner executorStepExecutorRunner;
    @Autowired ExecutorStepLogProcessor executorStepLogProcessor;


    Executor.StepResult runExecutorStep(ExecutorRunner executorRunner, TestSuite.Testcase testCase, Executor executor, Map<String, String> globalVariables, List<Executor> executors, Executor.Step step, TestSuite.Step testCaseStep, Executor.StepResult lastStepResult)
    {
        //step.vars.forEach((k, v) -> System.out.println("Step result var: " + k + " -> " + v));
        //step.assertions.forEach(k -> System.out.println("Assertion: " + k));
        step.input.putAll(globalVariables);
        executorStepInputPreparator.prepare(globalVariables, executor, step, lastStepResult);
        System.out.println("Step input vars: " + step.input);
        System.out.println("Running step: " + step.type);
        executorStepExecutorRunner.run(executorRunner, executors, executor, step, testCaseStep, testCase, globalVariables, lastStepResult);
        executorStepLogProcessor.process(step, globalVariables, executor);
        return step.result;
    }
}
