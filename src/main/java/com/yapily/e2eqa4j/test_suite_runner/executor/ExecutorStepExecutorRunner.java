package com.yapily.e2eqa4j.test_suite_runner.executor;

import com.yapily.e2eqa4j.executor.CustomExecutor;
import com.yapily.e2eqa4j.executor.ExecutorType;
import com.yapily.e2eqa4j.executor.http.APICallResult;
import com.yapily.e2eqa4j.executor.http.HTTPExecutor;
import com.yapily.e2eqa4j.executor.http.HTTPExecutorInputPreparator;
import com.yapily.e2eqa4j.executor.http.HTTPExecutorOutputProcessor;
import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExecutorStepExecutorRunner
{
    @Autowired HTTPExecutor httpExecutor;
    @Autowired ExecutorStepExecutorStepInputPreparator executorStepExecutorStepInputPreparator;
    @Autowired CustomExecutor customExecutor;
    @Autowired HTTPExecutorInputPreparator httpExecutorInputPreparator;
    @Autowired HTTPExecutorOutputProcessor httpExecutorOutputProcessor;
    @Autowired ExecutorStepVarsProcessor executorStepVarsProcessor;


    public void run(ExecutorRunner executorRunner, List<Executor> executors, Executor executor, Executor.Step step, TestSuite.Step testCaseStep, TestSuite.Testcase testCase, Map<String, String> globalVariables, Executor.StepResult lastStepResult)
    {
        for(Executor executorToRun : executors)
        {
            executorStepExecutorStepInputPreparator.prepare(executor, executorToRun, step, globalVariables, lastStepResult);
            if(executorToRun.executor.equals(step.type))
            {
                customExecutor.run(executorRunner, executorToRun, executors, step, testCaseStep, testCase, globalVariables);
                break;
            }
            else if(ExecutorType.HTTP.name().equalsIgnoreCase(step.type))
            {
                httpExecutorInputPreparator.prepare(step, globalVariables, executor);
                APICallResult apiCallResult = httpExecutor.run(step.method, step.url, httpExecutorInputPreparator.headers, step.body);
                httpExecutorOutputProcessor.process(step, apiCallResult, testCaseStep, testCase);
                break;
            }
            executorStepVarsProcessor.process(step, globalVariables, lastStepResult);
        }
    }
}
