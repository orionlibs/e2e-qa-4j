package com.yapily.e2eqa4j.test_suite_runner.executor;

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
public class ExecutorOutputProcessor
{
    public void process(Map<String, String> globalVariables, Executor executor, Executor.StepResult lastStepResult)
    {
        for(Entry<String, String> entry : executor.output.entrySet())
        {
            for(Map.Entry<String, String> globalVariable : globalVariables.entrySet())
            {
                StringUtils.injectValue(entry, globalVariable.getKey(), globalVariable.getValue());
            }
            List<String> placeholders2 = new ArrayList<>();
            Pattern p2 = Pattern.compile("\\{\\{[^}]+\\}\\}");
            Matcher m2 = p2.matcher(entry.getValue());
            while(m2.find())
            {
                placeholders2.add(m2.group());
            }
            for(String placeholder : placeholders2)
            {
                if(placeholder.startsWith("{{result."))
                {
                    if(lastStepResult != null)
                    {
                        if(placeholder.equals("{{result.statusCode}}"))
                        {
                            StringUtils.injectValue(entry, "result.statusCode", Integer.toString(lastStepResult.statusCode));
                        }
                        else if(placeholder.equals("{{result.body}}"))
                        {
                            StringUtils.injectValue(entry, "result.body", lastStepResult.body);
                        }
                        else if(placeholder.equals("{{result.headers}}"))
                        {
                            StringUtils.injectValue(entry, "result.headers", lastStepResult.headers.toString());
                        }
                        if(placeholder.startsWith("{{result.headers."))
                        {
                            String[] headerVarParts = placeholder.split("\\.");
                            String headerValue = lastStepResult.headers.get(headerVarParts[headerVarParts.length - 1]);
                            StringUtils.injectValue(entry, placeholder.substring(2, placeholder.length() - 2), headerValue);
                        }
                        for(Map.Entry<String, String> testCaseResultVar : lastStepResult.result.entrySet())
                        {
                            if(testCaseResultVar.getKey().equals(placeholder.substring(2, placeholder.length() - 2).replace("result.", "")))
                            {
                                StringUtils.injectValue(entry, "result." + testCaseResultVar.getKey(), testCaseResultVar.getValue());
                            }
                        }
                    }
                }
                else if(placeholder.startsWith("{{input."))
                {
                    for(Map.Entry<String, String> executorInputVar : executor.input.entrySet())
                    {
                        if(executorInputVar.getKey().equals(placeholder.substring(2, placeholder.length() - 2).replace("input.", "")))
                        {
                            StringUtils.injectValue(entry, "input." + executorInputVar.getKey(), executorInputVar.getValue());
                        }
                    }
                }
                else if(placeholder.indexOf(".") != -1)
                {
                    String[] keyParts = placeholder.split("\\.");
                    StringUtils.processReplacementsInTestStepUsingStepsAlreadyExecuted(keyParts, entry, placeholder);
                }
            }
        }
    }
}
