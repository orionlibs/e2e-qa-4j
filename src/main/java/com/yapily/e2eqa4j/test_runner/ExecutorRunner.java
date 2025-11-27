package com.yapily.e2eqa4j.test_runner;

import com.yapily.e2eqa4j.model.Executor;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ExecutorRunner
{
    @Autowired ExecutorStepRunner executorStepRunner;


    Map<String, String> runExecutor(Map<String, String> globalVariables, Executor executor, List<Executor> executors)
    {
        System.out.println("Running executor: " + executor.executor);
        Executor.StepResult lastStepResult = null;
        for(Executor.Step step : executor.steps)
        {
            lastStepResult = executorStepRunner.runExecutorStep(this, executor, globalVariables, executors, step, lastStepResult);
        }
        //set actual values in all executor.output
        executor.output.forEach((k, v) -> System.out.println("Executor output var: " + k + " -> " + v));
        return executor.output;
    }
}
