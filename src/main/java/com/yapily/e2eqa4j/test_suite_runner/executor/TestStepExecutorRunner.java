package com.yapily.e2eqa4j.test_suite_runner.executor;

import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import com.yapily.e2eqa4j.model.TestSuite.Step;
import com.yapily.e2eqa4j.utils.StringUtils;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestStepExecutorRunner
{
    @Autowired ExecutorRunner executorRunner;


    public void run(List<Executor> executors, Step step, TestSuite.Testcase testCase, Map<String, String> globalVariables, Map<String, String> executorOutput)
    {
        for(Executor executor : executors)
        {
            if(executor.executor.equals(step.type))
            {
                executor.input.putAll(step.input);
                for(Map.Entry<String, String> entry : executor.input.entrySet())
                {
                    for(Map.Entry<String, String> testResultVar : testCase.result.entrySet())
                    {
                        StringUtils.injectValue(entry, testResultVar.getKey(), testResultVar.getValue());
                    }
                }
                executorOutput.putAll(executorRunner.runExecutor(globalVariables, executor, executors, testCase, step));
                break;
            }
        }
    }
}
