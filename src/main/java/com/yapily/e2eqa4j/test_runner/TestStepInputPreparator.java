package com.yapily.e2eqa4j.test_runner;

import com.yapily.e2eqa4j.model.TestSuite;
import com.yapily.e2eqa4j.model.TestSuite.Step;
import com.yapily.e2eqa4j.utils.StringUtils;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.stereotype.Component;

@Component
class TestStepInputPreparator
{
    void prepare(Step step, Map<String, String> globalVariables, TestSuite.StepResult lastStepResult)
    {
        for(Map.Entry<String, String> entry : step.input.entrySet())
        {
            globalVariables.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
            if(lastStepResult != null)
            {
                if(lastStepResult.output != null)
                {
                    lastStepResult.output.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, "result." + entry1.getKey(), entry1.getValue()));
                }
            }
            String[] keyParts = entry.getValue().substring(2, entry.getValue().length() - 2).split("\\.");
            for(Entry<String, Map<String, String>> stepThatHasExecuted : TestLIVEData.stepNamesThatHaveExecuted.entrySet())
            {
                if(stepThatHasExecuted.getKey().equals(keyParts[0]))
                {
                    StringUtils.injectValue(entry, entry.getValue(), stepThatHasExecuted.getValue().get(keyParts[1]));
                }
            }
        }
    }
}
