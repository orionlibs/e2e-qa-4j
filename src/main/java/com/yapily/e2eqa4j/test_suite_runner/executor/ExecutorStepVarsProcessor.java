package com.yapily.e2eqa4j.test_suite_runner.executor;

import com.yapily.e2eqa4j.TestLIVEData;
import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.utils.StringUtils;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ExecutorStepVarsProcessor
{
    public void process(Executor.Step step, Executor.StepResult lastStepResult)
    {
        for(Map.Entry<String, String> entry : step.vars.entrySet())
        {
            TestLIVEData.globalVariables.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
            step.result.result.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, "result." + entry1.getKey(), entry1.getValue()));
            if(lastStepResult != null)
            {
                if(lastStepResult.result != null)
                {
                    lastStepResult.result.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
                }
            }
            String[] keyParts = entry.getValue().substring(2, entry.getValue().length() - 2).split("\\.");
            StringUtils.processReplacementsInStepUsingStepsAlreadyExecuted(keyParts, entry);
        }
    }
}
