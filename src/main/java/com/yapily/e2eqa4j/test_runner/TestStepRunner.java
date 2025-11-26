package com.yapily.e2eqa4j.test_runner;

import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import com.yapily.e2eqa4j.model.TestSuite.Step;
import com.yapily.e2eqa4j.utils.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class TestStepRunner
{
    @Autowired ExecutorRunner executorRunner;


    TestSuite.StepResult runStep(Map<String, String> globalVariables, List<Executor> executors, TestSuite.Testcase testCase, Step step, Map<String, Map<String, String>> stepNamesThatHaveExecuted, TestSuite.StepResult lastStepResult)
    {
        System.out.println("Step type: " + step.type);
        step.vars.forEach((k, v) -> System.out.println("Step result var: " + k + " -> " + v));
        step.assertions.forEach(k -> System.out.println("Assertion: " + k));
        step.input.putAll(globalVariables);
        System.out.println("Step input vars: " + step.input);
        System.out.println("Running step: " + step.type);
        //set actual values in all step.input
        for(Map.Entry<String, Object> entry : step.input.entrySet())
        {
            globalVariables.entrySet().forEach(entry1 -> StringUtils.injectObjectValue(entry, entry1.getKey(), entry1.getValue()));
            if(lastStepResult != null)
            {
                if(lastStepResult.output != null)
                {
                    lastStepResult.output.entrySet().forEach(entry1 -> StringUtils.injectObjectValue(entry, entry1.getKey(), entry1.getValue()));
                }
            }
            String[] keyParts = entry.getValue().toString().split("\\.");
            for(Entry<String, Map<String, String>> stepThatHasExecuted : stepNamesThatHaveExecuted.entrySet())
            {
                if(stepThatHasExecuted.getKey().equals(keyParts[0]))
                {
                    StringUtils.injectObjectValue(entry, entry.getValue().toString(), stepThatHasExecuted.getValue().get(keyParts[1]));
                }
            }
        }
        step.input.forEach((k, v) -> System.out.println("Updated step input var: " + k + " -> " + v));
        Map<String, String> executorOutput = new HashMap<>();
        for(Executor executor : executors)
        {
            if(executor.executor.equals(step.type))
            {
                executor.input.putAll(step.input);
                for(Map.Entry<String, Object> entry : executor.input.entrySet())
                {
                    if(lastStepResult != null)
                    {
                        if(lastStepResult.output != null)
                        {
                            lastStepResult.output.entrySet().forEach(entry1 -> StringUtils.injectObjectValue(entry, entry1.getKey(), entry1.getValue()));
                        }
                    }
                }
                System.out.println("Executor input vars: " + executor.input);
                executorOutput.putAll(executorRunner.runExecutor(globalVariables, executor, executors));
                break;
            }
        }
        step.result.output.putAll(executorOutput);
        System.out.println(">>>>>>>>>>>>>>>>>>>>size2: " + stepNamesThatHaveExecuted.size());
        stepNamesThatHaveExecuted.put(testCase.name + "." + step.type, new HashMap<>(step.result.output));
        System.out.println(">>>>>>>>>>>>>>>>>>>>size2: " + stepNamesThatHaveExecuted.size());
        System.out.println("steps that have executed: " + stepNamesThatHaveExecuted.entrySet());
        //set actual values in all step.vars
        for(Map.Entry<String, String> entry : step.vars.entrySet())
        {
            globalVariables.entrySet().forEach(entry1 -> StringUtils.injectStringValue(entry, entry1.getKey(), entry1.getValue()));
            step.result.output.entrySet().forEach(entry1 -> StringUtils.injectStringValue(entry, "result." + entry1.getKey(), entry1.getValue()));
            if(lastStepResult != null)
            {
                if(lastStepResult.output != null)
                {
                    lastStepResult.output.entrySet().forEach(entry1 -> StringUtils.injectStringValue(entry, entry1.getKey(), entry1.getValue()));
                }
            }
            testCase.result.putAll(step.result.output);
            String[] keyParts = entry.getValue().split("\\.");
            for(Entry<String, Map<String, String>> stepThatHasExecuted : stepNamesThatHaveExecuted.entrySet())
            {
                if(stepThatHasExecuted.getKey().equals(keyParts[0]))
                {
                    StringUtils.injectStringValue(entry, entry.getValue(), stepThatHasExecuted.getValue().get(keyParts[1]));
                }
            }
        }
        step.result.output.forEach((k, v) -> System.out.println("step output var: " + k + " -> " + v));
        step.vars.forEach((k, v) -> System.out.println("Updated step result var: " + k + " -> " + v));
        //process assertions
        //process log
        return step.result;
    }
}
