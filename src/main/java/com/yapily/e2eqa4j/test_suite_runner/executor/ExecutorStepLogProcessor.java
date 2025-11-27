package com.yapily.e2eqa4j.test_suite_runner.executor;

import com.yapily.e2eqa4j.Logger;
import com.yapily.e2eqa4j.model.Executor;
import com.yapily.e2eqa4j.test_suite_runner.test_case.TestLIVEData;
import com.yapily.e2eqa4j.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class ExecutorStepLogProcessor
{
    public void process(Executor.Step step, Map<String, String> globalVariables, Executor executor)
    {
        for(String log : step.log)
        {
            String updatedLog = log;
            for(Map.Entry<String, String> globalVariable : globalVariables.entrySet())
            {
                updatedLog = StringUtils.injectValue(updatedLog, globalVariable.getKey(), globalVariable.getValue());
            }
            for(Map.Entry<String, String> stepVar : step.vars.entrySet())
            {
                updatedLog = StringUtils.injectValue(updatedLog, stepVar.getKey(), stepVar.getValue());
            }
            List<String> placeholders = new ArrayList<>();
            Pattern p = Pattern.compile("\\{\\{[^}]+\\}\\}");
            Matcher m = p.matcher(updatedLog);
            while(m.find())
            {
                placeholders.add(m.group());
            }
            for(String placeholder : placeholders)
            {
                if(placeholder.startsWith("{{result."))
                {
                    if(placeholder.equals("{{result.statusCode}}"))
                    {
                        updatedLog = StringUtils.injectValue(updatedLog, "result.statusCode", Integer.toString(step.result.statusCode));
                    }
                    else if(placeholder.equals("{{result.body}}"))
                    {
                        updatedLog = StringUtils.injectValue(updatedLog, "result.body", step.result.body);
                    }
                    else if(placeholder.equals("{{result.headers}}"))
                    {
                        updatedLog = StringUtils.injectValue(updatedLog, "result.headers", step.result.headers.toString());
                    }
                    if(placeholder.startsWith("{{result.headers."))
                    {
                        String[] headerVarParts = placeholder.split("\\.");
                        String headerValue = step.result.headers.get(headerVarParts[headerVarParts.length - 1]);
                        updatedLog = StringUtils.injectValue(updatedLog, placeholder.substring(2, placeholder.length() - 2), headerValue);
                    }
                    for(Map.Entry<String, String> testCaseResultVar : step.result.result.entrySet())
                    {
                        if(testCaseResultVar.getKey().equals(placeholder.substring(2, placeholder.length() - 2).replace("result.", "")))
                        {
                            updatedLog = StringUtils.injectValue(updatedLog, "result." + testCaseResultVar.getKey(), testCaseResultVar.getValue());
                        }
                    }
                }
                else if(placeholder.startsWith("{{input."))
                {
                    for(Map.Entry<String, String> executorInputVar : executor.input.entrySet())
                    {
                        if(executorInputVar.getKey().equals(placeholder.substring(2, placeholder.length() - 2).replace("input.", "")))
                        {
                            updatedLog = StringUtils.injectValue(updatedLog, "input." + executorInputVar.getKey(), executorInputVar.getValue());
                        }
                    }
                }
                else if(placeholder.indexOf(".") != -1)
                {
                    String[] keyParts = placeholder.split("\\.");
                    updatedLog = StringUtils.processReplacementsInStepUsingStepsAlreadyExecuted(keyParts, updatedLog, placeholder);
                    for(Map.Entry<String, Map<String, String>> executorStepResultPlusVars : TestLIVEData.executorStepNamesThatHaveExecuted.entrySet())
                    {
                        if(keyParts.length == 2 && executorStepResultPlusVars.getKey().equals(keyParts[0].substring(2)))
                        {
                            String replacement = executorStepResultPlusVars.getValue().get(keyParts[1].substring(0, keyParts[1].length() - 2));
                            updatedLog = StringUtils.injectValue(updatedLog, placeholder, replacement);
                        }
                    }
                }
            }
            Logger.info(updatedLog);
        }
    }
}
