package com.yapily.e2eqa4j.test_suite_runner.test_case;

import com.yapily.e2eqa4j.model.TestSuite;
import com.yapily.e2eqa4j.model.TestSuite.Step;
import com.yapily.e2eqa4j.utils.StringUtils;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TestStepVarsProcessor
{
    public void process(Step step, TestSuite.Testcase testCase, Map<String, String> globalVariables, TestSuite.StepResult lastStepResult)
    {
        for(Map.Entry<String, String> entry : step.vars.entrySet())
        {
            globalVariables.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
            step.result.output.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, "result." + entry1.getKey(), entry1.getValue()));
            if(lastStepResult != null)
            {
                lastStepResult.output.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
            }
            String[] keyParts = entry.getValue().substring(2, entry.getValue().length() - 2).split("\\.");
            StringUtils.processReplacementsInTestStepUsingStepsAlreadyExecuted(keyParts, entry);
            testCase.result.put(entry.getKey(), entry.getValue());
        }
    }
}
