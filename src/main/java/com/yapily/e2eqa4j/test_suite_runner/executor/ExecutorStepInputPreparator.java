package com.yapily.e2eqa4j.test_suite_runner.executor;

import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.utils.StringUtils;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ExecutorStepInputPreparator
{
    public void prepare(Map<String, String> globalVariables, Executor executor, Executor.Step step, Executor.StepResult lastStepResult)
    {
        step.input.putAll(globalVariables);
        for(Map.Entry<String, String> entry : step.input.entrySet())
        {
            globalVariables.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
            if(lastStepResult != null)
            {
                lastStepResult.result.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), (String)entry1.getValue()));
            }
            String[] keyParts = entry.getValue().substring(2, entry.getValue().length() - 2).split("\\.");
            StringUtils.processReplacementsInStepUsingStepsAlreadyExecuted(keyParts, entry);
            /*for(Map.Entry<String, String> stepVar : step.vars.entrySet())
            {
                StringUtils.injectValue(entry, stepVar.getKey(), stepVar.getValue());
            }*/
            StringUtils.processReplacementsInExecutorStepUsingExecutorInput(entry, executor.input);
        }
    }
}
