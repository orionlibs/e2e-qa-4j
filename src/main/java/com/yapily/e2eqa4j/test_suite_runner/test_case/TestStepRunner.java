package com.yapily.e2eqa4j.test_suite_runner.test_case;

import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import com.yapily.e2eqa4j.model.TestSuite.Step;
import com.yapily.e2eqa4j.test_suite_runner.executor.TestStepExecutorRunner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestStepRunner
{
    @Autowired TestStepInputPreparator testStepInputPreparator;
    @Autowired TestStepExecutorRunner testStepExecutorRunner;
    @Autowired TestStepVarsProcessor testStepVarsProcessor;
    @Autowired TestStepLogProcessor testStepLogProcessor;


    public TestSuite.StepResult runStep(Map<String, String> globalVariables, List<Executor> executors, TestSuite.Testcase testCase, Step step, TestSuite.StepResult lastStepResult)
    {
        testStepInputPreparator.prepare(step, globalVariables, lastStepResult);
        System.out.println("Step input vars: " + step.input);
        Map<String, String> executorOutput = new HashMap<>();
        testStepExecutorRunner.run(executors, step, testCase, globalVariables, executorOutput);
        step.result.output.putAll(executorOutput);
        TestLIVEData.stepNamesThatHaveExecuted.put(testCase.name + "." + step.type, new HashMap<>(step.result.output));
        testStepVarsProcessor.process(step, testCase, globalVariables, lastStepResult);
        testCase.result.putAll(step.result.output);
        step.result.output.forEach((k, v) -> System.out.println("step result output var: " + k + " -> " + v));
        step.vars.forEach((k, v) -> System.out.println("step var: " + k + " -> " + v));
        testStepLogProcessor.process(step, testCase, globalVariables);
        return step.result;
    }
}
