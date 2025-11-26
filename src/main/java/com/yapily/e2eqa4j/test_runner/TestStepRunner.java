package com.yapily.e2eqa4j.test_runner;

import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import com.yapily.e2eqa4j.model.TestSuite.Step;
import com.yapily.e2eqa4j.utils.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class TestStepRunner
{
    @Autowired ExecutorRunner executorRunner;


    TestSuite.StepResult runStep(Map<String, String> globalVariables, List<Executor> executors, Step step, TestSuite.StepResult lastStepResult)
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
            globalVariables.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
            if(lastStepResult != null && lastStepResult.output != null)
            {
                lastStepResult.output.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
            }
        }
        step.input.forEach((k, v) -> System.out.println("Updated step result var: " + k + " -> " + v));
        Map<String, String> executorOutput = new HashMap<>();
        for(Executor executor : executors)
        {
            if(executor.executor.equals(step.type))
            {
                executorOutput.putAll(executorRunner.runExecutor(globalVariables, executor, executors));
                break;
            }
        }
        //set actual values in all step.vars
        //process assertions
        //set actual values in all step.result plus use executorOutput
        //process info
        return step.result;
    }
}
