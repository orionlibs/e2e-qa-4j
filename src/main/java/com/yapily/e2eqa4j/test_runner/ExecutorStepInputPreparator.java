package com.yapily.e2eqa4j.test_runner;

import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
class ExecutorStepInputPreparator
{
    void prepare(Map<String, String> globalVariables, Executor executor, Executor.Step step, Executor.StepResult lastStepResult)
    {
        for(Map.Entry<String, String> entry : step.input.entrySet())
        {
            globalVariables.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), entry1.getValue()));
            if(lastStepResult != null && lastStepResult.result != null)
            {
                lastStepResult.result.entrySet().forEach(entry1 -> StringUtils.injectValue(entry, entry1.getKey(), (String)entry1.getValue()));
            }
            for(Map.Entry<String, String> stepVar : step.vars.entrySet())
            {
                StringUtils.injectValue(entry, stepVar.getKey(), stepVar.getValue());
            }
            List<String> placeholders = new ArrayList<>();
            Pattern p = Pattern.compile("\\{\\{[^}]+\\}\\}");
            Matcher m = p.matcher(entry.getKey());
            while(m.find())
            {
                placeholders.add(m.group());
            }
            for(String placeholder : placeholders)
            {
                if(placeholder.startsWith("{{input."))
                {
                    String[] keyParts = placeholder.split("\\.");
                    for(Entry<String, String> executorInput : executor.input.entrySet())
                    {
                        if(keyParts.length == 2 && executorInput.getKey().equals(keyParts[0].substring(2)))
                        {
                            StringUtils.injectValue(entry, placeholder, keyParts[1].substring(0, keyParts[1].length() - 2));
                        }
                    }
                }
            }
        }
    }
}
