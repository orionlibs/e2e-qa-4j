package com.yapily.e2eqa4j.test_suite_runner.executor;

import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.model.TestSuite;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class SetupExecutorsRunner
{
    @Autowired ExecutorRunner executorRunner;


    void runSetupExecutors(List<Executor> executors, TestSuite testSuite, Map<String, String> globalVariables)
    {
        for(TestSuite.Step setupExecutor : testSuite.setup.steps)
        {
            for(Executor executor : executors)
            {
                if(executor.executor.equals(setupExecutor.type))
                {
                    Map<String, String> executorOutput = executorRunner.runExecutor(globalVariables, executor, executors, null, null);
                    globalVariables.putAll(executorOutput);
                    break;
                }
            }
        }
    }
}
